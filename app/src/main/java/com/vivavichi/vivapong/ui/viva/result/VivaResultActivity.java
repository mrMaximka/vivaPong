package com.vivavichi.vivapong.ui.viva.result;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.WindowManager;

import com.vivavichi.vivapong.R;
import com.vivavichi.vivapong.databinding.ActivityVivaResultBinding;
import com.vivavichi.vivapong.ui.viva.result.db.DbHelper;
import com.vivavichi.vivapong.ui.viva.result.db.DbTable;

public class VivaResultActivity extends AppCompatActivity {

    ActivityVivaResultBinding binding;
    private ResultAdapter adapter;
    private final DbTable dbTable = new DbTable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVivaResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setBackgroundDrawableResource(R.drawable.menu_back);
        //noinspection deprecation
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initResult();
        loadResultData();
    }

    private void loadResultData() {
        SQLiteDatabase database = new DbHelper(getApplicationContext()).getWritableDatabase();
        adapter.updateResult(dbTable.loadAllResults(database));
    }

    private void initResult() {
        adapter = new ResultAdapter();
        binding.vivaResultList.setAdapter(adapter);
    }
}