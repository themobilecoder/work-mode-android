package com.rafaelkarlo.workmode.mainscreen.config;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;

import com.rafaelkarlo.workmode.mainscreen.presenter.MainPresenterImpl;
import com.rafaelkarlo.workmode.mainscreen.service.WorkModeService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class MainActivityModule {

    private Application application;

    public MainActivityModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public Application provideApplication() {
        return application;
    }

    @Provides
    @Singleton
    public AudioManager provideAudioManager() {
        return (AudioManager) application.getSystemService(Context.AUDIO_SERVICE);
    }

    @Provides
    @Singleton
    public SharedPreferences provideSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @Singleton
    public WorkModeService provideWorkModeService() {
        return new WorkModeService(provideAudioManager(), provideSharedPreferences());
    }

    @Provides
    @Singleton
    public MainPresenterImpl provideMainPresenter() {
        return new MainPresenterImpl(provideWorkModeService());
    }

}
