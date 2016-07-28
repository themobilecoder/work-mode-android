package com.rafaelkarlo.workmode.mainscreen.service.time;

import org.joda.time.LocalTime;

import java.util.HashSet;
import java.util.Set;

public interface WorkTimeService {

    void setStartWorkTime(LocalTime startWorkTime);
    void setEndWorkTime(LocalTime endWorkTime);
    LocalTime getStartWorkTime();
    LocalTime getEndWorkTime();

    void saveWorkDays(HashSet<Workday> workdays);
    Set<Workday> getWorkDays();
}
