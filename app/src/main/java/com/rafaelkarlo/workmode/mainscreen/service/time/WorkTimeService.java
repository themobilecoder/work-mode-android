package com.rafaelkarlo.workmode.mainscreen.service.time;

import org.joda.time.LocalTime;

public interface WorkTimeService {

    void setStartWorkTime(LocalTime startWorkTime);
    void setEndWorkTime(LocalTime endWorkTime);
    LocalTime getStartWorkTime();
    LocalTime getEndWorkTime();
}
