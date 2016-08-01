package com.rafaelkarlo.workmode.mainscreen.service;


import android.content.SharedPreferences;

import com.rafaelkarlo.workmode.mainscreen.service.audio.AudioModeService;
import com.rafaelkarlo.workmode.mainscreen.service.time.WorkTimeService;
import com.rafaelkarlo.workmode.mainscreen.service.time.WorkDay;

import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.Set;

import static com.google.common.truth.Truth.assertThat;
import static com.rafaelkarlo.workmode.mainscreen.service.audio.AudioMode.NORMAL;
import static com.rafaelkarlo.workmode.mainscreen.service.audio.AudioMode.SILENT;
import static com.rafaelkarlo.workmode.mainscreen.service.audio.AudioMode.VIBRATE;
import static java.util.Arrays.asList;
import static org.joda.time.DateTimeUtils.setCurrentMillisFixed;
import static org.joda.time.DateTimeUtils.setCurrentMillisSystem;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkModeServiceTest {

    public static final LocalTime START_WORK_TIME = new LocalTime(9, 0, 0);
    private static final LocalTime END_WORK_TIME = new LocalTime(17, 0, 0);
    public static final String WORK_MODE_ACTIVATED_KEY = "WORK_MODE_ACTIVATED";
    private WorkModeService workModeService;

    @Mock
    AudioModeService audioModeService;

    @Mock
    WorkTimeService workTimeService;

    @Mock
    SharedPreferences sharedPreferences;

    @Mock
    SharedPreferences.Editor sharedPreferencesEditor;

    @Before
    public void setup() {
        workModeService = new WorkModeService(audioModeService, workTimeService, sharedPreferences);
        resetToPresent();
    }

    @Before
    public void setEverydayAsWorkDays() {
        when(workTimeService.getWorkDays()).thenReturn(new HashSet<>(asList(
                WorkDay.SUNDAY,
                WorkDay.MONDAY,
                WorkDay.TUESDAY,
                WorkDay.WEDNESDAY,
                WorkDay.THURSDAY,
                WorkDay.FRIDAY,
                WorkDay.SATURDAY,
                WorkDay.SUNDAY
        )));
    }

    @Test
    public void shouldOnlySetAModeIfWorkModeIsActivated() {
        setWorkHours();
        setWorkModeToDeactivatedMode();
        setCurrentTime(START_WORK_TIME);

        assertThat(workModeService.setToSilentMode()).isFalse();

        setCurrentTime(END_WORK_TIME);

        assertThat(workModeService.setToNormalMode()).isFalse();

        verifyZeroInteractions(audioModeService);
    }

    @Test
    public void shouldSetToSilentModeDuringWorkHours() {
        setWorkHours();
        setWorkModeToActivatedMode();
        setCurrentTime(START_WORK_TIME);
        when(audioModeService.getCurrentMode()).thenReturn(SILENT);

        assertThat(workModeService.setToSilentMode()).isTrue();

        verify(audioModeService).setModeTo(SILENT);
    }

    @Test
    public void shouldNotSetToSilentModeWhenBeforeWorkHours() {
        setWorkHours();
        setCurrentTime(START_WORK_TIME.minusMinutes(1));

        assertThat(workModeService.setToSilentMode()).isFalse();

        verifyZeroInteractions(audioModeService);
    }

    @Test
    public void shouldSetToSilentDuringWorkHoursOnANightShiftBeforeMidnight() {
        LocalTime eveningStartWorkTime = new LocalTime(20, 0);
        LocalTime morningEndWorkTime = new LocalTime(4, 0);
        when(workTimeService.getStartWorkTime()).thenReturn(eveningStartWorkTime);
        when(workTimeService.getEndWorkTime()).thenReturn(morningEndWorkTime);
        setWorkModeToActivatedMode();

        LocalTime currentTimeBeforeMidnight = eveningStartWorkTime.plusMinutes(1);
        setCurrentTime(currentTimeBeforeMidnight);

        assertThat(workModeService.setToSilentMode()).isTrue();

        verify(audioModeService).setModeTo(SILENT);
    }

    @Test
    public void shouldSetToSilentDuringWorkHoursOnANightShiftAfterMidnight() {
        LocalTime eveningStartWorkTime = new LocalTime(20, 0);
        LocalTime morningEndWorkTime = new LocalTime(4, 0);
        when(workTimeService.getStartWorkTime()).thenReturn(eveningStartWorkTime);
        when(workTimeService.getEndWorkTime()).thenReturn(morningEndWorkTime);
        setWorkModeToActivatedMode();

        LocalTime currentTimeAfterMidnight = new LocalTime(1, 0, 0);
        setCurrentTime(currentTimeAfterMidnight);

        assertThat(workModeService.setToSilentMode()).isTrue();

        verify(audioModeService).setModeTo(SILENT);
    }

    @Test
    public void shouldNotSetToSilentModeAfterWorkHours() {
        setWorkHours();
        setCurrentTime(END_WORK_TIME.plusMinutes(1));

        assertThat(workModeService.setToSilentMode()).isFalse();
        verifyZeroInteractions(audioModeService);
    }

    @Test
    public void shouldSetToNormalModeAfterWorkHours() {
        setWorkModeToActivatedMode();
        when(workTimeService.getEndWorkTime()).thenReturn((END_WORK_TIME));
        when(audioModeService.getCurrentMode()).thenReturn(NORMAL);

        setCurrentTime(END_WORK_TIME);

        assertThat(workModeService.setToNormalMode()).isTrue();

        verify(audioModeService).setModeTo(NORMAL);
    }

    @Test
    public void shouldNotSetToNormalModeBeforeEndOfWorkHours() {
        when(workTimeService.getEndWorkTime()).thenReturn((END_WORK_TIME));

        setCurrentTime(END_WORK_TIME.minusMinutes(1));

        assertThat(workModeService.setToNormalMode()).isFalse();

        verifyZeroInteractions(audioModeService);
    }

    @Test
    public void shouldPersistWhenWorkModeIsActivated() {
        when(sharedPreferences.edit()).thenReturn(sharedPreferencesEditor);
        when(sharedPreferencesEditor.putBoolean(WORK_MODE_ACTIVATED_KEY, true)).thenReturn(sharedPreferencesEditor);
        when(audioModeService.getCurrentMode()).thenReturn(VIBRATE);
        setWorkModeToActivatedMode();

        workModeService.activate();

        assertThat(workModeService.isActivated()).isTrue();

        verify(sharedPreferences, atLeastOnce()).edit();
        verify(sharedPreferencesEditor).putBoolean(WORK_MODE_ACTIVATED_KEY, true);
        verify(sharedPreferencesEditor, atLeastOnce()).apply();
        verify(sharedPreferences).getBoolean(WORK_MODE_ACTIVATED_KEY, false);
    }

    @Test
    public void shouldPersistWhenWorkModeIsDeactivated() {
        setWorkModeToDeactivatedMode();
        when(sharedPreferences.edit()).thenReturn(sharedPreferencesEditor);
        when(sharedPreferencesEditor.putBoolean(WORK_MODE_ACTIVATED_KEY, false)).thenReturn(sharedPreferencesEditor);

        workModeService.deactivate();

        assertThat(workModeService.isActivated()).isFalse();

        verify(sharedPreferences).edit();
        verify(sharedPreferencesEditor).putBoolean(WORK_MODE_ACTIVATED_KEY, false);
        verify(sharedPreferencesEditor).apply();
        verify(sharedPreferences).getBoolean(WORK_MODE_ACTIVATED_KEY, false);
    }

    @Test
    public void shouldPersistCurrentModeWhenSettingTheMode() {
        setWorkHours();
        workModeService.setToSilentMode();
    }

    @Test
    public void shouldSetToSilentModeOnWorkDays() {
        setWorkHours();
        setWorkModeToActivatedMode();

        setOnlyMondayAsWorkday();

        LocalDateTime mondayWorkDayTime = new LocalDateTime()
                .withDate(2016, 7, 25)
                .withTime(9, 0, 0, 0);
        setCurrentDayAndTime(mondayWorkDayTime);

        assertThat(workModeService.setToSilentMode()).isTrue();

        verify(audioModeService).setModeTo(SILENT);
    }

    @Test
    public void shouldNotSetToSilentModeDuringNonWorkDays() {
        setWorkHours();
        setWorkModeToActivatedMode();

        setOnlyMondayAsWorkday();

        LocalDateTime notAWorkDayTuesday = new LocalDateTime()
                .withDate(2016, 7, 26)
                .withTime(9, 0, 0, 0);
        setCurrentDayAndTime(notAWorkDayTuesday);

        assertThat(workModeService.setToSilentMode()).isFalse();

        verifyNoMoreInteractions(audioModeService);
    }

    @Test
    public void shouldSetToNormalModeDuringWorkDaysAfterWorkHours() {
        setWorkHours();
        setWorkModeToActivatedMode();

        setOnlyMondayAsWorkday();

        LocalDateTime mondayAfterWorkHoursDayTime = new LocalDateTime()
                .withDate(2016, 7, 25)
                .withTime(18, 0, 0, 0);
        setCurrentDayAndTime(mondayAfterWorkHoursDayTime);

        assertThat(workModeService.setToNormalMode()).isTrue();

        verify(audioModeService).setModeTo(NORMAL);
    }

    @Test
    public void shouldNotSetToNormalModeOutsideWorkDays() {
        setWorkHours();
        setWorkModeToActivatedMode();

        setOnlyMondayAsWorkday();

        LocalDateTime notAWorkDayTuesday = new LocalDateTime()
                .withDate(2016, 7, 26)
                .withTime(18, 0, 0, 0);
        setCurrentDayAndTime(notAWorkDayTuesday);

        assertThat(workModeService.setToNormalMode()).isFalse();

        verifyNoMoreInteractions(audioModeService);
    }

    @Test
    public void shouldBeAbleToSetWorkDays() {
        Set<WorkDay> workDays = new HashSet<>(asList(WorkDay.MONDAY));
        workModeService.setWorkDays(workDays);

        verify(workTimeService).saveWorkDays(workDays);
    }

    private void setWorkHours() {
        when(workTimeService.getStartWorkTime()).thenReturn((START_WORK_TIME));
        when(workTimeService.getEndWorkTime()).thenReturn((END_WORK_TIME));
    }

    private void setWorkModeToActivatedMode() {
        when(sharedPreferences.getBoolean(WORK_MODE_ACTIVATED_KEY, false)).thenReturn(true);
    }

    private void setOnlyMondayAsWorkday() {
        Set<WorkDay> savedDaysSet = new HashSet<>();
        savedDaysSet.add(WorkDay.MONDAY);
        when(workTimeService.getWorkDays()).thenReturn(savedDaysSet);
    }

    private void setWorkModeToDeactivatedMode() {
        when(sharedPreferences.getBoolean(WORK_MODE_ACTIVATED_KEY, false)).thenReturn(false);
    }

    private void setCurrentDayAndTime(LocalDateTime tuesdayWorkDay) {
        setCurrentMillisFixed(tuesdayWorkDay.toDateTime().getMillis());
    }

    private static void setCurrentTime(LocalTime localTime) {
        setCurrentMillisFixed(localTime.toDateTimeToday().getMillis());
    }

    private static void resetToPresent() {
        setCurrentMillisSystem();
    }

}