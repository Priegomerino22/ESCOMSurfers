package com.escom.escomsurfers;

public class StoryMessage {
    private final int triggerSecond;
    private final String speaker;
    private final String text;

    public StoryMessage(int triggerSecond, String speaker, String text) {
        this.triggerSecond = triggerSecond;
        this.speaker = speaker;
        this.text = text;
    }

    public int getTriggerSecond() {
        return triggerSecond;
    }

    public String getSpeaker() {
        return speaker;
    }

    public String getText() {
        return text;
    }
}
