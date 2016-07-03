package com.rafaelkarlo.workmode;

import android.app.Application;

import com.rafaelkarlo.workmode.config.DaggerMainScreenComponent;
import com.rafaelkarlo.workmode.config.MainScreenComponent;
import com.rafaelkarlo.workmode.config.MainScreenModule;

public class MainApplication extends Application {

    MainScreenComponent mainScreenComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mainScreenComponent = DaggerMainScreenComponent.builder()
                .mainScreenModule(new MainScreenModule(this))
                .build();
    }

    public MainScreenComponent getMainScreenComponent() {
        return mainScreenComponent;
    }
}
