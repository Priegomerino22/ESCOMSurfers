package com.escom.escomsurfers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class Obstacle {

    public static final int TYPE_BUG = 0;
    public static final int TYPE_EXAM = 1;
    public static final int TYPE_HOMEWORK = 2;

    private final int lane;
    private final int type;
    private final int screenWidth;
    private final int screenHeight;

    private float y;
    private float centerX;
    private float width;
    private float height;

    // un poquito más grande
    private final float baseWidth;
    private final float baseHeight;

    public Obstacle(int lane, int type, int screenWidth, int screenHeight) {
        this.lane = lane;
        this.type = type;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        baseWidth = screenWidth * 0.168f;
        baseHeight = screenWidth * 0.168f;

        y = PerspectiveHelper.getHorizonY(screenHeight) - screenHeight * 0.01f;

        updateLayout();
    }

    private void updateLayout() {
        centerX = PerspectiveHelper.getLaneCenterX(lane, y, screenWidth, screenHeight);

        float scale = PerspectiveHelper.scaleForY(y, screenHeight, 0.54f, 1.96f);

        width = baseWidth * scale;
        height = baseHeight * scale;
    }

    public void update(float speed) {
        y += speed * 1.28f;
        updateLayout();
    }

    public void draw(Canvas canvas, Paint paint, Bitmap bugImage, Bitmap examImage, Bitmap homeworkImage) {
        updateLayout();

        paint.setAlpha(255);

        RectF rect = new RectF(
                centerX - width / 2f,
                y - height / 2f,
                centerX + width / 2f,
                y + height / 2f
        );

        Bitmap selectedImage = null;

        if (type == TYPE_BUG) {
            selectedImage = bugImage;
        } else if (type == TYPE_EXAM) {
            selectedImage = examImage;
        } else if (type == TYPE_HOMEWORK) {
            selectedImage = homeworkImage;
        }

        if (selectedImage != null) {
            Paint glowPaint = new Paint(paint);
            glowPaint.setStyle(Paint.Style.FILL);
            glowPaint.setColor(type == TYPE_BUG
                    ? Color.argb(22, 255, 80, 80)
                    : (type == TYPE_EXAM ? Color.argb(22, 255, 190, 70) : Color.argb(22, 175, 120, 255)));
            glowPaint.setColorFilter(null);
            canvas.drawOval(
                    centerX - width * 0.52f,
                    y - height * 0.34f,
                    centerX + width * 0.52f,
                    y + height * 0.38f,
                    glowPaint
            );

            SpriteUtils.applySpritePaint(paint);
            canvas.drawBitmap(selectedImage, null, rect, paint);
            paint.setColorFilter(null);
            return;
        }

        paint.setStyle(Paint.Style.FILL);

        if (type == TYPE_BUG) {
            paint.setColor(Color.rgb(180, 0, 0));
            canvas.drawRoundRect(rect, 18, 18, paint);

            paint.setColor(Color.WHITE);
            paint.setTextSize(width * 0.25f);
            paint.setFakeBoldText(true);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("BUG", centerX, y + height * 0.12f, paint);

        } else if (type == TYPE_EXAM) {
            paint.setColor(Color.rgb(230, 135, 0));
            canvas.drawRoundRect(rect, 18, 18, paint);

            paint.setColor(Color.WHITE);
            paint.setTextSize(width * 0.23f);
            paint.setFakeBoldText(true);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("EXAM", centerX, y + height * 0.12f, paint);

        } else {
            paint.setColor(Color.rgb(90, 0, 130));
            canvas.drawRoundRect(rect, 18, 18, paint);

            paint.setColor(Color.WHITE);
            paint.setTextSize(width * 0.20f);
            paint.setFakeBoldText(true);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("TAREA", centerX, y + height * 0.12f, paint);
        }

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setAlpha(255);
    }

    public boolean isOffScreen(int screenHeight) {
        return y - height / 2f > screenHeight + 50;
    }

    public RectF getCollisionRect() {
        return new RectF(
                centerX - width * 0.24f,
                y - height * 0.12f,
                centerX + width * 0.24f,
                y + height * 0.30f
        );
    }

    public int getLane() {
        return lane;
    }

    public float getY() {
        return y;
    }
}
