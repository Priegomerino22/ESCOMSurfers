package com.escom.escomsurfers;

public class DialogueLine {
    private final String speaker;
    private final String text;
    private final int characterResId;

    public DialogueLine(String speaker, String text, int characterResId) {
        this.speaker = speaker;
        this.text = text;
        this.characterResId = characterResId;
    }

    public String getSpeaker() {
        return speaker;
    }

    public String getText() {
        return text;
    }

    public int getCharacterResId() {
        return characterResId;
    }
}
