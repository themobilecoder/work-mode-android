package com.rafaelkarlo.workmode.mainscreen.service;

import org.joda.time.LocalTime;

public interface WorkModeAlarm {

    void startAlarm(LocalTime workStartTime, LocalTime workEndTime);
    void cancelAlarm();

}
