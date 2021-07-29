package com.vivavichi.vivapong.ui.viva.board.items;

import android.graphics.RectF;

public interface Ball {
    RectF rectFBall = new RectF();

    void initializeBallPosition(int x, int y);

    void initializeBallVelocity(int x, int y);

    void updateBall();

    void collide(int x);

    float velocityBooster(float x);
}
