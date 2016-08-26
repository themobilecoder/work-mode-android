package com.rafaelkarlo.workmode.mainscreen.config;

import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;

import com.rafaelkarlo.workmode.mainscreen.presenter.MainPresenterImpl;
import com.rafaelkarlo.workmode.mainscreen.service.WorkModeAudioOverrideService;
import com.rafaelkarlo.workmode.mainscreen.service.alarm.AlarmService;
import com.rafaelkarlo.workmode.mainscreen.service.alarm.AlarmServiceImpl;
import com.rafaelkarlo.workmode.mainscreen.service.alarm.WorkModeAlarm;
import com.rafaelkarlo.workmode.mainscreen.service.audio.AudioModeService;
import com.rafaelkarlo.workmode.mainscreen.service.time.WorkTimeService;
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

    @Provides
    @Singleton
    public AudioModeService provideAudioModeService(AudioManager audioManager, SharedPreferences sharedPreferences) {
        return new AudioModeServiceImpl(audioManager, sharedPreferences);
    }

    @Provides
    @Singleton
    public WorkTimeService provideWorkTimeService(SharedPreferences sharedPreferences) {
        return new WorkTimeServiceImpl(sharedPreferences);
    }

    @Provides
    @Singleton
    public WorkModeService provideWorkModeService(AudioModeService audioModeService, WorkTimeService workTimeService, SharedPreferences sharedPreferences) {
        return new WorkModeService(audioModeService, workTimeService, sharedPreferences);
    }

    @Provides
    @Singleton
    public WorkModeAudioOverrideService provideWorkModeAudioOverrideService(AudioModeService audioModeService) {
        return new WorkModeAudioOverrideService(audioModeService);
    }

    @Provides
    @Singleton
    public MainPresenterImpl provideMainPresenter(WorkModeService workModeService, WorkModeAlarm workModeAlarm, WorkModeAudioOverrideService workModeAudioOverrideService) {
        return new MainPresenterImpl(workModeService, workModeAlarm, workModeAudioOverrideService);
    }

    @Provides
    @Singleton
    public AlarmService provideAlarmService(Context context, AlarmManager alarmManager) {
        return new AlarmServiceImpl(context, alarmManager);
    }

    @Provides
    @Singleton
    public WorkModeAlarm provideWorkModeAlarm(AlarmService alarmService) {
        return new WorkModeAlarmImpl(alarmService);
    }

    @Provides
    @Singleton
    public WorkModeAlarmReceiver provideWorkModeAlarmReceiver() {
        return new WorkModeAlarmReceiver();
    }

}
