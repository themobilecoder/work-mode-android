package com.rafaelkarlo.workmode.mainscreen.service;

import com.rafaelkarlo.workmode.mainscreen.service.audio.AudioModeService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.rafaelkarlo.workmode.mainscreen.service.audio.AudioMode.NORMAL;
import static com.rafaelkarlo.workmode.mainscreen.service.audio.AudioMode.SILENT;
import static com.rafaelkarlo.workmode.mainscreen.service.audio.AudioMode.VIBRATE;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class WorkModeAudioOverrideServiceTest {

    @Mock
    private AudioModeService audioModeService;

    private WorkModeAudioOverrideService workModeAudioOverrideService;

    @Before
    public void setup() {
        workModeAudioOverrideService = new WorkModeAudioOverrideService(audioModeService);
    }

    @Test
    public void shouldOverrideAudioModeToNormal() {
        workModeAudioOverrideService.overrideCurrentAudioMode(NORMAL);

        verify(audioModeService).setModeTo(NORMAL);
    }

    @Test
    public void shouldOverrideAudioModeToVibrate() {
        workModeAudioOverrideService.overrideCurrentAudioMode(VIBRATE);

        verify(audioModeService).setModeTo(VIBRATE);
    }

    @Test
    public void shouldOverrideAudioModeToSilent() {
        workModeAudioOverrideService.overrideCurrentAudioMode(SILENT);

        verify(audioModeService).setModeTo(SILENT);
    }

}