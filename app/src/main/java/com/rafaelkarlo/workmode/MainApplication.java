package com.rafaelkarlo.workmode;

import android.app.Application;

import com.rafaelkarlo.workmode.mainscreen.config.AndroidModule;
import com.rafaelkarlo.workmode.mainscreen.config.DaggerMainActivityComponent;
import com.rafaelkarlo.workmode.mainscreen.config.MainActivityComponent;
import com.rafaelkarlo.workmode.mainscreen.config.WorkModeModule;

public class MainApplication extends Application {

    MainActivityComponent mainActivityComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mainActivityComponent = DaggerMainActivityComponent.builder()
                .androidModule(new AndroidModule(this))
                .workModeModule(new WorkModeModule())
                .build();
    }

    public MainActivityComponent getMainActivityComponent() {
        return mainActivityComponent;
    }
}
