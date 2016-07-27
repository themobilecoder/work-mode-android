package com.rafaelkarlo.workmode.mainscreen.service.alarm;

import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import javax.inject.Inject;

import static com.rafaelkarlo.workmode.mainscreen.service.alarm.WorkModeAlarmUtils.WORK_END_ACTION;
import static com.rafaelkarlo.workmode.mainscreen.service.alarm.WorkModeAlarmUtils.WORK_START_ACTION;
import static org.joda.time.LocalTime.now;

public class WorkModeAlarmImpl implements WorkModeAlarm {

    private final AlarmService alarmService;

    @Inject
    public WorkModeAlarmImpl(AlarmService alarmService) {
        this.alarmService = alarmService;
    }

    @Override
    public void startAlarm(LocalTime workStartTime, LocalTime workEndTime) {
        if (workStartTime.isBefore(workEndTime)) {
            setAlarmForNormalShift(workStartTime, workEndTime);
        } else {
            setAlarmForNightShift(workStartTime, workEndTime);
        }
    }

    @Override
    public void cancelAlarm() {
        alarmService.cancelAlarmWithIdentifier(WORK_START_ACTION);
        alarmService.cancelAlarmWithIdentifier(WORK_END_ACTION);
    }

    private void setAlarmForNormalShift(LocalTime workStartTime, LocalTime workEndTime) {
        LocalTime currentTime = now();
        if (currentTime.isBefore(workEndTime)) {
            LocalDateTime todayWorkStartTime = workStartTime.toDateTimeToday().toLocalDateTime();
            LocalDateTime todayWorkEndTime = workEndTime.toDateTimeToday().toLocalDateTime();
            setAlarmForStartAndEndOfWorkDateTime(todayWorkStartTime, todayWorkEndTime);
        } else {
            LocalDateTime tomorrowWorkStartTime = workStartTime.toDateTimeToday().plusDays(1).toLocalDateTime();
            LocalDateTime tomorrowWorkEndTime = workEndTime.toDateTimeToday().plusDays(1).toLocalDateTime();
            setAlarmForStartAndEndOfWorkDateTime(tomorrowWorkStartTime, tomorrowWorkEndTime);
        }
    }

    private void setAlarmForNightShift(LocalTime workStartTime, LocalTime workEndTime) {
        LocalTime currentTime = now();
        if (currentTime.isBefore(workEndTime)) {
            LocalDateTime yesterdayWorkStartTime = workStartTime.toDateTimeToday().minusDays(1).toLocalDateTime();
            LocalDateTime todayWorkEndTime = workEndTime.toDateTimeToday().toLocalDateTime();
            setAlarmForStartAndEndOfWorkDateTime(yesterdayWorkStartTime, todayWorkEndTime);
        } else {
            LocalDateTime todayWorkStartTime = workStartTime.toDateTimeToday().toLocalDateTime();
            LocalDateTime tomorrowWorkEndTime = workEndTime.toDateTimeToday().plusDays(1).toLocalDateTime();
            setAlarmForStartAndEndOfWorkDateTime(todayWorkStartTime, tomorrowWorkEndTime);
        }

    }

    private void setAlarmForStartAndEndOfWorkDateTime(LocalDateTime tomorrowWorkStartTime, LocalDateTime tomorrowWorkEndTime) {
        alarmService.setRepeatingAlarmForDateTimeWithIdentifier(tomorrowWorkStartTime, WORK_START_ACTION);
        alarmService.setRepeatingAlarmForDateTimeWithIdentifier(tomorrowWorkEndTime, WORK_END_ACTION);
    }
}
