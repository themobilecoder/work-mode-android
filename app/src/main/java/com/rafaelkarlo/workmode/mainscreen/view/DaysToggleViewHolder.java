package com.rafaelkarlo.workmode.mainscreen.view;

import android.view.View;
import android.widget.ToggleButton;

import com.rafaelkarlo.workmode.R;
import com.rafaelkarlo.workmode.mainscreen.service.time.WorkDay;

import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.rafaelkarlo.workmode.mainscreen.service.time.WorkDay.FRIDAY;
import static com.rafaelkarlo.workmode.mainscreen.service.time.WorkDay.MONDAY;
import static com.rafaelkarlo.workmode.mainscreen.service.time.WorkDay.SATURDAY;
import static com.rafaelkarlo.workmode.mainscreen.service.time.WorkDay.SUNDAY;
import static com.rafaelkarlo.workmode.mainscreen.service.time.WorkDay.THURSDAY;
import static com.rafaelkarlo.workmode.mainscreen.service.time.WorkDay.TUESDAY;
import static com.rafaelkarlo.workmode.mainscreen.service.time.WorkDay.WEDNESDAY;

public class DaysToggleViewHolder  {

    @BindView(R.id.sunday_check_box)
    ToggleButton sundayToggleButton;

    @BindView(R.id.monday_check_box)
    ToggleButton mondayToggleButton;

    @BindView(R.id.tuesday_check_box)
    ToggleButton tuesdayToggleButton;

    @BindView(R.id.wednesday_check_box)
    ToggleButton wednesdayToggleButton;

    @BindView(R.id.thursday_check_box)
    ToggleButton thursdayToggleButton;

    @BindView(R.id.friday_check_box)
    ToggleButton fridayToggleButton;

    @BindView(R.id.saturday_check_box)
    ToggleButton saturdayToggleButton;

    public DaysToggleViewHolder(View view) {
        ButterKnife.bind(this, view);
    }

    public void updateToggleButtons(Set<WorkDay> workDays) {
        sundayToggleButton.setChecked(workDays.contains(SUNDAY));
        mondayToggleButton.setChecked(workDays.contains(MONDAY));
        tuesdayToggleButton.setChecked(workDays.contains(TUESDAY));
        wednesdayToggleButton.setChecked(workDays.contains(WEDNESDAY));
        thursdayToggleButton.setChecked(workDays.contains(THURSDAY));
        fridayToggleButton.setChecked(workDays.contains(FRIDAY));
        saturdayToggleButton.setChecked(workDays.contains(SATURDAY));
    }


    public Set<WorkDay> getDaysSet() {
        Set<WorkDay> workDays = new HashSet<>();

        if (sundayToggleButton.isChecked()) {
            workDays.add(SUNDAY);
        }
        if (mondayToggleButton.isChecked()) {
            workDays.add(MONDAY);
        }
        if (tuesdayToggleButton.isChecked()) {
            workDays.add(TUESDAY);
        }
        if (wednesdayToggleButton.isChecked()) {
            workDays.add(WEDNESDAY);
        }
        if (thursdayToggleButton.isChecked()) {
            workDays.add(THURSDAY);
        }
        if (fridayToggleButton.isChecked()) {
            workDays.add(FRIDAY);
        }
        if (saturdayToggleButton.isChecked()) {
            workDays.add(SATURDAY);
        }
        return workDays;
    }
}
