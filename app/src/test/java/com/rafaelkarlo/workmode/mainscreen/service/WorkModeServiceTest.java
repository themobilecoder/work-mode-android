package com.rafaelkarlo.workmode.mainscreen.service;


import android.content.SharedPreferences;
import android.media.AudioManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static android.media.AudioManager.RINGER_MODE_NORMAL;
import static android.media.AudioManager.RINGER_MODE_SILENT;
import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkModeServiceTest {

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
    }

    @Test
    public void shouldSetToSilentMode() {
        when(audioManager.getRingerMode()).thenReturn(RINGER_MODE_SILENT);

        assertThat(workModeService.setToSilentMode()).isTrue();

        verify(audioManager).setRingerMode(RINGER_MODE_SILENT);
    }

    @Test
    public void shouldSetToNormalMode() {
        when(audioManager.getRingerMode()).thenReturn(RINGER_MODE_NORMAL);

        assertThat(workModeService.setToNormalMode()).isTrue();

        verify(audioManager).setRingerMode(RINGER_MODE_NORMAL);
    }

    @Test
    public void shouldReturnFalseIfSilentModeHasNotBeenSetSuccessfully() {
        when(audioManager.getRingerMode()).thenReturn(RINGER_MODE_NORMAL);

        assertThat(workModeService.setToSilentMode()).isFalse();

        verify(audioManager).setRingerMode(RINGER_MODE_SILENT);
    }

    @Test
    public void shouldReturnFalseIfNormalModeHasNotBeenSetSuccessfully() {
        when(audioManager.getRingerMode()).thenReturn(RINGER_MODE_SILENT);

        assertThat(workModeService.setToNormalMode()).isFalse();

        verify(audioManager).setRingerMode(RINGER_MODE_NORMAL);
    }

    @Test
    public void shouldPersistWhenWorkModeIsActivated() {
        when(sharedPreferences.edit()).thenReturn(sharedPreferencesEditor);
        when(sharedPreferencesEditor.putBoolean("WORK_MODE_ACTIVATED", true)).thenReturn(sharedPreferencesEditor);
        when(sharedPreferences.getBoolean("WORK_MODE_ACTIVATED", false)).thenReturn(true);

        workModeService.activate();
        assertThat(workModeService.isActivated()).isTrue();

        verify(sharedPreferences).edit();
        verify(sharedPreferencesEditor).putBoolean("WORK_MODE_ACTIVATED", true);
        verify(sharedPreferencesEditor).apply();

        verify(sharedPreferences).getBoolean("WORK_MODE_ACTIVATED", false);
    }

    @Test
    public void shouldPersistWhenWorkModeIsDeactivated() {
        when(sharedPreferences.edit()).thenReturn(sharedPreferencesEditor);
        when(sharedPreferencesEditor.putBoolean("WORK_MODE_ACTIVATED", false)).thenReturn(sharedPreferencesEditor);
        when(sharedPreferences.getBoolean("WORK_MODE_ACTIVATED", false)).thenReturn(false);

        workModeService.deactivate();
        assertThat(workModeService.isActivated()).isFalse();

        verify(sharedPreferences).edit();
        verify(sharedPreferencesEditor).putBoolean("WORK_MODE_ACTIVATED", false);
        verify(sharedPreferencesEditor).apply();

        verify(sharedPreferences).getBoolean("WORK_MODE_ACTIVATED", false);
    }

}