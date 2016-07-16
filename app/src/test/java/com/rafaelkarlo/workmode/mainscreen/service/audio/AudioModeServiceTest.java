package com.rafaelkarlo.workmode.mainscreen.service.audio;

import android.media.AudioManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;

import static android.media.AudioManager.RINGER_MODE_NORMAL;
import static android.media.AudioManager.RINGER_MODE_SILENT;
import static android.media.AudioManager.RINGER_MODE_VIBRATE;
import static com.google.common.truth.Truth.assertThat;
import static com.rafaelkarlo.workmode.mainscreen.service.audio.AudioMode.NORMAL;
import static com.rafaelkarlo.workmode.mainscreen.service.audio.AudioMode.SILENT;
import static com.rafaelkarlo.workmode.mainscreen.service.audio.AudioMode.VIBRATE;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static rx.schedulers.Schedulers.immediate;

@RunWith(MockitoJUnitRunner.class)
public class AudioModeServiceTest {

    @Mock
    private AudioManager audioManager;

    private AudioModeService audioModeService;

    @Before
    public void setup() {
        audioModeService = new AudioModeServiceImpl(audioManager);
    }

    @Before
    public void setupSchedulers() throws Exception {
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return immediate();
            }
        });
    }

    @After
    public void tearDown() {
        RxAndroidPlugins.getInstance().reset();
    }

    @Test
    public void shouldSetRingerMode() {
        audioModeService.setModeTo(NORMAL);
        audioModeService.setModeTo(VIBRATE);
        audioModeService.setModeTo(SILENT);

        verify(audioManager, atLeastOnce()).setRingerMode(RINGER_MODE_NORMAL);
        verify(audioManager, atLeastOnce()).setRingerMode(RINGER_MODE_VIBRATE);
        verify(audioManager, atLeastOnce()).setRingerMode(RINGER_MODE_SILENT);
    }

    @Test
    public void shouldReturnCurrentRingerMode() {
        when(audioManager.getRingerMode()).thenReturn(RINGER_MODE_NORMAL);

        assertThat(audioModeService.getCurrentMode()).isEqualTo(NORMAL);
    }

}