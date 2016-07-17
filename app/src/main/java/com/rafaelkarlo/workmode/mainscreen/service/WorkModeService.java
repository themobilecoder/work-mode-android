package com.rafaelkarlo.workmode.mainscreen.service;

import android.content.SharedPreferences;

import com.rafaelkarlo.workmode.mainscreen.service.audio.AudioMode;
import com.rafaelkarlo.workmode.mainscreen.service.audio.AudioModeService;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import static android.media.AudioManager.RINGER_MODE_NORMAL;
import static android.media.AudioManager.RINGER_MODE_SILENT;
import static android.media.AudioManager.RINGER_MODE_VIBRATE;
import static com.rafaelkarlo.workmode.mainscreen.service.audio.AudioMode.NORMAL;
import static com.rafaelkarlo.workmode.mainscreen.service.audio.AudioMode.SILENT;
import static org.joda.time.DateTime.now;

public class WorkModeService {

    private static final String WORK_MODE_ACTIVATED = "WORK_MODE_ACTIVATED";
    private static final String PREVIOUS_RINGER_MODE_KEY = "PREVIOUS_RINGER_MODE";

    private AudioModeService audioModeService;
    private WorkTimeService workTimeService;
    private SharedPreferences sharedPreferences;

    public WorkModeService(AudioModeService audioModeService, WorkTimeService workTimeService, SharedPreferences sharedPreferences) {
        this.audioModeService = audioModeService;
        this.workTimeService = workTimeService;
        this.sharedPreferences = sharedPreferences;
    }

    public boolean setToSilentMode() {
        if (canSetToSilentMode()) {
            saveCurrentRingerMode();
            audioModeService.setModeTo(SILENT);
            return true;
        } else {
            return false;
        }
    }

    public boolean setToNormalMode() {
        if (canSetToNormalMode()) {
            audioModeService.setModeTo(NORMAL);
            return true;
        } else {
            return false;
        }
    }

    public void setToPreviousMode() {
        audioModeService.setModeTo(getPreviousRingerMode());
    }

    public void activate() {
        saveModeActivated();
    }

    public void deactivate() {
        saveModeDeactivated();
    }

    public boolean isActivated() {
        return sharedPreferences.getBoolean(WORK_MODE_ACTIVATED, false);
    }

    public void setStartTime(LocalTime workStartTime) {
        workTimeService.setStartWorkTime(workStartTime);
    }

    public void setEndTime(LocalTime workEndTime) {
        workTimeService.setEndWorkTime(workEndTime);
    }

    public LocalTime getStartTime() {
        return workTimeService.getStartWorkTime();
    }

    public LocalTime getEndTime() {
        return workTimeService.getEndWorkTime();
    }

    private AudioMode getPreviousRingerMode() {

        return transformToAudioMode(sharedPreferences.getInt(PREVIOUS_RINGER_MODE_KEY, -1));
    }

    private AudioMode transformToAudioMode(int audioModeInInt) {
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


    private boolean canSetToSilentMode() {
        return nowIsWithinWorkHours() && isActivated();
    }

    private boolean canSetToNormalMode() {
        return nowIsAfterWorkHours() && isActivated();
    }

    private boolean nowIsWithinWorkHours() {
        int startTimeInSecondsOfDay = workTimeService.getStartWorkTime().getMillisOfDay();
        int endTimeInSecondsOfDay = workTimeService.getEndWorkTime().getMillisOfDay();
        DateTime now = now();

        return startTimeInSecondsOfDay <= now.getMillisOfDay() && now.getMillisOfDay() <= endTimeInSecondsOfDay;
    }

    private boolean nowIsAfterWorkHours() {
        int endTimeInSecondsOfDay = workTimeService.getEndWorkTime().getMillisOfDay();
        return now().getMillisOfDay() >= endTimeInSecondsOfDay;
    }

    private void saveCurrentRingerMode() {
        AudioMode currentRingerMode = audioModeService.getCurrentMode();
        sharedPreferences.edit().putInt(PREVIOUS_RINGER_MODE_KEY, currentRingerMode.getIntValue()).apply();
    }

    private void saveModeActivated() {
        saveModeInSharedPreferences(true);
    }

    private void saveModeDeactivated() {
        saveModeInSharedPreferences(false);
    }

    private void saveModeInSharedPreferences(boolean activated) {
        sharedPreferences.edit().putBoolean(WORK_MODE_ACTIVATED, activated).apply();
    }
}
