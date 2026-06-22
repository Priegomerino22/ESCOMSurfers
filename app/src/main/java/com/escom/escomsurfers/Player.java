package com.escom.escomsurfers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class Player {

    private int lane;

    private float x;
    private float y;
    private float baseY;
    private float groundY;

    private float width;
    private float height;
    private float normalHeight;

    private final int screenWidth;
    private final int screenHeight;

    private boolean jumping;
    private boolean sliding;

    private float jumpOffset;
    private float jumpVelocity;

    private long slideStartTime;

    private static final long SLIDE_DURATION = 650;

    public Player(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        lane = 1;

        width = screenWidth * 0.255f;
        normalHeight = screenHeight * 0.172f;
        height = normalHeight;

        groundY = PerspectiveHelper.getPlayerGroundY(screenHeight);
        baseY = groundY - normalHeight;
        y = baseY;

        jumping = false;
        sliding = false;

        jumpOffset = 0;
        jumpVelocity = 0;

        updateXPosition();
    }

    public void update() {
        updateJump();
        updateSlide();
        updateXPosition();
    }

    private void updateJump() {
        if (jumping) {
            jumpOffset += jumpVelocity;
            jumpVelocity += screenHeight * 0.0018f;

            if (jumpOffset >= 0) {
                jumpOffset = 0;
                jumpVelocity = 0;
                jumping = false;
            }
        }

        y = baseY + jumpOffset;
    }

    private void updateSlide() {
        if (sliding) {
            long currentTime = System.currentTimeMillis();

            if (currentTime - slideStartTime > SLIDE_DURATION) {
                sliding = false;
                height = normalHeight;
            } else {
                height = normalHeight * 0.62f;
                y = baseY + (normalHeight - height);
            }
        } else {
            height = normalHeight;

            if (!jumping) {
                y = baseY;
            }
        }
    }

    private void updateXPosition() {
        float centerX = PerspectiveHelper.getLaneCenterX(
                lane,
                PerspectiveHelper.getBottomY(screenHeight),
                screenWidth,
                screenHeight
        );

        x = centerX - width / 2f;
    }

    public void moveLeft() {
        if (lane > 0) {
            lane--;
        }
    }

    public void moveRight() {
        if (lane < 2) {
            lane++;
        }
    }

    public void jump() {
        if (!jumping && !sliding) {
            jumping = true;
            jumpVelocity = -screenHeight * 0.025f;
            jumpOffset = -1;
        }
    }

    public void slide() {
        if (!jumping) {
            sliding = true;
            slideStartTime = System.currentTimeMillis();
        }
    }

    public void draw(Canvas canvas, Paint paint, Bitmap playerImage) {
        paint.setAlpha(255);
        paint.setColorFilter(null);
        paint.setStyle(Paint.Style.FILL);

        // Sombra + luz suave para que el personaje destaque más.
        paint.setColor(Color.argb(145, 0, 0, 0));
        canvas.drawOval(
                x + width * 0.15f,
                groundY - 7,
                x + width * 0.85f,
                groundY + 13,
                paint
        );

        paint.setColor(Color.argb(16, 255, 228, 120));
        canvas.drawOval(
                x + width * 0.05f,
                y + height * 0.10f,
                x + width * 0.95f,
                y + height * 1.02f,
                paint
        );

        RectF playerRect = new RectF(x, y, x + width, y + height);

        if (playerImage != null) {
            SpriteUtils.applySpritePaint(paint);
            canvas.drawBitmap(playerImage, null, playerRect, paint);
            paint.setColorFilter(null);
            return;
        }

        paint.setColor(Color.rgb(0, 120, 230));
        canvas.drawRoundRect(playerRect, 18, 18, paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(width * 0.18f);
        paint.setFakeBoldText(true);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("ESCOM", x + width / 2f, y + height * 0.58f, paint);

        paint.setTextAlign(Paint.Align.LEFT);
    }

    public RectF getCollisionRect() {
        // Colisión intencionalmente pequeña: solo cuenta la zona de los pies.
        // Así la cabeza y el torso no chocan con obstáculos visuales altos,
        // y al saltar el rectángulo sube con el jugador para poder esquivar.
        return new RectF(
                x + width * 0.40f,
                y + height * 0.78f,
                x + width * 0.60f,
                y + height * 0.98f
        );
    }

    public boolean isJumping() {
        return jumping;
    }

    public boolean isSliding() {
        return sliding;
    }

    public int getLane() {
        return lane;
    }
}
