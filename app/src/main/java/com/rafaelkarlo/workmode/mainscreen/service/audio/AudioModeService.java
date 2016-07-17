package com.rafaelkarlo.workmode.mainscreen.service.audio;

public interface AudioModeService {
    void setModeTo(AudioMode audioMode);
    void saveCurrentRingerMode(AudioMode audioMode);
    AudioMode getCurrentMode();
    AudioMode getPreviouslySavedMode();
}
