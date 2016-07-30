package com.rafaelkarlo.workmode.mainscreen.service;

import android.content.SharedPreferences;

import com.rafaelkarlo.workmode.mainscreen.service.time.WorkTimeService;
import com.rafaelkarlo.workmode.mainscreen.service.time.WorkTimeServiceImpl;
import com.rafaelkarlo.workmode.mainscreen.service.time.WorkDay;

import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.Set;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkTimeServiceTest {
    public static final String WORK_START_TIME_KEY = "WORK_START_TIME";
    public static final String WORK_END_TIME_KEY = "WORK_END_TIME";

    @Mock
    private SharedPreferences sharedPreferences;

    @Mock
    private SharedPreferences.Editor sharedPreferencesEditor;

    private WorkTimeService workTimeService;

    private LocalTime expectedStartTime = new LocalTime(9, 0, 0);
    private LocalTime expectedEndTime = new LocalTime(17, 0, 0);

    @Before
    public void setup() {
        workTimeService = new WorkTimeServiceImpl(sharedPreferences);
    }

    @Test
    public void shouldSaveStartOfWorkTime() {
        setupMockForSavingStartTime();

        workTimeService.setStartWorkTime(expectedStartTime);

        verify(sharedPreferencesEditor).putInt(WORK_START_TIME_KEY, expectedStartTime.getMillisOfDay());
    }

    @Test
    public void shouldSaveEndOfWorkTime() {
        setupMockForSavingEndTime();

        workTimeService.setEndWorkTime(expectedEndTime);

        verify(sharedPreferencesEditor).putInt(WORK_END_TIME_KEY, expectedEndTime.getMillisOfDay());
    }

    @Test
    public void shouldGetSavedStartTime() {
        when(sharedPreferences.getInt(WORK_START_TIME_KEY, -1)).thenReturn(expectedStartTime.getMillisOfDay());

        assertThat(workTimeService.getStartWorkTime()).isEqualTo(expectedStartTime);
    }

    @Test
    public void shouldGetSavedendTime() {
        when(sharedPreferences.getInt(WORK_END_TIME_KEY, -1)).thenReturn(expectedEndTime.getMillisOfDay());

        assertThat(workTimeService.getEndWorkTime()).isEqualTo(expectedEndTime);
    }

    @Test
    public void shouldReturnNullIfThereAreNoSavedWorkTimes() {
        when(sharedPreferences.getInt(WORK_START_TIME_KEY, -1)).thenReturn(-1);
        when(sharedPreferences.getInt(WORK_END_TIME_KEY, -1)).thenReturn(-1);

        assertThat(workTimeService.getStartWorkTime()).isNull();
        assertThat(workTimeService.getEndWorkTime()).isNull();
    }

    @Test
    public void shouldSaveWorkDays() {
        setupWhenSavingWorkDays();

        HashSet<WorkDay> workDays = new HashSet<>(asList(
                WorkDay.SUNDAY,
                WorkDay.MONDAY,
                WorkDay.TUESDAY,
                WorkDay.WEDNESDAY,
                WorkDay.THURSDAY,
                WorkDay.FRIDAY,
                WorkDay.SATURDAY
        ));

        workTimeService.saveWorkDays(workDays);

        verifyThatWorkDaysHaveBeenSaved();
    }

    @Test
    public void shouldGetSavedWorkDays() {
        setupWhenGettingWorkDays();


        Set<WorkDay> expectedSavedDays = new HashSet<>(asList(
           WorkDay.SUNDAY,
           WorkDay.MONDAY,
           WorkDay.TUESDAY,
           WorkDay.WEDNESDAY,
           WorkDay.THURSDAY,
           WorkDay.FRIDAY,
           WorkDay.SATURDAY
        ));
        assertThat(workTimeService.getWorkDays()).containsExactlyElementsIn(expectedSavedDays);
    }

    private void setupWhenGettingWorkDays() {
        when(sharedPreferences.getBoolean("SUNDAY", false)).thenReturn(true);
        when(sharedPreferences.getBoolean("MONDAY", false)).thenReturn(true);
        when(sharedPreferences.getBoolean("TUESDAY", false)).thenReturn(true);
        when(sharedPreferences.getBoolean("WEDNESDAY", false)).thenReturn(true);
        when(sharedPreferences.getBoolean("THURSDAY", false)).thenReturn(true);
        when(sharedPreferences.getBoolean("FRIDAY", false)).thenReturn(true);
        when(sharedPreferences.getBoolean("SATURDAY", false)).thenReturn(true);
    }

    private void setupWhenSavingWorkDays() {
        when(sharedPreferences.edit()).thenReturn(sharedPreferencesEditor);
        when(sharedPreferencesEditor.putBoolean("SUNDAY", true)).thenReturn(sharedPreferencesEditor);
        when(sharedPreferencesEditor.putBoolean("MONDAY", true)).thenReturn(sharedPreferencesEditor);
        when(sharedPreferencesEditor.putBoolean("TUESDAY", true)).thenReturn(sharedPreferencesEditor);
        when(sharedPreferencesEditor.putBoolean("WEDNESDAY", true)).thenReturn(sharedPreferencesEditor);
        when(sharedPreferencesEditor.putBoolean("THURSDAY", true)).thenReturn(sharedPreferencesEditor);
        when(sharedPreferencesEditor.putBoolean("FRIDAY", true)).thenReturn(sharedPreferencesEditor);
        when(sharedPreferencesEditor.putBoolean("SATURDAY", true)).thenReturn(sharedPreferencesEditor);
    }

    private void verifyThatWorkDaysHaveBeenSaved() {
        verify(sharedPreferencesEditor).putBoolean("SUNDAY", true);
        verify(sharedPreferencesEditor).putBoolean("MONDAY", true);
        verify(sharedPreferencesEditor).putBoolean("TUESDAY", true);
        verify(sharedPreferencesEditor).putBoolean("WEDNESDAY", true);
        verify(sharedPreferencesEditor).putBoolean("THURSDAY", true);
        verify(sharedPreferencesEditor).putBoolean("FRIDAY", true);
        verify(sharedPreferencesEditor).putBoolean("SATURDAY", true);
        verify(sharedPreferencesEditor).apply();
    }

    private void setupMockForSavingStartTime() {
        when(sharedPreferences.edit()).thenReturn(sharedPreferencesEditor);
        int millisOfStartTimeToday = expectedStartTime.getMillisOfDay();
        when(sharedPreferencesEditor.putInt(WORK_START_TIME_KEY, millisOfStartTimeToday)).thenReturn(sharedPreferencesEditor);
    }

    private void setupMockForSavingEndTime() {
        when(sharedPreferences.edit()).thenReturn(sharedPreferencesEditor);
        int millisOfEndTimeToday = expectedEndTime.getMillisOfDay();
        when(sharedPreferencesEditor.putInt(WORK_END_TIME_KEY, millisOfEndTimeToday)).thenReturn(sharedPreferencesEditor);
    }
}