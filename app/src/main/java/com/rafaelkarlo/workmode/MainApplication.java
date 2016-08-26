package com.rafaelkarlo.workmode;

import android.app.Application;

import com.rafaelkarlo.workmode.mainscreen.config.AndroidModule;
import com.rafaelkarlo.workmode.mainscreen.config.DaggerMainActivityComponent;
import com.rafaelkarlo.workmode.mainscreen.config.MainActivityComponent;
import com.rafaelkarlo.workmode.mainscreen.config.MainActivityModule;

public class MainApplication extends Application {

    MainActivityComponent mainActivityComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mainActivityComponent = DaggerMainActivityComponent.builder()
                .androidModule(new AndroidModule(this))
                .mainActivityModule(new MainActivityModule())
                .build();
    }

    public MainActivityComponent getMainActivityComponent() {
        return mainActivityComponent;
    }
}
