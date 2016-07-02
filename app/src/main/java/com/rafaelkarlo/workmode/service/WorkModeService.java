package com.rafaelkarlo.workmode.service;

import android.media.AudioManager;

public class WorkModeService {

    private AudioManager audioManager;

    public boolean activateWorkMode() {
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        return true;
    }

    public boolean deactivateWorkMode() {
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        return true;
    }

    public void setAudioManager(AudioManager audioManager) {
        this.audioManager = audioManager;
    }
}
