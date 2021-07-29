package com.vivavichi.vivapong.ui.viva.board;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.vivavichi.vivapong.R;
import com.vivavichi.vivapong.ui.viva.VivaMenuActivity;
import com.vivavichi.vivapong.ui.viva.board.items.Ball;
import com.vivavichi.vivapong.ui.viva.board.items.BoardOne;
import com.vivavichi.vivapong.ui.viva.board.items.BoardTwo;

import java.util.Random;

public class BoardView extends SurfaceView implements SurfaceHolder.Callback, Ball, BoardOne, BoardTwo {


    public static int radius;
    public static int boardWidth1;
    public static float vBallX, vBallY;

    private final int boardWidth2 = VivaMenuActivity.width / 5;
    private final int boardHeight = 120;
    private final float dT = 0.3f;
    private final Paint paintBall;
    private final Paint textPaint;
    private final Paint circlePaint;
    private final BoardActivity boardActivity;
    private final RectF rectFB1;
    private final RectF rectFB2;
    private final RectF rectFInvisibleB1;
    private final RectF rectFInvisibleB2;
    private final BoardActivity.RenderThread renderThread;
    private float vB1X;

    public static int xBallCenter, yBallCenter;
    public static int xSecondBallCenter, ySecondBallCenter;

    private int xB1Center, yB1Center;
    private int xB2Center, yB2Center;
    private final int[] ballDirection = new int[]{-1, 1};
    private final Bitmap leftButton;
    private final Bitmap rightButton;
    public static int numberOfHits = 1;
    public static int score = 0;
    private int touchPosition;
    private Bitmap backgroundBitmap;
    private final Bitmap scaledBitmap;
    private boolean touchAction;
    private boolean tokenToUpdateB2Center = false;

    private final Bitmap ballBitmap;
    private final Bitmap manOne;
    private final Bitmap manTwo;


    public BoardView(Context context, BoardActivity boardActivity) {
        super(context);
        this.boardActivity = boardActivity;

        getHolder().addCallback(this);
        renderThread = new BoardActivity.RenderThread(getHolder(), this);
        setFocusable(true);
        boardWidth1 = (VivaMenuActivity.width) / 5;
        radius = VivaMenuActivity.width / 25;

        paintBall = new Paint();
        paintBall.setColor(0xFFFF4081);
        paintBall.setAlpha(255);
        paintBall.setStyle(Paint.Style.FILL);
        paintBall.setAntiAlias(true);

        Paint paintInvisible = new Paint();
        paintInvisible.setColor(0xFFFFFFFF);
        paintInvisible.setAlpha(0);
        paintInvisible.setStyle(Paint.Style.FILL);
        paintInvisible.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setAlpha(255);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.BLACK);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize((float) VivaMenuActivity.width / 10);

        circlePaint = new Paint();
        circlePaint.setColor(Color.WHITE);
        circlePaint.setAlpha(255);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setAntiAlias(true);

        ballBitmap = getBitmap(context, R.drawable.ic_ball);
        manOne = getBitmap(context, R.drawable.ic_player_one);
        manTwo = getBitmap(context, R.drawable.ic_player_two);

        rectFB1 = new RectF();
        rectFB2 = new RectF();
        rectFInvisibleB1 = new RectF();
        rectFInvisibleB2 = new RectF();

        setBoardOneAtCenter(VivaMenuActivity.width / 2, VivaMenuActivity.height);
        setBoardTwoAtCenter(VivaMenuActivity.width / 2, 0);
        initializeBallPosition(VivaMenuActivity.width, VivaMenuActivity.height);
        initializeSecondBallPosition();
        leftButton = BitmapFactory.decodeResource(getResources(), R.drawable.left_button);
        rightButton = BitmapFactory.decodeResource(getResources(), R.drawable.right_button);
        takeBitmap();
        scaledBitmap = Bitmap.createScaledBitmap(backgroundBitmap, VivaMenuActivity.width
                , VivaMenuActivity.height, true);
    }

    private Bitmap getBitmap(Context context, int drawableId){
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        assert drawable != null;
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private void initializeSecondBallPosition() {
        xSecondBallCenter = VivaMenuActivity.width / 2;
        ySecondBallCenter = VivaMenuActivity.height / 2;
    }

    public void update(boolean check) {
        if (check) {
            updateB1Center();
            updateBall();
            smartUpdateB2Center();
        }
    }

    public void smartUpdateB2Center() {
        if (vBallY < 0 && yBallCenter < 4 * VivaMenuActivity.height / 5) {
            if (xBallCenter >= xB2Center - boardWidth2 / 6 && xBallCenter <= xB2Center + boardWidth2 / 6) {
                tokenToUpdateB2Center = true;
            }
            updateB2Center(tokenToUpdateB2Center);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touchPosition = (int) event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchAction = true;
                break;
            case MotionEvent.ACTION_UP:
                touchAction = false;
                break;
        }
        return true;
    }

    public void takeBitmap() {
//        int[] number = new int[]{1, 2};
//        int index = (number[new Random().nextInt(number.length)]);
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inMutable = true;
        backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.viva_back, opt);
        textPaint.setColor(0xFF605E5F);
        /*switch (index) {
            case 1:
                backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.viva_back, opt);
                textPaint.setColor(0xFF605E5F);
                break;
            case 2:
                backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.back2, opt);
                textPaint.setColor(0xFF3F8522);
                break;
        }*/
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        renderThread.setRunning(true);
        renderThread.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        renderThread.setRunning(false);
    }

    @Override
    public void initializeBallPosition(int x, int y) {
        score = 0;
        xBallCenter = x / 2;
        yBallCenter = y / 2;
    }

    @Override
    public void initializeBallVelocity(int x, int y) {
        vBallX = (float) (ballDirection[new Random().nextInt(ballDirection.length)]) * x / 25;
        vBallY = (float) -y / (25 + 9);
    }

    @Override
    public void updateBall() {
        long time = (System.currentTimeMillis() / 1000) - VivaMenuActivity.startTime + 1;

        if (time % 20 == 0) {
            vBallY = velocityBooster(vBallY);
        }
        xBallCenter += vBallX * dT;
        yBallCenter += vBallY * dT;

        if (xBallCenter < radius) {
            xBallCenter = radius;
            vBallX = -vBallX;
        } else if (xBallCenter > VivaMenuActivity.width - radius) {
            xBallCenter = VivaMenuActivity.width - radius;
            vBallX = -vBallX;
        } else if (yBallCenter < radius) {
            yBallCenter = radius;
            vBallY = -vBallY;
        } else if (yBallCenter > VivaMenuActivity.height) {
            boardActivity.popDialog();
        }
    }

    @Override
    public void collide(int x) {
        if (x == 1) {
            numberOfHits++;
            score++;
            yBallCenter = VivaMenuActivity.height - (4 * boardHeight / 2) - radius;
            vBallY = -vBallY;
            if (Math.abs(xBallCenter - xB1Center) < (Math.abs(radius))) {
                vBallX = (float) (vBallX / 1.005);
            } else {
                vBallX = (float) (1.05 * vBallX);
            }

        } else if (x == 2) {
            yBallCenter = radius + (4 * boardHeight / 2);
            vBallY = -vBallY;
            if ((xB2Center < VivaMenuActivity.width / 3 && vBallX > 0) || (xB2Center > VivaMenuActivity.width / 3 && vBallX < 0)) {
                vBallX = -vBallX;
            }
            tokenToUpdateB2Center = false;
        }
    }

    @Override
    public float velocityBooster(float velocity) {
        return (float) (velocity * (1 + (1 * 0.004)));
    }

    @Override
    public void setBoardOneAtCenter(int x, int y) {
        xB1Center = x;
        yB1Center = (y - boardHeight);
    }

    @Override
    public void updateB1Center() {
        if (VivaMenuActivity.sensorMode) {
            if (Math.abs(BoardActivity.aB1X) < 1) {
                vB1X = 0;
            } else {
                if (BoardActivity.aB1X < 0) {
                    vB1X = (float) VivaMenuActivity.width / 25;
                } else {
                    vB1X = (float) -VivaMenuActivity.width / 25;
                }
            }
        } else {
            if (touchAction) {
                if (touchPosition < VivaMenuActivity.width / 2) {
                    vB1X = (float) -VivaMenuActivity.width / 25;
                } else if (touchPosition > VivaMenuActivity.width / 2) {
                    vB1X = (float) VivaMenuActivity.width / 25;
                }
            } else {
                vB1X = 0;
            }

        }
        xB1Center += (int) (vB1X * dT);
        if (xB1Center < boardWidth1 / 2) {
            xB1Center = boardWidth1 / 2;
            vB1X = 0;
        }
        if (xB1Center > VivaMenuActivity.width - (boardWidth1 / 2)) {
            xB1Center = (VivaMenuActivity.width - (boardWidth1 / 2));
            vB1X = 0;
        }
    }

    @Override
    public void setBoardTwoAtCenter(int x, int y) {
        xB2Center = x;
        yB2Center = (y + boardHeight);
    }

    @Override
    public void updateB2Center(boolean token) {
        if (token) {
            xB2Center = xBallCenter;
            if (xB2Center < boardWidth2 / 2) {
                xB2Center = boardWidth2 / 2;
            }
            if (xB2Center > VivaMenuActivity.width - (boardWidth2 / 2)) {
                xB2Center = (VivaMenuActivity.width - (boardWidth2 / 2));
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(scaledBitmap, 0, 0, null);
        canvas.drawCircle((float) VivaMenuActivity.width / 2, (float) VivaMenuActivity.height / 2, (float) VivaMenuActivity.width / 8
                , circlePaint);

        canvas.drawText(String.valueOf(score), (float) VivaMenuActivity.width / 2,
                (float) VivaMenuActivity.height / 2 + (float) VivaMenuActivity.width / 40, textPaint);

        if (VivaMenuActivity.showButtons) {
            canvas.drawBitmap(leftButton, 10, (VivaMenuActivity.height - ((float) VivaMenuActivity.height / 8)), paintBall);
            canvas.drawBitmap(rightButton, (VivaMenuActivity.width - ((float) VivaMenuActivity.width / 5)),
                    (VivaMenuActivity.height - ((float) VivaMenuActivity.height / 8)), paintBall);
        }

        if (rectFB1 != null) {
            rectFB1.set(xB1Center - (
                    (float) boardWidth1 / 2), yB1Center + ((float) boardHeight / 2), xB1Center + ((float) boardWidth1 / 2), yB1Center - ((float) boardHeight / 2));
            rectFInvisibleB1.set(xB1Center - ((float) boardWidth1 / 2), yB1Center - (boardHeight), xB1Center + ((float) boardWidth1 / 2), yB1Center - ((float) 3 * boardHeight / 2));
            canvas.drawBitmap(manOne, rectFInvisibleB1.left - (float) boardWidth1/4, rectFInvisibleB1.top - (float) (2 * boardHeight / 2), null);
        }

        if (rectFB2 != null) {
            rectFB2.set(xB2Center - (float) (boardWidth2 / 2), yB2Center + (float) (boardHeight / 2), xB2Center + (float) (boardWidth2 / 2), yB2Center - (float) (boardHeight / 2));
            rectFInvisibleB2.set(xB2Center - (float) (boardWidth2 / 2), yB2Center + (float) (3 * boardHeight / 2), xB2Center + (float) (boardWidth2 / 2), yB2Center + (float) (boardHeight / 2));
            canvas.drawBitmap(manTwo, rectFInvisibleB2.left - (float) (boardWidth2 / 4), rectFInvisibleB2.top - (float) (6 * boardHeight / 2), null);
        }
        if (Ball.rectFBall != null) {
            Ball.rectFBall.set(xBallCenter - radius, yBallCenter - radius, xBallCenter + radius, yBallCenter + radius);
            canvas.drawBitmap(ballBitmap, null, Ball.rectFBall, paintBall);
        }

        if (rectFB1 != null) {
            if (rectFBall.intersect(rectFInvisibleB1)) {
                collide(1);
            } else if (rectFBall.intersect(rectFInvisibleB2)) {
                collide(2);
            }
        }
    }
}
