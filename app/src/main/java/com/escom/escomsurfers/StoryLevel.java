package com.escom.escomsurfers;

public class StoryLevel {
    private final int index;
    private final String title;
    private final String subtitle;
    private final long durationMs;
    private final String difficulty;
    private final int backgroundResId;
    private final int[] obstacleResIds;
    private final DialogueLine[] introLines;
    private final DialogueLine[] outroLines;
    private final StoryMessage[] inGameMessages;

    public StoryLevel(
            int index,
            String title,
            String subtitle,
            long durationMs,
            String difficulty,
            int backgroundResId,
            int[] obstacleResIds,
            DialogueLine[] introLines,
            DialogueLine[] outroLines,
            StoryMessage[] inGameMessages
    ) {
        this.index = index;
        this.title = title;
        this.subtitle = subtitle;
        this.durationMs = durationMs;
        this.difficulty = difficulty;
        this.backgroundResId = backgroundResId;
        this.obstacleResIds = obstacleResIds;
        this.introLines = introLines;
        this.outroLines = outroLines;
        this.inGameMessages = inGameMessages;
    }

    public int getIndex() {
        return index;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public int getBackgroundResId() {
        return backgroundResId;
    }

    public int[] getObstacleResIds() {
        return obstacleResIds;
    }

    public DialogueLine[] getIntroLines() {
        return introLines;
    }

    public DialogueLine[] getOutroLines() {
        return outroLines;
    }

    public StoryMessage[] getInGameMessages() {
        return inGameMessages;
    }

    public String getDurationText() {
        long totalSeconds = durationMs / 1000L;
        long minutes = totalSeconds / 60L;
        long seconds = totalSeconds % 60L;
        return String.format("%d:%02d", minutes, seconds);
    }
}
