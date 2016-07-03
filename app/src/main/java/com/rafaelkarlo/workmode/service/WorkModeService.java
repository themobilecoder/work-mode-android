package com.rafaelkarlo.workmode.service;

import android.media.AudioManager;

public class WorkModeService {

    private AudioManager audioManager;

    public WorkModeService(AudioManager audioManager) {
        this.audioManager = audioManager;
    }

    public boolean activateWorkMode() {
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        return audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT;
    }

    public boolean deactivateWorkMode() {
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        return audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
    }
}
