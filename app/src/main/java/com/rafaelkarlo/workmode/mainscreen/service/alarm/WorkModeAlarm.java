package com.rafaelkarlo.workmode.mainscreen.service.alarm;

import org.joda.time.LocalTime;

public interface WorkModeAlarm {

    void startAlarm(LocalTime workStartTime, LocalTime workEndTime);
    void cancelAlarm();

}
