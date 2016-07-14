package com.rafaelkarlo.workmode.mainscreen.service;

import android.content.SharedPreferences;
import android.media.AudioManager;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import rx.Observable;
import rx.Subscriber;

import static org.joda.time.DateTime.now;
import static rx.android.schedulers.AndroidSchedulers.*;
import static rx.schedulers.Schedulers.*;

public class WorkModeService {

    private static final String WORK_MODE_ACTIVATED = "WORK_MODE_ACTIVATED";
    public static final String WORK_START_TIME_KEY = "WORK_START_TIME";
    public static final String WORK_END_TIME_KEY = "WORK_END_TIME";
    private static final String PREVIOUS_RINGER_MODE_KEY = "PREVIOUS_RINGER_MODE";

    private AudioManager audioManager;
    private SharedPreferences sharedPreferences;

    private final Observable<Void> setSilentModeTask = Observable.create(new Observable.OnSubscribe<Void>() {
        @Override
        public void call(Subscriber<? super Void> subscriber) {
            setRingerModeToSilentCompletely();
        }
    });

    public WorkModeService(AudioManager audioManager, SharedPreferences sharedPreferences) {
        this.audioManager = audioManager;
        this.sharedPreferences = sharedPreferences;
    }

    public boolean setToSilentMode() {
        if (canSetToSilentMode()) {
            saveCurrentRingerMode();
            setSilentModeTask
                    .subscribeOn(io())
                    .observeOn(mainThread())
                    .subscribe();
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

    public void setToPreviousMode() {
        audioManager.setRingerMode(getPreviousRingerMode());
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

    public void setWorkHours(LocalTime workStartTime, LocalTime workEndTime) {
        if (workStartTime.isAfter(workEndTime)) {
            throw new IllegalArgumentException("Work start time should be before the work end time");
        }
        saveWorkHoursToSharedPreferences(workStartTime, workEndTime);
    }

    public void setStartTime(LocalTime workStartTime) {
        sharedPreferences.edit()
                .putInt(WORK_START_TIME_KEY, workStartTime.getMillisOfDay())
                .apply();
    }

    public void setEndTime(LocalTime workEndTime) {
        sharedPreferences.edit()
                .putInt(WORK_END_TIME_KEY, workEndTime.getMillisOfDay())
                .apply();
    }

    public LocalTime getStartTime() {
        int timeInMillis = sharedPreferences.getInt(WORK_START_TIME_KEY, -1);
        if (timeInMillis == -1) {
            return null;
        }
        return LocalTime.fromMillisOfDay(timeInMillis);
    }

    public LocalTime getEndTime() {
        int timeInMillis = sharedPreferences.getInt(WORK_END_TIME_KEY, -1);
        if (timeInMillis == -1) {
            return null;
        }
        return LocalTime.fromMillisOfDay(timeInMillis);
    }

    private int getPreviousRingerMode() {
        return sharedPreferences.getInt(PREVIOUS_RINGER_MODE_KEY, 0);
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

    private void saveCurrentRingerMode() {
        int currentRingerMode = audioManager.getRingerMode();
        sharedPreferences.edit().putInt(PREVIOUS_RINGER_MODE_KEY, currentRingerMode).apply();
    }

    private void saveModeActivated() {
        saveModeInSharedPreferences(true);
    }

    private void saveModeDeactivated() {
        saveModeInSharedPreferences(false);
    }

    private void saveModeInSharedPreferences(boolean activated) {
        sharedPreferences.edit().putBoolean(WORK_MODE_ACTIVATED, activated).apply();
    }

    private void saveWorkHoursToSharedPreferences(LocalTime workStartTime, LocalTime workEndTime) {
        sharedPreferences.edit()
                .putInt(WORK_START_TIME_KEY, workStartTime.getMillisOfDay())
                .putInt(WORK_END_TIME_KEY, workEndTime.getMillisOfDay())
                .apply();
    }

    private void setRingerModeToSilentCompletely() {
        try {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            Thread.sleep(1000);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        } catch (InterruptedException e) {
        }
    }
}
