package com.rafaelkarlo.workmode.mainscreen.service;

import android.content.SharedPreferences;
import android.media.AudioManager;

import org.joda.time.DateTime;

import static org.joda.time.DateTime.now;

public class WorkModeService {

    private static final String WORK_MODE_ACTIVATED = "WORK_MODE_ACTIVATED";
    public static final String WORK_START_TIME_KEY = "WORK_START_TIME";
    public static final String WORK_END_TIME_KEY = "WORK_END_TIME";

    private AudioManager audioManager;
    private SharedPreferences sharedPreferences;

    public WorkModeService(AudioManager audioManager, SharedPreferences sharedPreferences) {
        this.audioManager = audioManager;
        this.sharedPreferences = sharedPreferences;
    }

    public boolean setToSilentMode() {
        if (canSetToSilentMode()) {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            return true;
        } else {
            return false;
        }
    }

    public boolean setToNormalMode() {
        if (canSetToNormalMode()) {
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

    private boolean canSetToSilentMode() {
        return nowIsWithinWorkHours() && isActivated();
    }

    private boolean canSetToNormalMode() {
        return nowIsAfterWorkHours() && isActivated();
    }

    private boolean nowIsWithinWorkHours() {
        int startTimeInSecondsOfDay = sharedPreferences.getInt(WORK_START_TIME_KEY, 0);
        int endTimeInSecondsOfDay = sharedPreferences.getInt(WORK_END_TIME_KEY, 0);
        DateTime now = now();

        return startTimeInSecondsOfDay <= now.getMillisOfDay() && now.getMillisOfDay() <= endTimeInSecondsOfDay;
    }

    private boolean nowIsAfterWorkHours() {
        int endTimeInSecondsOfDay = sharedPreferences.getInt(WORK_END_TIME_KEY, 0);
        return now().getMillisOfDay() >= endTimeInSecondsOfDay;
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
