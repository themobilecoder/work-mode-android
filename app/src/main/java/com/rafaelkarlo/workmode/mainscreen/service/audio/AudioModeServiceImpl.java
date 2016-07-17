package com.rafaelkarlo.workmode.mainscreen.service.audio;

import android.content.SharedPreferences;
import android.media.AudioManager;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;

import static android.media.AudioManager.RINGER_MODE_NORMAL;
import static android.media.AudioManager.RINGER_MODE_SILENT;
import static android.media.AudioManager.RINGER_MODE_VIBRATE;
import static rx.android.schedulers.AndroidSchedulers.mainThread;
import static rx.schedulers.Schedulers.io;

public class AudioModeServiceImpl implements AudioModeService {

    private static final String PREVIOUS_RINGER_MODE_KEY = "PREVIOUS_RINGER_MODE";

    private AudioManager audioManager;
    private SharedPreferences sharedPreferences;

    @Inject
    public AudioModeServiceImpl(AudioManager audioManager, SharedPreferences sharedPreferences) {
        this.audioManager = audioManager;
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public void setModeTo(AudioMode audioMode) {
        switch (audioMode) {
            case NORMAL:
                setNormalModeTask
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribe();
                break;
            case VIBRATE:
                setVibrateModeTask
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribe();
                break;
            case SILENT:
                setSilentModeTask
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribe();
                break;
            default:
                break;
        }
    }

    @Override
    public void saveCurrentRingerMode(AudioMode audioMode) {
        sharedPreferences.edit()
                .putInt(PREVIOUS_RINGER_MODE_KEY, audioMode.getIntValue())
                .apply();
    }

    @Override
    public AudioMode getCurrentMode() {
        return transformToAudioMode(audioManager.getRingerMode());
    }

    @Override
    public AudioMode getPreviouslySavedMode() {
        return transformToAudioMode(sharedPreferences.getInt(PREVIOUS_RINGER_MODE_KEY, -1));
    }

    private final Observable<Void> setSilentModeTask = Observable.create(new Observable.OnSubscribe<Void>() {
        @Override
        public void call(Subscriber<? super Void> subscriber) {
            setRingerModeTo(RINGER_MODE_SILENT);
        }
    });

    private final Observable<Void> setVibrateModeTask = Observable.create(new Observable.OnSubscribe<Void>() {
        @Override
        public void call(Subscriber<? super Void> subscriber) {
            setRingerModeTo(RINGER_MODE_VIBRATE);
        }
    });

    private final Observable<Void> setNormalModeTask = Observable.create(new Observable.OnSubscribe<Void>() {
        @Override
        public void call(Subscriber<? super Void> subscriber) {
            setRingerModeTo(RINGER_MODE_NORMAL);
        }
    });

    private void setRingerModeTo(int mode) {
        try {
            audioManager.setRingerMode(mode);
            Thread.sleep(1000);
            audioManager.setRingerMode(mode);
        } catch (InterruptedException e) {
        }
    }

    private static AudioMode transformToAudioMode(int audioModeInInt) {
        switch (audioModeInInt) {
            case RINGER_MODE_NORMAL:
                return AudioMode.NORMAL;
            case RINGER_MODE_VIBRATE:
                return AudioMode.VIBRATE;
            case RINGER_MODE_SILENT:
                return AudioMode.SILENT;
            default:
                return AudioMode.UNKNOWN;
        }
    }
}
