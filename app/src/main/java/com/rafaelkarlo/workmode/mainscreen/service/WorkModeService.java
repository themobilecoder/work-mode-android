package com.rafaelkarlo.workmode.mainscreen.service;

import android.content.SharedPreferences;
import android.media.AudioManager;

public class WorkModeService {

    private static final String WORK_MODE_ACTIVATED = "WORK_MODE_ACTIVATED";

    private AudioManager audioManager;
    private SharedPreferences sharedPreferences;

    public WorkModeService(AudioManager audioManager, SharedPreferences sharedPreferences) {
        this.audioManager = audioManager;
        this.sharedPreferences = sharedPreferences;
    }

    public boolean setToSilentMode() {
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        return audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT;
    }

    public boolean setToNormalMode() {
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        return audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
    }

    public void activate() {
        saveModeActivated();
    }

    public void deactivate() {
        saveModeDeactivated();
    }

    public boolean isActivated() {
        return sharedPreferences.getBoolean(WORK_MODE_ACTIVATED, false);
    }

    private void saveModeActivated() {
        saveInSharedPreferences(true);
    }

    private void saveModeDeactivated() {
        saveInSharedPreferences(false);
    }

    private void saveInSharedPreferences(boolean activated) {
        sharedPreferences.edit().putBoolean("WORK_MODE_ACTIVATED", activated).apply();
    }
}
