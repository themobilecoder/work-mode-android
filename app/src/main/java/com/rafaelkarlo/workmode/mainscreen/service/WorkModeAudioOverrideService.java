package com.rafaelkarlo.workmode.mainscreen.service;

import com.rafaelkarlo.workmode.mainscreen.service.audio.AudioMode;
import com.rafaelkarlo.workmode.mainscreen.service.audio.AudioModeService;

import javax.inject.Inject;

public class WorkModeAudioOverrideService {

    private final AudioModeService audioModeService;

    @Inject
    public WorkModeAudioOverrideService(AudioModeService audioModeService) {
        this.audioModeService = audioModeService;
    }

    public void overrideCurrentAudioMode(AudioMode audioMode) {
        audioModeService.setModeTo(audioMode);
    }
}
