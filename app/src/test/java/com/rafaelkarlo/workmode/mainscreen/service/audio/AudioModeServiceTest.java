package com.rafaelkarlo.workmode.mainscreen.service.audio;

import android.content.SharedPreferences;
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
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static rx.schedulers.Schedulers.immediate;

@RunWith(MockitoJUnitRunner.class)
public class AudioModeServiceTest {

    private static final String PREVIOUS_RINGER_MODE_KEY = "PREVIOUS_RINGER_MODE";

    @Mock
    private AudioManager audioManager;

    @Mock
    private SharedPreferences sharedPreferences;

    @Mock
    private SharedPreferences.Editor sharedPreferencesEditor;

    private AudioModeService audioModeService;

    @Before
    public void setup() {
        audioModeService = new AudioModeServiceImpl(audioManager, sharedPreferences);
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

    @Test
    public void shouldSaveCurrentRingerMode() {
        when(sharedPreferences.edit()).thenReturn(sharedPreferencesEditor);
        when(sharedPreferencesEditor.putInt(anyString(), anyInt())).thenReturn(sharedPreferencesEditor);

        audioModeService.saveCurrentRingerMode(NORMAL);

        verify(sharedPreferencesEditor).putInt(PREVIOUS_RINGER_MODE_KEY, NORMAL.getIntValue());
        verify(sharedPreferencesEditor).apply();
    }

    @Test
    public void shouldGetPreviousAudioMode() {
        when(sharedPreferences.getInt(PREVIOUS_RINGER_MODE_KEY, -1)).thenReturn(NORMAL.getIntValue());

        assertThat(audioModeService.getPreviouslySavedMode()).isEqualTo(NORMAL);
    }

}