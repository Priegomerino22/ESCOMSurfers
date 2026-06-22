package com.escom.escomsurfers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class PowerUp {

    public static final int TYPE_COFFEE = 0;
    public static final int TYPE_VPN = 1;

    private final int lane;
    private final int type;
    private final int screenWidth;
    private final int screenHeight;

    private float y;
    private float centerX;
    private float size;

    // un poquito más grande
    private final float baseSize;

    public PowerUp(int lane, int type, int screenWidth, int screenHeight) {
        this.lane = lane;
        this.type = type;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        baseSize = screenWidth * 0.152f;
        y = PerspectiveHelper.getHorizonY(screenHeight) - screenHeight * 0.012f;

        updateLayout();
    }

    private void updateLayout() {
        centerX = PerspectiveHelper.getLaneCenterX(lane, y, screenWidth, screenHeight);

        float scale = PerspectiveHelper.scaleForY(y, screenHeight, 0.50f, 1.66f);
        size = baseSize * scale;
    }

    public void update(float speed) {
        y += speed * 1.30f;
        updateLayout();
    }

    public void draw(Canvas canvas, Paint paint, Bitmap coffeeImage, Bitmap vpnImage) {
        updateLayout();

        paint.setAlpha(255);

        RectF rect = new RectF(
                centerX - size / 2f,
                y - size / 2f,
                centerX + size / 2f,
                y + size / 2f
        );

        Bitmap selectedImage = null;

        if (type == TYPE_COFFEE) {
            selectedImage = coffeeImage;
        } else if (type == TYPE_VPN) {
            selectedImage = vpnImage;
        }

        if (selectedImage != null) {
            Paint glowPaint = new Paint(paint);
            glowPaint.setStyle(Paint.Style.FILL);
            glowPaint.setColor(type == TYPE_COFFEE ? Color.argb(28, 255, 190, 90) : Color.argb(30, 95, 225, 255));
            glowPaint.setColorFilter(null);
            canvas.drawOval(
                    centerX - size * 0.62f,
                    y - size * 0.46f,
                    centerX + size * 0.62f,
                    y + size * 0.46f,
                    glowPaint
            );

            SpriteUtils.applySpritePaint(paint);
            canvas.drawBitmap(selectedImage, null, rect, paint);
            paint.setColorFilter(null);
            return;
        }

        paint.setStyle(Paint.Style.FILL);

        if (type == TYPE_COFFEE) {
            paint.setColor(Color.rgb(150, 85, 35));
            canvas.drawOval(rect, paint);

            paint.setColor(Color.WHITE);
            paint.setTextSize(size * 0.26f);
            paint.setFakeBoldText(true);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("CAFÉ", centerX, y + size * 0.10f, paint);

        } else {
            paint.setColor(Color.rgb(0, 150, 255));
            canvas.drawOval(rect, paint);

            paint.setColor(Color.WHITE);
            paint.setTextSize(size * 0.30f);
            paint.setFakeBoldText(true);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("VPN", centerX, y + size * 0.10f, paint);
        }

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setAlpha(255);
    }

    public boolean isOffScreen(int screenHeight) {
        return y - size / 2f > screenHeight + 50;
    }

    public RectF getCollisionRect() {
        return new RectF(
                centerX - size * 0.32f,
                y - size * 0.32f,
                centerX + size * 0.32f,
                y + size * 0.32f
        );
    }

    public int getType() {
        return type;
    }

    public int getLane() {
        return lane;
    }

    public float getY() {
        return y;
    }
}
