package com.rafaelkarlo.workmode.mainscreen.presenter;

import com.rafaelkarlo.workmode.mainscreen.service.audio.AudioMode;
import com.rafaelkarlo.workmode.mainscreen.service.time.WorkDay;
import com.rafaelkarlo.workmode.mainscreen.view.MainView;

import java.util.Set;

public interface MainPresenter {

    void onCreate();
    void attachView(MainView mainView);
    void activateWorkMode();
    void deactivateWorkMode();
    void setStartDate(int hour, int minute);
    void setEndDate(int hour, int minute);

    void setWorkDays(Set<WorkDay> workDays);

    Set<WorkDay> getSavedDays();

    void setCurrentAudioMode(AudioMode audioMode);
}
