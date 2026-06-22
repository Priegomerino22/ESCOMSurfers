package com.escom.escomsurfers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class Coin {

    private final int lane;
    private final int screenWidth;
    private final int screenHeight;

    private float y;
    private float centerX;
    private float radius;

    // un poquito más grande
    private final float baseRadius;

    public Coin(int lane, int screenWidth, int screenHeight) {
        this.lane = lane;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        this.baseRadius = screenWidth * 0.049f;
        this.y = PerspectiveHelper.getHorizonY(screenHeight) - screenHeight * 0.015f;

        updateLayout();
    }

    private void updateLayout() {
        centerX = PerspectiveHelper.getLaneCenterX(lane, y, screenWidth, screenHeight);

        float scale = PerspectiveHelper.scaleForY(y, screenHeight, 0.48f, 1.62f);
        radius = baseRadius * scale;
    }

    public void update(float speed) {
        y += speed * 1.35f;
        updateLayout();
    }

    public void draw(Canvas canvas, Paint paint, Bitmap coinImage) {
        updateLayout();

        paint.setAlpha(255);

        // brillo suave
        Paint glowPaint = new Paint(paint);
        glowPaint.setStyle(Paint.Style.FILL);
        glowPaint.setColor(Color.argb(72, 255, 225, 85));
        glowPaint.setAntiAlias(true);
        canvas.drawCircle(centerX, y, radius * 1.55f, glowPaint);

        RectF rect = new RectF(
                centerX - radius,
                y - radius,
                centerX + radius,
                y + radius
        );

        if (coinImage != null) {
            SpriteUtils.applySpritePaint(paint);
            canvas.drawBitmap(coinImage, null, rect, paint);
            paint.setColorFilter(null);
            return;
        }

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(255, 215, 0));
        canvas.drawCircle(centerX, y, radius, paint);

        paint.setColor(Color.rgb(130, 88, 0));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
        canvas.drawCircle(centerX, y, radius * 0.78f, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(90, 60, 0));
        paint.setTextSize(radius * 0.85f);
        paint.setFakeBoldText(true);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("C", centerX, y + radius * 0.30f, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setAlpha(255);
    }

    public boolean isOffScreen(int screenHeight) {
        return y - radius > screenHeight + 40;
    }

    public RectF getCollisionRect() {
        return new RectF(
                centerX - radius * 0.60f,
                y - radius * 0.60f,
                centerX + radius * 0.60f,
                y + radius * 0.60f
        );
    }

    public int getLane() {
        return lane;
    }

    public float getY() {
        return y;
    }
}
