package com.rafaelkarlo.workmode.mainscreen.service;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;

import com.rafaelkarlo.workmode.MainApplication;
import com.rafaelkarlo.workmode.mainscreen.config.MainActivityComponent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkModeAlarmReceiverTest {

    public static final String START_WORK_ACTION_IDENTIFIER = "com.rafaelkarlo.workmode.START_WORK";
    public static final String END_WORK_ACTION_IDENTIFIER = "com.rafaelkarlo.workmode.END_WORK";

    @Mock
    Context context;

    @Mock
    AlarmManager alarmManager;

    @Mock
    WorkModeService workModeService;

    @Mock
    MainApplication mainApplication;

    @Mock
    MainActivityComponent mainActivityComponent;

    @Mock
    Intent intent;

    @Mock
    WorkModeAlarmUtils workModeAlarmUtils;

    private WorkModeAlarmReceiver workModeAlarmReceiver;

    @Before
    public void setupAlarmReceiver() {
        workModeAlarmReceiver = new WorkModeAlarmReceiver();
        workModeAlarmReceiver.setWorkModeService(workModeService);
        workModeAlarmReceiver.setAlarmManager(alarmManager);
        setupDaggerMocks();
    }

    @Test
    public void shouldSetToSilentModeWhenAlarmHasBeenTriggered() {
        when(intent.getAction()).thenReturn(START_WORK_ACTION_IDENTIFIER);

        workModeAlarmReceiver.onReceive(context, intent);

        verify(workModeService, only()).setToSilentMode();
    }

    @Test
    public void shouldSetToNormalModeWhenAlarmHasBeenTriggered() {
        when(intent.getAction()).thenReturn(END_WORK_ACTION_IDENTIFIER);

        workModeAlarmReceiver.onReceive(context, intent);

        verify(workModeService, only()).setToNormalMode();
    }

    private void setupDaggerMocks() {
        when(context.getApplicationContext()).thenReturn(mainApplication);
        when(mainApplication.getMainActivityComponent()).thenReturn(mainActivityComponent);
    }
}