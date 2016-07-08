package com.rafaelkarlo.workmode.mainscreen.service;

import android.content.SharedPreferences;
import android.media.AudioManager;

import org.joda.time.DateTime;

import static org.joda.time.DateTime.now;

public class WorkModeService {

    private static final String WORK_MODE_ACTIVATED = "WORK_MODE_ACTIVATED";

    private AudioManager audioManager;
    private SharedPreferences sharedPreferences;

    public WorkModeService(AudioManager audioManager, SharedPreferences sharedPreferences) {
        this.audioManager = audioManager;
        this.sharedPreferences = sharedPreferences;
    }

    public boolean setToSilentMode() {
        if (nowIsWithinWorkHours()) {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            return true;
        } else {
            return false;
        }
    }

    public boolean setToNormalMode() {
        if (nowIsAfterWorkHours()) {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            return true;
        } else {
            return false;
        }
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

    private boolean nowIsWithinWorkHours() {
        int startTimeInSecondsOfDay = sharedPreferences.getInt("WORK_START_TIME", 0);
        int endTimeInSecondsOfDay = sharedPreferences.getInt("WORK_END_TIME", 0);
        DateTime now = now();

        return startTimeInSecondsOfDay <= now.getSecondOfDay() && now.getSecondOfDay() <= endTimeInSecondsOfDay;
    }

    private boolean nowIsAfterWorkHours() {
        int endTimeInSecondsOfDay = sharedPreferences.getInt("WORK_END_TIME", 0);
        return now().getSecondOfDay() >= endTimeInSecondsOfDay;
    }

    private void saveModeActivated() {
        saveInSharedPreferences(true);
    }

    private void saveModeDeactivated() {
        saveInSharedPreferences(false);
    }

    private void saveInSharedPreferences(boolean activated) {
        sharedPreferences.edit().putBoolean(WORK_MODE_ACTIVATED, activated).apply();
    }
}
