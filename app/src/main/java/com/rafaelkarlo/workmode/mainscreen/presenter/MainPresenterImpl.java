package com.rafaelkarlo.workmode.mainscreen.presenter;

import com.rafaelkarlo.workmode.mainscreen.service.WorkModeAudioOverrideService;
import com.rafaelkarlo.workmode.mainscreen.service.alarm.WorkModeAlarm;
import com.rafaelkarlo.workmode.mainscreen.service.WorkModeService;
import com.rafaelkarlo.workmode.mainscreen.service.audio.AudioMode;
import com.rafaelkarlo.workmode.mainscreen.service.time.WorkDay;
import com.rafaelkarlo.workmode.mainscreen.view.MainView;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

public class MainPresenterImpl implements MainPresenter {

    private MainView mainView;

    private WorkModeService workModeService;

    private WorkModeAudioOverrideService workModeAudioOverrideService;

    private WorkModeAlarm workModeAlarm;

    @Inject
    public MainPresenterImpl(WorkModeService workModeService, WorkModeAlarm workModeAlarm, WorkModeAudioOverrideService workModeAudioOverrideService) {
        this.workModeService = workModeService;
        this.workModeAlarm = workModeAlarm;
        this.workModeAudioOverrideService = workModeAudioOverrideService;
    }

    @Override
    public void onCreate() {
        updateViewForStatus();
        updateWorkHoursView();
        updateWorkDaysView();
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

    @Override
    public void setWorkDays(Set<WorkDay> workDays) {
        workModeService.setWorkDays(workDays);
        updateWorkDaysView();
        mainView.onWorkModeDeactivation();
    }

    @Override
    public Set<WorkDay> getSavedDays() {
        return workModeService.getWorkDays();
    }

    @Override
    public void setCurrentAudioMode(AudioMode audioMode) {
        workModeAudioOverrideService.overrideCurrentAudioMode(audioMode);
        String audioModeString = audioMode.toString();
        String prettifiedString = audioModeString.substring(0, 1) + audioModeString.substring(1).toLowerCase();
        mainView.displayAudioOverrideSuccessMessage(prettifiedString);
    }

    private void activateWorkModeIfAllowed() {
        LocalTime startTime = workModeService.getStartTime();
        LocalTime endTime = workModeService.getEndTime();
        Set<WorkDay> workDaySet = workModeService.getWorkDays();

        if (workHoursAreMissing(startTime, endTime)) {
            mainView.displayErrorOnMissingWorkHours();
        } else if (startTime.equals(endTime)) {
            mainView.displayErrorOnInvalidWorkHours();
        } else if (workDaySet.isEmpty()) {
            mainView.displayErrorOnMissingWorkDays();
        } else {
            workModeService.activate();
            workModeAlarm.startAlarm(startTime, endTime);
            mainView.onWorkModeActivation();
            mainView.displayActivationSuccessful();
        }
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

    private void updateWorkDaysView() {
        Set<WorkDay> workDays = workModeService.getWorkDays();
        String daysToDisplay = workDays.isEmpty() ? "--" : shortenDayAndMerge(workDays);
        mainView.onSetWorkDays(daysToDisplay);
    }

    private String getFormattedTimeString(int hour, int minute) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a");
        return simpleDateFormat.format(new LocalTime(hour, minute).toDateTimeToday().toDate());
    }

    private String shortenDayAndMerge(Set<WorkDay> workDays) {
        List<WorkDay> workDayList = new ArrayList<>(workDays);
        sortWorkdayListChronologically(workDayList);
        List<String> workDaysAsString = new ArrayList<>();
        for (WorkDay workDay : workDayList) {
            String workDayStringPrettified = workDay.getShortenDayString();
            workDaysAsString.add(workDayStringPrettified);
        }
        return StringUtils.join(workDaysAsString, ", ");
    }

    private void sortWorkdayListChronologically(List<WorkDay> workDayList) {
        Collections.sort(workDayList, new Comparator<WorkDay>() {
            @Override
            public int compare(WorkDay lhs, WorkDay rhs) {
                if (lhs.getValue() < rhs.getValue()) {
                    return -1;
                } else if (lhs.getValue() == rhs.getValue()) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });
    }
}
