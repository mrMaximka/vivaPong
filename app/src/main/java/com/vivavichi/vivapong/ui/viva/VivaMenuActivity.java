package com.vivavichi.vivapong.ui.viva;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.vivavichi.vivapong.R;
import com.vivavichi.vivapong.databinding.ActivityVivaMenuBinding;
import com.vivavichi.vivapong.model.ResultModel;
import com.vivavichi.vivapong.ui.viva.board.BoardActivity;
import com.vivavichi.vivapong.ui.viva.board.BoardView;
import com.vivavichi.vivapong.ui.viva.result.VivaResultActivity;
import com.vivavichi.vivapong.ui.viva.result.db.DbHelper;
import com.vivavichi.vivapong.ui.viva.result.db.DbTable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class VivaMenuActivity extends AppCompatActivity {

    ActivityVivaMenuBinding binding;
    public static long startTime;
    public static int check;
    public static int height, width;

    private SQLiteDatabase database;
    ArrayList<ResultModel> dbModelArrayList;
    DbTable dbTable = new DbTable();
    private int winScorePos = -1;

    public static boolean sensorMode = false;
    public static boolean showButtons = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVivaMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setBackgroundDrawableResource(R.drawable.menu_back);
        //noinspection deprecation
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        check = 0;

        DisplayMetrics displaymetrics = new DisplayMetrics();
        //noinspection deprecation
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = (displaymetrics.heightPixels) ;
        width = displaymetrics.widthPixels;
        database = new DbHelper(getApplicationContext()).getWritableDatabase();

        binding.oynaSensor.setOnClickListener(v -> onSensorButtonClicked());

        binding.oynaExit.setOnClickListener(v -> onBackPressed());

        binding.oynaStart.setOnClickListener(v -> onPlayClicked());

        binding.oynaResult.setOnClickListener(v -> onResultClicked());
    }

    private void onResultClicked() {
        Intent i = new Intent(getApplicationContext(), VivaResultActivity.class);
        i.addFlags(android.content.Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
    }

    private void onPlayClicked() {
        Intent i = new Intent(getApplicationContext(), BoardActivity.class);
        i.addFlags(android.content.Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
    }

    private void onSensorButtonClicked() {
        sensorMode = !sensorMode;
        showButtons = !showButtons;
        if (sensorMode) {
            binding.oynaSensor.setText("Наклон");
            Toast.makeText(this, "Управление наклоном включено", Toast.LENGTH_SHORT).show();
        } else {
            binding.oynaSensor.setText("Кнопки");
            Toast.makeText(this, "Управление кнопками включено", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        dialog(check);
    }

    @Override
    protected void onPause() {
        super.onPause();
        check--;
    }

    public void dialog(int param) {
        if (param == 1) {
            long score = BoardView.score;
            String result = "Ваши очки: " + score;

            final Dialog d = new Dialog(this);
            d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            d.requestWindowFeature(Window.FEATURE_NO_TITLE);
            d.setContentView(R.layout.dialog_result);
            d.show();
            d.setCancelable(true);

            dbModelArrayList = dbTable.loadAllResults(database);
            checkToUpdateDb(score);

            Button reloadBtn = d.findViewById(R.id.dialog_result_reload);
            Button closeBtn = d.findViewById(R.id.dialog_result_close);

            TextView resultTxt = d.findViewById(R.id.dialog_result_score);

            resultTxt.setText(result);

            reloadBtn.setOnClickListener(v -> {
                d.dismiss();
                onPlayClicked();
            });
            closeBtn.setOnClickListener(v -> d.dismiss());
        }
    }

    private void checkToUpdateDb(long score) {
        for (int i = 0; i < dbModelArrayList.size(); i++) {
            if (score >= dbModelArrayList.get(i).getScore()){
                winScorePos = i;
                updateResultList(score);
                return;
            }
        }
    }

    private void updateResultList(long score) {
        String currentDate = new SimpleDateFormat("dd-MM-yyyy \t HH:mm", Locale.getDefault()).format(new Date());
        for (int i = (dbModelArrayList.size() - 1); i >= (winScorePos + 1); i--) {
            dbModelArrayList.get(i).setScore(dbModelArrayList.get(i - 1).getScore());
            dbModelArrayList.get(i).setDate(dbModelArrayList.get(i - 1).getDate());
        }
        dbModelArrayList.get(winScorePos).setScore(score);
        dbModelArrayList.get(winScorePos).setDate(currentDate);
        updateResult();
    }

    private void updateResult() {
        for (int i = winScorePos; i < dbModelArrayList.size(); i++) {
            dbTable.updateResult(database, dbModelArrayList.get(i).getId(),
                    dbModelArrayList.get(i).getDate(), dbModelArrayList.get(i).getScore());
        }
    }
}