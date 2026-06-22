package com.escom.escomsurfers;

public final class PerspectiveHelper {

    private PerspectiveHelper() {
    }

    // Subido de 0.34 a 0.42 para que monedas/obstáculos salgan más cerca
    // y no desde tan al fondo del pasillo.
    private static final float HORIZON_Y_RATIO = 0.42f;
    private static final float BOTTOM_Y_RATIO = 0.915f;

    private static final float LEFT_DIVIDER_TOP_RATIO = 0.474f;
    private static final float RIGHT_DIVIDER_TOP_RATIO = 0.526f;

    private static final float LEFT_DIVIDER_BOTTOM_RATIO = 0.332f;
    private static final float RIGHT_DIVIDER_BOTTOM_RATIO = 0.668f;

    private static final float LEFT_OUTER_TOP_RATIO = 0.410f;
    private static final float RIGHT_OUTER_TOP_RATIO = 0.590f;

    private static final float LEFT_OUTER_BOTTOM_RATIO = 0.150f;
    private static final float RIGHT_OUTER_BOTTOM_RATIO = 0.850f;

    public static float getHorizonY(int screenHeight) {
        return screenHeight * HORIZON_Y_RATIO;
    }

    public static float getBottomY(int screenHeight) {
        return screenHeight * BOTTOM_Y_RATIO;
    }

    public static float getPlayerGroundY(int screenHeight) {
        return screenHeight * 0.915f;
    }

    public static float clamp(float value, float min, float max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    public static float lerp(float start, float end, float t) {
        return start + (end - start) * t;
    }

    public static float progressForY(float y, int screenHeight) {
        float top = getHorizonY(screenHeight);
        float bottom = getBottomY(screenHeight);

        if (bottom <= top) {
            return 0f;
        }

        return clamp((y - top) / (bottom - top), 0f, 1f);
    }

    public static float scaleForY(float y, int screenHeight, float minScale, float maxScale) {
        float t = progressForY(y, screenHeight);
        t = (float) Math.pow(t, 0.92f);
        return lerp(minScale, maxScale, t);
    }

    public static float getLeftDividerX(float y, int screenWidth, int screenHeight) {
        float t = progressForY(y, screenHeight);
        return lerp(screenWidth * LEFT_DIVIDER_TOP_RATIO, screenWidth * LEFT_DIVIDER_BOTTOM_RATIO, t);
    }

    public static float getRightDividerX(float y, int screenWidth, int screenHeight) {
        float t = progressForY(y, screenHeight);
        return lerp(screenWidth * RIGHT_DIVIDER_TOP_RATIO, screenWidth * RIGHT_DIVIDER_BOTTOM_RATIO, t);
    }

    public static float getLeftOuterX(float y, int screenWidth, int screenHeight) {
        float t = progressForY(y, screenHeight);
        return lerp(screenWidth * LEFT_OUTER_TOP_RATIO, screenWidth * LEFT_OUTER_BOTTOM_RATIO, t);
    }

    public static float getRightOuterX(float y, int screenWidth, int screenHeight) {
        float t = progressForY(y, screenHeight);
        return lerp(screenWidth * RIGHT_OUTER_TOP_RATIO, screenWidth * RIGHT_OUTER_BOTTOM_RATIO, t);
    }

    public static float getLaneCenterX(int lane, float y, int screenWidth, int screenHeight) {
        float leftOuter = getLeftOuterX(y, screenWidth, screenHeight);
        float leftDivider = getLeftDividerX(y, screenWidth, screenHeight);
        float rightDivider = getRightDividerX(y, screenWidth, screenHeight);
        float rightOuter = getRightOuterX(y, screenWidth, screenHeight);

        if (lane == 0) {
            return (leftOuter + leftDivider) / 2f;
        } else if (lane == 1) {
            return (leftDivider + rightDivider) / 2f;
        } else {
            return (rightDivider + rightOuter) / 2f;
        }
    }
}
