package com.rafaelkarlo.workmode.service;


import android.media.AudioManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class WorkModeServiceTest {

    private WorkModeService workModeService;

    @Mock
    AudioManager audioManager;

    @Before
    public void setup() {
        workModeService = new WorkModeService();
        workModeService.setAudioManager(audioManager);
    }

    @Test
    public void shouldSetToWorkMode() {
        assertThat(workModeService.activateWorkMode()).isTrue();
        verify(audioManager).setRingerMode(AudioManager.RINGER_MODE_SILENT);
    }

    @Test
    public void shouldDisableWorkMode() {
        assertThat(workModeService.deactivateWorkMode()).isTrue();
        verify(audioManager).setRingerMode(AudioManager.RINGER_MODE_NORMAL);
    }

}