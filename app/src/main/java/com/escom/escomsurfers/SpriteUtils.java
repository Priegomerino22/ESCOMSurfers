package com.escom.escomsurfers;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

public final class SpriteUtils {

    private static final ColorMatrixColorFilter SPRITE_FILTER = createSpriteFilter();

    private SpriteUtils() {
    }

    public static Bitmap decodeTrimmed(Resources resources, int resId) {
        Bitmap bitmap = BitmapFactory.decodeResource(resources, resId);

        if (bitmap == null) {
            return null;
        }

        return trimTransparent(bitmap);
    }

    public static Bitmap trimTransparent(Bitmap source) {
        if (source == null) {
            return null;
        }

        int width = source.getWidth();
        int height = source.getHeight();
        int[] pixels = new int[width * height];
        source.getPixels(pixels, 0, width, 0, 0, width, height);

        int minX = width;
        int minY = height;
        int maxX = -1;
        int maxY = -1;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = pixels[y * width + x];
                int alpha = (pixel >>> 24) & 0xff;

                if (alpha > 12) {
                    if (x < minX) {
                        minX = x;
                    }
                    if (x > maxX) {
                        maxX = x;
                    }
                    if (y < minY) {
                        minY = y;
                    }
                    if (y > maxY) {
                        maxY = y;
                    }
                }
            }
        }

        if (maxX < minX || maxY < minY) {
            return source;
        }

        int paddingX = Math.max(2, width / 100);
        int paddingY = Math.max(2, height / 100);

        minX = Math.max(0, minX - paddingX);
        minY = Math.max(0, minY - paddingY);
        maxX = Math.min(width - 1, maxX + paddingX);
        maxY = Math.min(height - 1, maxY + paddingY);

        int croppedWidth = maxX - minX + 1;
        int croppedHeight = maxY - minY + 1;

        if (croppedWidth <= 0 || croppedHeight <= 0 ||
                (croppedWidth == width && croppedHeight == height)) {
            return source;
        }

        return Bitmap.createBitmap(source, minX, minY, croppedWidth, croppedHeight);
    }

    public static void applySpritePaint(Paint paint) {
        if (paint == null) {
            return;
        }

        paint.setAlpha(255);
        paint.setFilterBitmap(true);
        paint.setAntiAlias(true);
        paint.setColorFilter(SPRITE_FILTER);
    }

    private static ColorMatrixColorFilter createSpriteFilter() {
        // Filtro suave: sube un poco color y luz, pero sin quemar los PNG.
        // La versión anterior usaba el canal alpha como brillo y por eso todo se veía blanco.
        ColorMatrix saturation = new ColorMatrix();
        saturation.setSaturation(1.06f);

        ColorMatrix brightness = new ColorMatrix(new float[]{
                1.02f, 0f, 0f, 0f, 4f,
                0f, 1.02f, 0f, 0f, 4f,
                0f, 0f, 1.02f, 0f, 4f,
                0f, 0f, 0f, 1f, 0f
        });

        saturation.postConcat(brightness);
        return new ColorMatrixColorFilter(saturation);
    }
}
