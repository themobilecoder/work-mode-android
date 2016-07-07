package com.rafaelkarlo.workmode.mainscreen.presenter;

import com.rafaelkarlo.workmode.mainscreen.view.MainView;
import com.rafaelkarlo.workmode.mainscreen.service.WorkModeService;

import javax.inject.Inject;

public class MainPresenterImpl implements MainPresenter {

    private MainView mainView;

    private WorkModeService workModeService;

    @Inject
    public MainPresenterImpl(WorkModeService workModeService) {
        this.workModeService = workModeService;
    }

    @Override
    public void onCreate() {
        updateViewForStatus();
    }

    @Override
    public void attachView(MainView mainView) {
        this.mainView = mainView;
    }

    @Override
    public void activateWorkMode() {
        workModeService.activate();
        mainView.onWorkModeActivation();
    }

    @Override
    public void deactivateWorkMode() {
        workModeService.deactivate();
        mainView.onWorkModeDeactivation();
    }

    private void updateViewForStatus() {
        if (workModeService.isActivated()) {
            mainView.onWorkModeActivation();
        } else {
            mainView.onWorkModeDeactivation();
        }
    }
}
