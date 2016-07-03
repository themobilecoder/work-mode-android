package com.rafaelkarlo.workmode.config;

import com.rafaelkarlo.workmode.MainScreen;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = MainScreenModule.class)
public interface MainScreenComponent {
    void inject(MainScreen mainScreenActivity);
}
