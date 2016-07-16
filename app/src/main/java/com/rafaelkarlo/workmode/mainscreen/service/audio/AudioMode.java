package com.rafaelkarlo.workmode.mainscreen.service.audio;

public enum AudioMode {
    NORMAL(2),
    VIBRATE(1),
    SILENT(0),
    UNKNOWN(-1);

    private int value;

    AudioMode(int intValue) {
        this.value = intValue;
    }

    int getValue() {
        return value;
    }
}
