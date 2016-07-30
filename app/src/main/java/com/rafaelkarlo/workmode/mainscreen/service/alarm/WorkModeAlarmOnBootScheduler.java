package com.rafaelkarlo.workmode.mainscreen.service.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rafaelkarlo.workmode.MainApplication;
import com.rafaelkarlo.workmode.mainscreen.service.WorkModeService;
import com.rafaelkarlo.workmode.mainscreen.service.time.WorkTimeServiceImpl;

import org.joda.time.LocalTime;

import javax.inject.Inject;

public class WorkModeAlarmOnBootScheduler extends BroadcastReceiver {

    private WorkTimeServiceImpl workTimeService;
    private WorkModeAlarmImpl workModeAlarm;
    private WorkModeService workModeService;

    @Override
    public void onReceive(Context context, Intent intent) {
            injectDependencies(context);

            LocalTime startWorkTime = workTimeService.getStartWorkTime();
            LocalTime endWorkTime = workTimeService.getEndWorkTime();

            if (canStartAlarm(startWorkTime, endWorkTime)) {
                workModeAlarm.startAlarm(startWorkTime, endWorkTime);
            }
    }

    @Inject
    public void setWorkTimeService(WorkTimeServiceImpl workTimeService) {
        this.workTimeService = workTimeService;
    }

    @Inject
    public void setAlarmService(WorkModeAlarmImpl workModeAlarm) {
        this.workModeAlarm = workModeAlarm;
    }

    @Inject
    public void setWorkModeService(WorkModeService workModeService) {
        this.workModeService = workModeService;
    }

    private void injectDependencies(Context context) {
        ((MainApplication) context.getApplicationContext()).getMainActivityComponent().inject(this);
    }

    private boolean canStartAlarm(LocalTime startWorkTime, LocalTime endWorkTime) {
        return workModeService.isActivated() && startWorkTime != null && endWorkTime != null;
    }
}
