package com.rafaelkarlo.workmode.config;

import android.app.Application;
import android.content.Context;
import android.media.AudioManager;

import com.rafaelkarlo.workmode.service.WorkModeService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class MainScreenModule {

    Application application;

    public MainScreenModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Application application() {
        return application;
    }

    @Provides
    @Singleton
    public AudioManager audioManager(Application application) {
        return (AudioManager) application.getSystemService(Context.AUDIO_SERVICE);
    }

    @Provides
    @Singleton
    public WorkModeService workModeService() {
        return new WorkModeService(audioManager(application));
    }

}
