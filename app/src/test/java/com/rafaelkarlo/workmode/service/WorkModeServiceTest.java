package com.rafaelkarlo.workmode.service;


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

    @Before
    public void setup() {
        workModeService = new WorkModeService(audioManager);
    }

    @Test
    public void shouldSetToWorkMode() {
        when(audioManager.getRingerMode()).thenReturn(RINGER_MODE_SILENT);

        assertThat(workModeService.activateWorkMode()).isTrue();

        verify(audioManager).setRingerMode(RINGER_MODE_SILENT);
    }

    @Test
    public void shouldDisableWorkMode() {
        when(audioManager.getRingerMode()).thenReturn(RINGER_MODE_NORMAL);

        assertThat(workModeService.deactivateWorkMode()).isTrue();

        verify(audioManager).setRingerMode(RINGER_MODE_NORMAL);
    }

    @Test
    public void shouldReturnFalseIfWorkModeHasNotBeenSetSuccessfully() {
        when(audioManager.getRingerMode()).thenReturn(RINGER_MODE_NORMAL);

        assertThat(workModeService.activateWorkMode()).isFalse();

        verify(audioManager).setRingerMode(RINGER_MODE_SILENT);
    }

    @Test
    public void shouldReturnFalseIfDeactivateWorkModeHasNotBeenSetSuccessfully() {
        when(audioManager.getRingerMode()).thenReturn(RINGER_MODE_SILENT);

        assertThat(workModeService.deactivateWorkMode()).isFalse();

        verify(audioManager).setRingerMode(RINGER_MODE_NORMAL);
    }

}