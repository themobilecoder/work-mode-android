package com.rafaelkarlo.workmode.mainscreen.service.alarm;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.joda.time.DateTimeUtils.setCurrentMillisFixed;
import static org.joda.time.DateTimeUtils.setCurrentMillisSystem;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class WorkModeAlarmTest {

    @Mock
    AlarmService alarmService;

    private WorkModeAlarmImpl workModeAlarm;

    @Before
    public void setup() {
        workModeAlarm = new WorkModeAlarmImpl(alarmService);
    }

    @After
    public void resetTime() {
        resetToPresent();
    }

    @Test
    public void shouldSetAlarmsTodayOnNormalShiftWhenCurrentTimeIsBeforeEndTime() {
        LocalTime workStartTimeNormalShift = new LocalTime(7, 0, 0);
        LocalTime workEndTimeNormalShift = new LocalTime(17, 0, 0);
        setCurrentTime(new LocalTime(8, 0, 0));

        workModeAlarm.startAlarm(workStartTimeNormalShift, workEndTimeNormalShift);

        LocalDateTime localDateTimeToday = new LocalDateTime();
        LocalDateTime triggerStartTimeToday = localDateTimeToday.withTime(7, 0, 0, 0);
        LocalDateTime triggerEndTimeToday = localDateTimeToday.withTime(17, 0, 0, 0);

        verifyAlarmsForStartAndEndTimesHaveBeenCalled(triggerStartTimeToday, triggerEndTimeToday);
    }

    @Test
    public void shouldSetAlarmsTomorrowOnNormalShiftWhenCurrentTimeIsAfterEndTime() {
        LocalTime workStartTimeNormalShift = new LocalTime(7, 0, 0);
        LocalTime workEndTimeNormalShift = new LocalTime(17, 0, 0);
        setCurrentTime(new LocalTime(17, 0, 1));

        workModeAlarm.startAlarm(workStartTimeNormalShift, workEndTimeNormalShift);

        LocalDateTime localDateTimeTomorrow = new LocalDateTime().plusDays(1);
        LocalDateTime triggerStartTimeTomorrow = localDateTimeTomorrow.withTime(7, 0, 0, 0);
        LocalDateTime triggerEndTimeTomorrow = localDateTimeTomorrow.withTime(17, 0, 0, 0);

        verifyAlarmsForStartAndEndTimesHaveBeenCalled(triggerStartTimeTomorrow, triggerEndTimeTomorrow);
    }

    @Test
    public void shouldProperlySetAlarmOnNightShiftWhenCurrentTimeIsBeforeEndOfWorkInTheMorning() {
        LocalTime workStartTimeNightShift = new LocalTime(22, 0, 0);
        LocalTime workEndTimeNightShift = new LocalTime(6, 0, 0);
        LocalTime beforeEndOfWorkInTheMorning = new LocalTime(5, 0, 0);
        setCurrentTime(beforeEndOfWorkInTheMorning);

        workModeAlarm.startAlarm(workStartTimeNightShift, workEndTimeNightShift);

        LocalDate localDateTimeYesterday = new LocalDate().minusDays(1);
        LocalDateTime triggerStartTimeYesterday = localDateTimeYesterday.toLocalDateTime(workStartTimeNightShift);
        LocalDateTime triggerEndTimeToday = new LocalDate().toLocalDateTime(workEndTimeNightShift);

        verifyAlarmsForStartAndEndTimesHaveBeenCalled(triggerStartTimeYesterday, triggerEndTimeToday);
    }

    private void verifyAlarmsForStartAndEndTimesHaveBeenCalled(LocalDateTime triggerStartTimeYesterday, LocalDateTime triggerEndTimeToday) {
        verify(alarmService).setRepeatingAlarmForDateTimeWithIdentifier(triggerStartTimeYesterday, WorkModeAlarmUtils.WORK_START_ACTION);
        verify(alarmService).setRepeatingAlarmForDateTimeWithIdentifier(triggerEndTimeToday, WorkModeAlarmUtils.WORK_END_ACTION);
    }

    @Test
    public void shouldProperlySetAlarmOnNightShiftWhenCurrentTimeIsBeforeEndOfWorkInTheEvening() {
        LocalTime workStartTimeNightShift = new LocalTime(22, 0, 0);
        LocalTime workEndTimeNightShift = new LocalTime(6, 0, 0);
        LocalTime beforeEndOfWorkInTheEvening = new LocalTime(23, 0, 0);
        setCurrentTime(beforeEndOfWorkInTheEvening);

        workModeAlarm.startAlarm(workStartTimeNightShift, workEndTimeNightShift);

        LocalDate localDateTimeTomorrow = new LocalDate().plusDays(1);
        LocalDateTime triggerStartTimeToday = new LocalDate().toLocalDateTime(workStartTimeNightShift);
        LocalDateTime triggerEndTimeTomorrow = localDateTimeTomorrow.toLocalDateTime(workEndTimeNightShift);

        verifyAlarmsForStartAndEndTimesHaveBeenCalled(triggerStartTimeToday, triggerEndTimeTomorrow);
    }



    private static void setCurrentTime(LocalTime localTime) {
        setCurrentMillisFixed(localTime.toDateTimeToday().getMillis());
    }

    private static void resetToPresent() {
        setCurrentMillisSystem();
    }

}