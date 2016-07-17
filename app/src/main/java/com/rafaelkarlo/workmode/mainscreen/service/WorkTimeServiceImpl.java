package com.rafaelkarlo.workmode.mainscreen.service;

import android.content.SharedPreferences;

import org.joda.time.LocalTime;

import javax.inject.Inject;

public class WorkTimeServiceImpl implements WorkTimeService {

    public static final String WORK_START_TIME_KEY = "WORK_START_TIME";
    public static final String WORK_END_TIME_KEY = "WORK_END_TIME";


    private SharedPreferences sharedPreferences;

    @Inject
    public WorkTimeServiceImpl(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public void setStartWorkTime(LocalTime startWorkTime) {
        sharedPreferences.edit()
                .putInt(WORK_START_TIME_KEY, startWorkTime.getMillisOfDay())
                .apply();
    }

    @Override
    public void setEndWorkTime(LocalTime endWorkTime) {
        sharedPreferences.edit()
                .putInt(WORK_END_TIME_KEY, endWorkTime.getMillisOfDay())
                .apply();
    }

    @Override
    public LocalTime getStartWorkTime() {
        int timeInMillis = sharedPreferences.getInt(WORK_START_TIME_KEY, -1);
        if (timeInMillis == -1) {
            return null;
        }
        return LocalTime.fromMillisOfDay(timeInMillis);
    }

    @Override
    public LocalTime getEndWorkTime() {
        int timeInMillis = sharedPreferences.getInt(WORK_END_TIME_KEY, -1);
        if (timeInMillis == -1) {
            return null;
        }
        return LocalTime.fromMillisOfDay(timeInMillis);
    }

}
