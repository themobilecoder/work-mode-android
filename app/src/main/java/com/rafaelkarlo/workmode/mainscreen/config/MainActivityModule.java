package com.rafaelkarlo.workmode.mainscreen.config;

import android.app.AlarmManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;

import com.rafaelkarlo.workmode.mainscreen.presenter.MainPresenterImpl;
import com.rafaelkarlo.workmode.mainscreen.service.time.WorkTimeServiceImpl;
import com.rafaelkarlo.workmode.mainscreen.service.alarm.WorkModeAlarmImpl;
import com.rafaelkarlo.workmode.mainscreen.service.alarm.WorkModeAlarmReceiver;
import com.rafaelkarlo.workmode.mainscreen.service.WorkModeService;
import com.rafaelkarlo.workmode.mainscreen.service.audio.AudioModeServiceImpl;

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
    public AlarmManager provideAlarmManager() {
        return (AlarmManager) application.getSystemService(Context.ALARM_SERVICE);
    }

    @Provides
    @Singleton
    public SharedPreferences provideSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @Singleton
    public AudioModeServiceImpl provideAudioModeService() {
        return new AudioModeServiceImpl(provideAudioManager(), provideSharedPreferences());
    }

    @Provides
    @Singleton
    public WorkTimeServiceImpl provideWorkTimeService() {
        return new WorkTimeServiceImpl(provideSharedPreferences());
    }

    @Provides
    @Singleton
    public WorkModeService provideWorkModeService() {
        return new WorkModeService(provideAudioModeService(), provideWorkTimeService(), provideSharedPreferences());
    }

    @Provides
    @Singleton
    public MainPresenterImpl provideMainPresenter() {
        return new MainPresenterImpl(provideWorkModeService(), provideWorkModeAlarm());
    }

    @Provides
    @Singleton
    public WorkModeAlarmImpl provideWorkModeAlarm() {
        return new WorkModeAlarmImpl(application, provideAlarmManager());
    }

    @Provides
    @Singleton
    public WorkModeAlarmReceiver provideWorkModeAlarmReceiver() {
        return new WorkModeAlarmReceiver();
    }

}
