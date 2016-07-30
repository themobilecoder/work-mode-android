package com.rafaelkarlo.workmode.mainscreen.service.alarm;

import android.content.Context;

import com.rafaelkarlo.workmode.MainApplication;
import com.rafaelkarlo.workmode.mainscreen.config.MainActivityComponent;
import com.rafaelkarlo.workmode.mainscreen.service.WorkModeService;
import com.rafaelkarlo.workmode.mainscreen.service.time.WorkTimeServiceImpl;

import org.joda.time.LocalTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkModeAlarmOnBootSchedulerTest {

    @Mock
    private WorkTimeServiceImpl workTimeService;

    @Mock
    private WorkModeAlarmImpl workModeAlarm;

    @Mock
    private WorkModeService workModeService;

    @Mock
    private Context context;

    @Mock
    private MainApplication mainApplication;

    @Mock
    private MainActivityComponent mainActivityComponent;

    private WorkModeAlarmOnBootScheduler workModeAlarmOnBootScheduler;

    @Before
    public void setup() {
        workModeAlarmOnBootScheduler = new WorkModeAlarmOnBootScheduler();
        workModeAlarmOnBootScheduler.setWorkTimeService(workTimeService);
        workModeAlarmOnBootScheduler.setAlarmService(workModeAlarm);
        workModeAlarmOnBootScheduler.setWorkModeService(workModeService);

        setupDaggerMocks();
    }

    @After
    public void verifyDependenciesHaveBeenInjected() {
        verify(mainApplication).getMainActivityComponent();
        verify(mainActivityComponent).inject(workModeAlarmOnBootScheduler);
    }

    @Test
    public void shouldSetTheAlarmsForWorkModeOnBootWhenActivated() {
        LocalTime startWorkTime = new LocalTime(9, 0, 0);
        LocalTime endWorkTime = new LocalTime(17, 0, 0);
        when(workTimeService.getStartWorkTime()).thenReturn(startWorkTime);
        when(workTimeService.getEndWorkTime()).thenReturn(endWorkTime);
        when(workModeService.isActivated()).thenReturn(true);

        workModeAlarmOnBootScheduler.onReceive(context, null);

        verify(workModeAlarm).startAlarm(startWorkTime, endWorkTime);
    }

    @Test
    public void shouldNotSetTheAlarmsWhenDeactivated() {
        LocalTime startWorkTime = new LocalTime(9, 0, 0);
        LocalTime endWorkTime = new LocalTime(17, 0, 0);
        when(workTimeService.getStartWorkTime()).thenReturn(startWorkTime);
        when(workTimeService.getEndWorkTime()).thenReturn(endWorkTime);
        when(workModeService.isActivated()).thenReturn(false);

        workModeAlarmOnBootScheduler.onReceive(context, null);

        verifyZeroInteractions(workModeAlarm);
    }

    @Test
    public void shouldNotSetAlarmWhenStartWorkTimeIsNull() {
        LocalTime endWorkTime = new LocalTime(17, 0, 0);
        when(workTimeService.getStartWorkTime()).thenReturn(null);
        when(workTimeService.getEndWorkTime()).thenReturn(endWorkTime);

        workModeAlarmOnBootScheduler.onReceive(context, null);

        verifyNoMoreInteractions(workModeAlarm);
    }

    @Test
    public void shouldNotSetAlarmWhenEndWorkTimeIsNull() {
        LocalTime startWorkTime = new LocalTime(9, 0, 0);
        when(workTimeService.getEndWorkTime()).thenReturn(null);
        when(workTimeService.getStartWorkTime()).thenReturn(startWorkTime);

        workModeAlarmOnBootScheduler.onReceive(context, null);

        verifyNoMoreInteractions(workModeAlarm);
    }

    private void setupDaggerMocks() {
        when(context.getApplicationContext()).thenReturn(mainApplication);
        when(mainApplication.getMainActivityComponent()).thenReturn(mainActivityComponent);
    }

}