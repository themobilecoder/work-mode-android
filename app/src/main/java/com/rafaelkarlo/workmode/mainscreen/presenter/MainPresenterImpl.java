package com.rafaelkarlo.workmode.mainscreen.presenter;

import com.rafaelkarlo.workmode.mainscreen.service.alarm.WorkModeAlarm;
import com.rafaelkarlo.workmode.mainscreen.service.WorkModeService;
import com.rafaelkarlo.workmode.mainscreen.view.MainView;

import org.joda.time.LocalTime;

import java.text.SimpleDateFormat;

import javax.inject.Inject;

public class MainPresenterImpl implements MainPresenter {

    private MainView mainView;

    private WorkModeService workModeService;

    private WorkModeAlarm workModeAlarm;

    @Inject
    public MainPresenterImpl(WorkModeService workModeService, WorkModeAlarm workModeAlarm) {
        this.workModeService = workModeService;
        this.workModeAlarm = workModeAlarm;
    }

    @Override
    public void onCreate() {
        updateViewForStatus();
        updateWorkHoursView();
    }

    @Override
    public void attachView(MainView mainView) {
        this.mainView = mainView;
    }

    @Override
    public void activateWorkMode() {
        activateWorkModeIfAllowed();
    }

    @Override
    public void deactivateWorkMode() {
        workModeService.deactivate();
        workModeService.setToPreviousMode();
        mainView.onWorkModeDeactivation();
        workModeAlarm.cancelAlarm();
    }

    @Override
    public void setStartDate(int hour, int minute) {
        LocalTime workStartTime = new LocalTime(hour, minute);
        workModeService.setStartTime(workStartTime);
        mainView.onSetStartDate(getFormattedTimeString(hour, minute));
        mainView.onWorkModeDeactivation();
    }

    @Override
    public void setEndDate(int hour, int minute) {
        LocalTime workEndTime = new LocalTime(hour, minute);
        workModeService.setEndTime(workEndTime);
        mainView.onSetEndDate(getFormattedTimeString(hour, minute));
        mainView.onWorkModeDeactivation();
    }

    private void activateWorkModeIfAllowed() {
        LocalTime startTime = workModeService.getStartTime();
        LocalTime endTime = workModeService.getEndTime();

        if (workHoursAreMissing(startTime, endTime)) {
            mainView.displayErrorOnMissingWorkHours();
        } else if (startTime.equals(endTime)) {
            mainView.displayErrorOnInvalidWorkHours();
        } else {
            workModeService.activate();
            workModeAlarm.startAlarm(startTime, endTime);
            mainView.onWorkModeActivation();
            mainView.displayActivationSuccessful();
        }
    }

    private boolean isEndTimeBeforeStartTime(LocalTime startTime, LocalTime endTime) {
        return endTime.isBefore(startTime);
    }

    private boolean workHoursAreMissing(LocalTime startTime, LocalTime endTime) {
        return startTime == null || endTime == null;
    }

    private void updateViewForStatus() {
        if (workModeService.isActivated()) {
            mainView.onWorkModeActivation();
        } else {
            mainView.onWorkModeDeactivation();
        }
    }

    private void updateWorkHoursView() {
        LocalTime startDate = workModeService.getStartTime();
        String startDateString = startDate == null ? "--" : getFormattedTimeString(startDate.getHourOfDay(), startDate.getMinuteOfHour());
        mainView.onSetStartDate(startDateString);

        LocalTime endDate = workModeService.getEndTime();
        String endDateString = endDate == null ? "--" : getFormattedTimeString(endDate.getHourOfDay(), endDate.getMinuteOfHour());
        mainView.onSetEndDate(endDateString);
    }

    private String getFormattedTimeString(int hour, int minute) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a");
        return simpleDateFormat.format(new LocalTime(hour, minute).toDateTimeToday().toDate());
    }
}
