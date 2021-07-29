package com.vivavichi.vivapong.ui.viva.board;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import com.vivavichi.vivapong.R;
import com.vivavichi.vivapong.ui.viva.VivaMenuActivity;

public class BoardActivity extends AppCompatActivity implements SensorEventListener {

    public static float aB1X;
    public static int height, width;
    public static boolean isUpdate;
    private SensorManager sensorManager;
    private Sensor sensor;
    private BoardView boardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //noinspection deprecation
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = (displaymetrics.heightPixels);
        width = displaymetrics.widthPixels;
        boardView = new BoardView(this, this);

        setContentView(boardView);
        isUpdate = false;
        initBoard();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void initBoard() {
        VivaMenuActivity.startTime = (System.currentTimeMillis()) / 1000;
        isUpdate = true;
        boardView.initializeBallVelocity(width, height);
    }

    public void popDialog() {
        runOnUiThread(() -> {
            VivaMenuActivity.check = 1;
            this.finish();
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        aB1X = event.values[0];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null)
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null)
            sensorManager.unregisterListener(this);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), VivaMenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (sensorManager != null)
            sensorManager.unregisterListener(this);
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.finish();
    }

    public static class RenderThread extends Thread {

        private final SurfaceHolder surfaceHolder;
        private final BoardView pongPlayerView;
        private boolean isRunning = false;

        public RenderThread(SurfaceHolder surfaceHolder, BoardView pongPlayerView) {
            this.surfaceHolder = surfaceHolder;
            this.pongPlayerView = pongPlayerView;
        }

        public void setRunning(boolean running) {
            isRunning = running;
        }

        @SuppressLint("WrongCall")
        @Override
        public void run() {
            Canvas canvas;

            while (isRunning) {
                pongPlayerView.update(isUpdate);
                canvas = null;
                try {
                    canvas = surfaceHolder.lockCanvas(null);

                    synchronized (surfaceHolder) {
                        if (canvas != null)
                            pongPlayerView.onDraw(canvas);
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }
}