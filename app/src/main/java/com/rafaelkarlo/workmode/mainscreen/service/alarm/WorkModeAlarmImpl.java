package com.rafaelkarlo.workmode.mainscreen.service.alarm;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import org.joda.time.LocalTime;

import java.util.Calendar;

import javax.inject.Inject;

import static android.app.AlarmManager.INTERVAL_DAY;
import static android.app.AlarmManager.RTC_WAKEUP;
import static com.rafaelkarlo.workmode.mainscreen.service.alarm.WorkModeAlarmUtils.ONE_DAY_IN_MILLIS;
import static com.rafaelkarlo.workmode.mainscreen.service.alarm.WorkModeAlarmUtils.WORK_END_ACTION;
import static com.rafaelkarlo.workmode.mainscreen.service.alarm.WorkModeAlarmUtils.WORK_START_ACTION;
import static com.rafaelkarlo.workmode.mainscreen.service.alarm.WorkModeAlarmUtils.createIntentWithIdentifierAndTime;
import static com.rafaelkarlo.workmode.mainscreen.service.alarm.WorkModeAlarmUtils.createPendingIntentWithIntent;
import static org.joda.time.LocalTime.now;

public class WorkModeAlarmImpl implements WorkModeAlarm {

    private final AlarmManager alarmManager;

    private Context context;

    @Inject
    public WorkModeAlarmImpl(Context context, AlarmManager alarmManager) {
        this.context = context;
        this.alarmManager = alarmManager;
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
        alarmManager.cancel(createPendingIntentWithIntent(context, createIntentWithIdentifierAndTime(context, WORK_START_ACTION, 0)));
        alarmManager.cancel(createPendingIntentWithIntent(context, createIntentWithIdentifierAndTime(context, WORK_END_ACTION, 0)));
    }

    private void setAlarmForNormalShift(LocalTime workStartTime, LocalTime workEndTime) {
        LocalTime currentTime = now();
        if (currentTime.isBefore(workEndTime)) {
            setDailyAlarmStartingToday(workStartTime, workEndTime);
        } else {
            setDailyAlarmStartingTomorrow(workStartTime, workEndTime);
        }
    }

    private void setAlarmForNightShift(LocalTime workStartTime, LocalTime workEndTime) {
        LocalTime currentTime = now();

        if (currentTime.isBefore(workEndTime)) {
            setDailyAlarmForMidnightStartingYesterday(workStartTime, workEndTime);
        } else {
            setDailyAlarmForMidnightStartingToday(workStartTime, workEndTime);
        }
    }

    private void setDailyAlarmStartingToday(LocalTime workStartTime, LocalTime workEndTime) {
        setDailyAlarmForAnAction(workStartTime, WORK_START_ACTION, TriggerDay.TODAY);
        setDailyAlarmForAnAction(workEndTime, WORK_END_ACTION, TriggerDay.TODAY);
    }

    private void setDailyAlarmStartingTomorrow(LocalTime workStartTime, LocalTime workEndTime) {
        setDailyAlarmForAnAction(workStartTime, WORK_START_ACTION, TriggerDay.TOMORROW);
        setDailyAlarmForAnAction(workEndTime, WORK_END_ACTION, TriggerDay.TOMORROW);
    }

    private void setDailyAlarmForMidnightStartingYesterday(LocalTime workStartTime, LocalTime workEndTime) {
        setDailyAlarmForAnAction(workStartTime, WORK_START_ACTION, TriggerDay.YESTERDAY);
        setDailyAlarmForAnAction(workEndTime, WORK_END_ACTION, TriggerDay.TODAY);
    }

    private void setDailyAlarmForMidnightStartingToday(LocalTime workStartTime, LocalTime workEndTime) {
        setDailyAlarmForAnAction(workStartTime, WORK_START_ACTION, TriggerDay.TODAY);
        setDailyAlarmForAnAction(workEndTime, WORK_END_ACTION, TriggerDay.TOMORROW);
    }

    private void setDailyAlarmForAnAction(LocalTime triggerTime, String actionIdentifier, TriggerDay triggerDay) {
        long timeInMillis = getTimeInMillisFromLocalTime(triggerTime);
        Intent workStartIntent = createIntentWithIdentifierAndTime(context, actionIdentifier, timeInMillis);

        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= Build.VERSION_CODES.KITKAT) {
            switch (triggerDay) {
                case YESTERDAY:
                    setAlarmYesterdayForApi19AndAbove(timeInMillis, workStartIntent);
                    break;
                case TODAY:
                    setAlarmForApi19AndAbove(timeInMillis, workStartIntent);
                    break;
                case TOMORROW:
                    setAlarmTomorrowForApi19AndAbove(timeInMillis, workStartIntent);
                    break;
                default:
                    break;
            }
        } else {
            switch (triggerDay) {
                case YESTERDAY:
                    setAlarmYesterdayForApiBelow19(timeInMillis, workStartIntent);
                    break;
                case TODAY:
                    setAlarmForApiBelow19(timeInMillis, workStartIntent);
                    break;
                case TOMORROW:
                    setAlarmTomorrowForApiBelow19(timeInMillis, workStartIntent);
                    break;
                default:
                    break;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setAlarmTomorrowForApi19AndAbove(long triggerTimeInMillis, Intent workStartIntent) {
        setAlarmForApi19AndAbove(getTomorrowsTriggerTimeInMillis(triggerTimeInMillis), workStartIntent);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setAlarmYesterdayForApi19AndAbove(long triggerTimeInMillis, Intent workStartIntent) {
        setAlarmForApi19AndAbove(getYesterdaysTriggerTimeInMillis(triggerTimeInMillis), workStartIntent);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setAlarmForApi19AndAbove(long triggerTimeInMillis, Intent workStartIntent) {
        alarmManager.setExact(
                RTC_WAKEUP,
                triggerTimeInMillis,
                createPendingIntentWithIntent(context, workStartIntent)
        );
    }

    private void setAlarmTomorrowForApiBelow19(long triggerTimeInMillis, Intent workStartIntent) {
        setAlarmForApiBelow19(getTomorrowsTriggerTimeInMillis(triggerTimeInMillis), workStartIntent);
    }

    private void setAlarmYesterdayForApiBelow19(long triggerTimeInMillis, Intent workStartIntent) {
        setAlarmForApiBelow19(getYesterdaysTriggerTimeInMillis(triggerTimeInMillis), workStartIntent);

    }

    private void setAlarmForApiBelow19(long triggerTimeInMillis, Intent workStartIntent) {
        alarmManager.setRepeating(
                RTC_WAKEUP,
                triggerTimeInMillis,
                INTERVAL_DAY,
                createPendingIntentWithIntent(context, workStartIntent)
        );
    }

    private long getTimeInMillisFromLocalTime(LocalTime triggerTime) {
        Calendar calendarSilentInstance = Calendar.getInstance();
        calendarSilentInstance.setTime(triggerTime.toDateTimeToday().toDate());
        return calendarSilentInstance.getTimeInMillis();
    }

    private long getTomorrowsTriggerTimeInMillis(long triggerTimeInMillis) {
        return triggerTimeInMillis + ONE_DAY_IN_MILLIS;
    }

    private long getYesterdaysTriggerTimeInMillis(long triggerTimeInMillis) {
        return triggerTimeInMillis - ONE_DAY_IN_MILLIS;
    }

    private enum TriggerDay {
        YESTERDAY,
        TODAY,
        TOMORROW
    }
}
