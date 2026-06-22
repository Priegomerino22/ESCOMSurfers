package com.escom.escomsurfers;

import android.content.Context;
import android.content.SharedPreferences;

public class StoryProgressManager {
    private static final String PREFS_NAME = "ESCOM_SURFERS_STORY";
    private static final String UNLOCKED_LEVEL_KEY = "UNLOCKED_LEVEL";

    private final SharedPreferences preferences;

    public StoryProgressManager(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public int getUnlockedLevel() {
        return preferences.getInt(UNLOCKED_LEVEL_KEY, 1);
    }

    public boolean isLevelUnlocked(int levelIndex) {
        return levelIndex <= getUnlockedLevel();
    }

    public void completeLevel(int levelIndex) {
        int currentUnlocked = getUnlockedLevel();
        int nextUnlocked = Math.min(StoryManager.getLevelCount(), levelIndex + 1);

        if (levelIndex >= currentUnlocked && currentUnlocked < StoryManager.getLevelCount()) {
            preferences.edit().putInt(UNLOCKED_LEVEL_KEY, nextUnlocked).apply();
        }
    }

    public void resetProgress() {
        preferences.edit().putInt(UNLOCKED_LEVEL_KEY, 1).apply();
    }
}
