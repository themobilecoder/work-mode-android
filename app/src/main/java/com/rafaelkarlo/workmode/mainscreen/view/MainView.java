package com.rafaelkarlo.workmode.mainscreen.view;

public interface MainView {

    void onWorkModeActivation();
    void onWorkModeDeactivation();
    void onSetStartDate(String startDate);
    void onSetEndDate(String endDate);
    void onSetWorkDays(String workDays);
    void displayActivationSuccessful();
    void displayErrorOnMissingWorkHours();
    void displayErrorOnInvalidWorkHours();
    void displayErrorOnMissingWorkDays();
    void displayAudioOverrideSuccessMessage(String newAudioMode);
}
