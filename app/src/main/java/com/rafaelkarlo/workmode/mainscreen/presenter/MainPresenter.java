package com.rafaelkarlo.workmode.mainscreen.presenter;

import com.rafaelkarlo.workmode.mainscreen.view.MainView;

public interface MainPresenter {

    void onCreate();
    void attachView(MainView mainView);
    void activateWorkMode();
    void deactivateWorkMode();
}
