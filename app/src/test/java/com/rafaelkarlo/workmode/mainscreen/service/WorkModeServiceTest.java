package com.rafaelkarlo.workmode.mainscreen.service;


import android.content.SharedPreferences;
import android.media.AudioManager;

import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static android.media.AudioManager.RINGER_MODE_NORMAL;
import static android.media.AudioManager.RINGER_MODE_SILENT;
import static com.google.common.truth.Truth.assertThat;
import static org.joda.time.DateTimeUtils.setCurrentMillisFixed;
import static org.joda.time.DateTimeUtils.setCurrentMillisSystem;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkModeServiceTest {

    public static final LocalDateTime START_WORK_TIME = new LocalDateTime(2016, 7, 28, 9, 0, 0);
    private static final LocalDateTime END_WORK_TIME = new LocalDateTime(2016, 7, 28, 17, 0, 0);
    public static final String WORK_MODE_ACTIVATED_KEY = "WORK_MODE_ACTIVATED";
    public static final String WORK_START_TIME_KEY = "WORK_START_TIME";
    public static final String WORK_END_TIME_KEY = "WORK_END_TIME";
    private WorkModeService workModeService;

    @Mock
    AudioManager audioManager;

    @Mock
    SharedPreferences sharedPreferences;

    @Mock
    SharedPreferences.Editor sharedPreferencesEditor;

    @Before
    public void setup() {
        workModeService = new WorkModeService(audioManager, sharedPreferences);
        resetToPresent();
    }

    @Test
    public void shouldOnlySetAModeIfWorkModeIsActivated() {
        setWorkHours();
        setWorkModeToDeactivatedMode();
        setCurrentTime(START_WORK_TIME);

        assertThat(workModeService.setToSilentMode()).isFalse();

        setCurrentTime(END_WORK_TIME);

        assertThat(workModeService.setToNormalMode()).isFalse();

        verifyZeroInteractions(audioManager);
    }

    @Test
    public void shouldSetToSilentModeDuringWorkHours() {
        setWorkHours();
        setWorkModeToActivatedMode();
        setCurrentTime(START_WORK_TIME);
        when(audioManager.getRingerMode()).thenReturn(RINGER_MODE_SILENT);

        assertThat(workModeService.setToSilentMode()).isTrue();

        verify(audioManager).setRingerMode(RINGER_MODE_SILENT);
    }

    @Test
    public void shouldNotSetToSilentModeWhenBeforeWorkHours() {
        setWorkStartTime();
        setCurrentTime(START_WORK_TIME.minusMinutes(1));

        assertThat(workModeService.setToSilentMode()).isFalse();

        verifyZeroInteractions(audioManager);
    }

    @Test
    public void shouldNotSetToSilentModeAfterWorkHours() {
        setWorkEndTime();
        setCurrentTime(END_WORK_TIME.plusMinutes(1));

        assertThat(workModeService.setToSilentMode()).isFalse();
        verifyZeroInteractions(audioManager);
    }

    @Test
    public void shouldSetToNormalModeAfterWorkHours() {
        setWorkModeToActivatedMode();
        setWorkEndTime();
        when(audioManager.getRingerMode()).thenReturn(RINGER_MODE_NORMAL);

        setCurrentTime(END_WORK_TIME);

        assertThat(workModeService.setToNormalMode()).isTrue();

        verify(audioManager).setRingerMode(RINGER_MODE_NORMAL);
    }

    @Test
    public void shouldNotSetToNormalModeBeforeEndOfWorkHours() {
        setWorkEndTime();

        setCurrentTime(END_WORK_TIME.minusMinutes(1));

        assertThat(workModeService.setToNormalMode()).isFalse();

        verifyZeroInteractions(audioManager);
    }

    @Test
    public void shouldPersistWhenWorkModeIsActivated() {
        when(sharedPreferences.edit()).thenReturn(sharedPreferencesEditor);
        when(sharedPreferencesEditor.putBoolean(WORK_MODE_ACTIVATED_KEY, true)).thenReturn(sharedPreferencesEditor);
        setWorkModeToActivatedMode();

        workModeService.activate();

        assertThat(workModeService.isActivated()).isTrue();

        verify(sharedPreferences).edit();
        verify(sharedPreferencesEditor).putBoolean(WORK_MODE_ACTIVATED_KEY, true);
        verify(sharedPreferencesEditor).apply();
        verify(sharedPreferences).getBoolean(WORK_MODE_ACTIVATED_KEY, false);
    }

    @Test
    public void shouldPersistWhenWorkModeIsDeactivated() {
        setWorkModeToDeactivatedMode();
        when(sharedPreferences.edit()).thenReturn(sharedPreferencesEditor);
        when(sharedPreferencesEditor.putBoolean(WORK_MODE_ACTIVATED_KEY, false)).thenReturn(sharedPreferencesEditor);

        workModeService.deactivate();

        assertThat(workModeService.isActivated()).isFalse();

        verify(sharedPreferences).edit();
        verify(sharedPreferencesEditor).putBoolean(WORK_MODE_ACTIVATED_KEY, false);
        verify(sharedPreferencesEditor).apply();
        verify(sharedPreferences).getBoolean(WORK_MODE_ACTIVATED_KEY, false);
    }

    private void setWorkHours() {
        setWorkStartTime();
        setWorkEndTime();
    }

    private void setWorkStartTime() {
        when(sharedPreferences.getInt(WORK_START_TIME_KEY, 0)).thenReturn((START_WORK_TIME.toDateTime().getSecondOfDay()));
    }

    private void setWorkEndTime() {
        when(sharedPreferences.getInt(WORK_END_TIME_KEY, 0)).thenReturn((END_WORK_TIME.toDateTime().getSecondOfDay()));
    }

    private void setWorkModeToActivatedMode() {
        when(sharedPreferences.getBoolean(WORK_MODE_ACTIVATED_KEY, false)).thenReturn(true);
    }

    private void setWorkModeToDeactivatedMode() {
        when(sharedPreferences.getBoolean(WORK_MODE_ACTIVATED_KEY, false)).thenReturn(false);
    }


    private static void setCurrentTime(LocalDateTime localDateTime) {
        setCurrentMillisFixed(localDateTime.toDate().getTime());
    }

    private static void resetToPresent() {
        setCurrentMillisSystem();
    }

}