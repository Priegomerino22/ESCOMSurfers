package com.escom.escomsurfers;

import android.content.Context;
import android.content.SharedPreferences;

public class ScoreManager {

    private static final String PREFS_NAME = "ESCOM_SURFERS_PREFS";
    private static final String HIGH_SCORE_KEY = "HIGH_SCORE";

    private final SharedPreferences preferences;

    public ScoreManager(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public int getHighScore() {
        return preferences.getInt(HIGH_SCORE_KEY, 0);
    }

    public void saveHighScore(int score) {
        int currentHighScore = getHighScore();

        if (score > currentHighScore) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(HIGH_SCORE_KEY, score);
            editor.apply();
        }
    }
}
