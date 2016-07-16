package com.rafaelkarlo.workmode.mainscreen.service;


import android.content.SharedPreferences;
import android.media.AudioManager;

import com.rafaelkarlo.workmode.mainscreen.service.audio.AudioModeService;

import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.google.common.truth.Truth.assertThat;
import static com.rafaelkarlo.workmode.mainscreen.service.audio.AudioMode.NORMAL;
import static com.rafaelkarlo.workmode.mainscreen.service.audio.AudioMode.SILENT;
import static com.rafaelkarlo.workmode.mainscreen.service.audio.AudioMode.VIBRATE;
import static org.joda.time.DateTimeUtils.setCurrentMillisFixed;
import static org.joda.time.DateTimeUtils.setCurrentMillisSystem;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkModeServiceTest {

    public static final LocalTime START_WORK_TIME = new LocalTime(9, 0, 0);
    private static final LocalTime END_WORK_TIME = new LocalTime(17, 0, 0);
    public static final String WORK_MODE_ACTIVATED_KEY = "WORK_MODE_ACTIVATED";
    public static final String WORK_START_TIME_KEY = "WORK_START_TIME";
    public static final String WORK_END_TIME_KEY = "WORK_END_TIME";
    private static final String PREVIOUS_RINGER_MODE_KEY = "PREVIOUS_RINGER_MODE";
    private WorkModeService workModeService;

    @Mock
    AudioModeService audioModeService;

    @Mock
    SharedPreferences sharedPreferences;

    @Mock
    SharedPreferences.Editor sharedPreferencesEditor;

    @Before
    public void setup() {
        workModeService = new WorkModeService(audioModeService, sharedPreferences);
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

        verifyZeroInteractions(audioModeService);
    }

    @Test
    public void shouldSetToSilentModeDuringWorkHours() {
        setWorkHours();
        setWorkModeToActivatedMode();
        setCurrentTime(START_WORK_TIME);
        when(audioModeService.getCurrentMode()).thenReturn(SILENT);
        setupMockForSavingPreviousMode();

        assertThat(workModeService.setToSilentMode()).isTrue();

        verify(audioModeService).setModeTo(SILENT);
    }

    @Test
    public void shouldNotSetToSilentModeWhenBeforeWorkHours() {
        setWorkStartTime();
        setCurrentTime(START_WORK_TIME.minusMinutes(1));

        assertThat(workModeService.setToSilentMode()).isFalse();

        verifyZeroInteractions(audioModeService);
    }

    @Test
    public void shouldNotSetToSilentModeAfterWorkHours() {
        setWorkEndTime();
        setCurrentTime(END_WORK_TIME.plusMinutes(1));

        assertThat(workModeService.setToSilentMode()).isFalse();
        verifyZeroInteractions(audioModeService);
    }

    @Test
    public void shouldSetToNormalModeAfterWorkHours() {
        setWorkModeToActivatedMode();
        setWorkEndTime();
        when(audioModeService.getCurrentMode()).thenReturn(NORMAL);

        setCurrentTime(END_WORK_TIME);

        assertThat(workModeService.setToNormalMode()).isTrue();

        verify(audioModeService).setModeTo(NORMAL);
    }

    @Test
    public void shouldNotSetToNormalModeBeforeEndOfWorkHours() {
        setWorkEndTime();

        setCurrentTime(END_WORK_TIME.minusMinutes(1));

        assertThat(workModeService.setToNormalMode()).isFalse();

        verifyZeroInteractions(audioModeService);
    }

    @Test
    public void shouldPersistWhenWorkModeIsActivated() {
        when(sharedPreferences.edit()).thenReturn(sharedPreferencesEditor);
        when(sharedPreferencesEditor.putBoolean(WORK_MODE_ACTIVATED_KEY, true)).thenReturn(sharedPreferencesEditor);
        when(audioModeService.getCurrentMode()).thenReturn(VIBRATE);
        when(sharedPreferencesEditor.putInt(PREVIOUS_RINGER_MODE_KEY, AudioManager.RINGER_MODE_VIBRATE)).thenReturn(sharedPreferencesEditor);
        setWorkModeToActivatedMode();

        workModeService.activate();

        assertThat(workModeService.isActivated()).isTrue();

        verify(sharedPreferences, atLeastOnce()).edit();
        verify(sharedPreferencesEditor).putBoolean(WORK_MODE_ACTIVATED_KEY, true);
        verify(sharedPreferencesEditor, atLeastOnce()).apply();
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

    @Test
    public void shouldPersistWorkHours() {
        when(sharedPreferences.edit()).thenReturn(sharedPreferencesEditor);
        when(sharedPreferencesEditor.putInt(WORK_START_TIME_KEY, START_WORK_TIME.getMillisOfDay())).thenReturn(sharedPreferencesEditor);
        when(sharedPreferencesEditor.putInt(WORK_END_TIME_KEY, END_WORK_TIME.getMillisOfDay())).thenReturn(sharedPreferencesEditor);

        workModeService.setWorkHours(START_WORK_TIME, END_WORK_TIME);

        verify(sharedPreferences).edit();
        verify(sharedPreferencesEditor).putInt(WORK_START_TIME_KEY, START_WORK_TIME.getMillisOfDay());
        verify(sharedPreferencesEditor).putInt(WORK_END_TIME_KEY, END_WORK_TIME.getMillisOfDay());
        verify(sharedPreferencesEditor).apply();
    }

    @Test
    public void shouldPersistCurrentModeWhenSettingTheMode() {


        workModeService.setToSilentMode();
    }

    @Test
    public void shouldNotAllowStartOfWorkTimeAfterTheEndOfWorkTime() {
        when(sharedPreferences.edit()).thenReturn(sharedPreferencesEditor);
        when(sharedPreferencesEditor.putInt(WORK_START_TIME_KEY, END_WORK_TIME.getMillisOfDay())).thenReturn(sharedPreferencesEditor);
        when(sharedPreferencesEditor.putInt(WORK_END_TIME_KEY, START_WORK_TIME.getMillisOfDay())).thenReturn(sharedPreferencesEditor);

        try {
            workModeService.setWorkHours(END_WORK_TIME, START_WORK_TIME);
            fail("Should throw an illegal argument exception");
        } catch (IllegalArgumentException exception) {
            //Ignore Exception
        }

    }

    private void setupMockForSavingPreviousMode() {
        when(sharedPreferences.edit()).thenReturn(sharedPreferencesEditor);
        when(sharedPreferencesEditor.putInt(PREVIOUS_RINGER_MODE_KEY, AudioManager.RINGER_MODE_SILENT)).thenReturn(sharedPreferencesEditor);
    }

    private void setWorkHours() {
        setWorkStartTime();
        setWorkEndTime();
    }

    private void setWorkStartTime() {
        when(sharedPreferences.getInt(WORK_START_TIME_KEY, 0)).thenReturn((START_WORK_TIME.getMillisOfDay()));
    }

    private void setWorkEndTime() {
        when(sharedPreferences.getInt(WORK_END_TIME_KEY, 0)).thenReturn((END_WORK_TIME.getMillisOfDay()));
    }

    private void setWorkModeToActivatedMode() {
        when(sharedPreferences.getBoolean(WORK_MODE_ACTIVATED_KEY, false)).thenReturn(true);
    }

    private void setWorkModeToDeactivatedMode() {
        when(sharedPreferences.getBoolean(WORK_MODE_ACTIVATED_KEY, false)).thenReturn(false);
    }

    private static void setCurrentTime(LocalTime localTime) {
        setCurrentMillisFixed(localTime.toDateTimeToday().getMillis());
    }

    private static void resetToPresent() {
        setCurrentMillisSystem();
    }

}