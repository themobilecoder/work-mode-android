package com.rafaelkarlo.workmode.mainscreen.view;

import android.content.DialogInterface;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.rafaelkarlo.workmode.MainApplication;
import com.rafaelkarlo.workmode.R;
import com.rafaelkarlo.workmode.mainscreen.presenter.MainPresenterImpl;
import com.rafaelkarlo.workmode.mainscreen.service.time.WorkDay;

import org.joda.time.LocalTime;

import java.util.Set;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

import static java.lang.String.format;
import static org.joda.time.LocalTime.now;

public class MainActivity extends AppCompatActivity implements MainView, RadialTimePickerDialogFragment.OnTimeSetListener {

    @Inject
    AudioManager audioManager;

    @Inject
    MainPresenterImpl mainPresenter;

    @BindView(R.id.enable_button)
    SwitchCompat switchButton;

    @BindView(R.id.work_mode_status_value)
    TextView workModeStatus;

    @BindView(R.id.set_start_time_button)
    ImageView startTimeButton;

    @BindView(R.id.set_end_time_button)
    ImageView endTimeButton;

    @BindView(R.id.set_days_button)
    ImageView setDaysButton;

    @BindView(R.id.work_start_time_value)
    TextView workStartTimeText;

    @BindView(R.id.work_end_time_value)
    TextView workEndTimeText;

    @BindView(R.id.work_days_value)
    TextView workDaysValue;

    @BindView(R.id.audio_override_button)
    FloatingActionButton audioOverrideButton;

    @BindView(R.id.adView)
    AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        injectDependencies();

        mainPresenter.attachView(this);
        mainPresenter.onCreate();

        loadAdbanner();

    }

    @Override
    public void onWorkModeActivation() {
        setViewToActivated();
    }

    @Override
    public void onWorkModeDeactivation() {
        setViewToDeactivated();
    }

    @Override
    public void onSetStartDate(String startDate) {
        workStartTimeText.setText(startDate);
    }

    @Override
    public void onSetEndDate(String endDate) {
        workEndTimeText.setText(endDate);
    }

    @Override
    public void onSetWorkDays(String workDays) {
        workDaysValue.setText(workDays);
    }

    @Override
    public void displayActivationSuccessful() {
        displaySuccessfulSnackbarWithMessage(
                format("Muting phone from %s to %s",
                        workStartTimeText.getText(),
                        workEndTimeText.getText()
                )
        );
    }

    @Override
    public void displayErrorOnMissingWorkHours() {
        displayErrorSnackbarWithMessage("Please set the work hours first");
        switchButton.setChecked(false);
    }

    @Override
    public void displayErrorOnInvalidWorkHours() {
        displayErrorSnackbarWithMessage("Start time should not be equal to Stop time");
        switchButton.setChecked(false);
    }

    @Override
    public void displayErrorOnMissingWorkDays() {
        displayErrorSnackbarWithMessage("Please add work days");
        switchButton.setChecked(false);
    }

    @Override
    public void displayAudioOverrideSuccessMessage(String newAudioMode) {
        displaySuccessfulSnackbarWithMessage(format("Audio Mode Set to %s", newAudioMode));
    }

    @Override
    public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
        String tag = dialog.getTag();
        if (tag.equals("startTime")) {
            mainPresenter.setStartDate(hourOfDay, minute);
        } else if (tag.equals("endTime")) {
            mainPresenter.setEndDate(hourOfDay, minute);
        }
    }

    @OnCheckedChanged(R.id.enable_button)
    public void whenSwitchHasChanged(SwitchCompat switchButton) {
        if (switchButton.isShown()) {
            if (switchButton.isChecked()) {
                mainPresenter.activateWorkMode();
            } else {
                mainPresenter.deactivateWorkMode();
            }
        }
    }

    @OnClick(R.id.set_start_time_button)
    public void setStartTime(View button) {
        showTimePickerDialog("startTime");
    }

    @OnClick(R.id.set_end_time_button)
    public void setEndTime(View button) {
        showTimePickerDialog("endTime");
    }

    @OnClick(R.id.set_days_button)
    public void setDays(View button) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.day_picker_dialog, null);
        final DaysToggleViewHolder daysToggleViewHolder = new DaysToggleViewHolder(dialogView);
        daysToggleViewHolder.updateToggleButtons(mainPresenter.getSavedDays());
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Work Days");
        dialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Set<WorkDay> daysSet = daysToggleViewHolder.getDaysSet();
                mainPresenter.setWorkDays(daysSet);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", null);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red));
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.red));
    }

    @OnClick(R.id.audio_override_button)
    public void onAudioOverrideButtonClick() {
        overrideAudioMode();
    }

    private void overrideAudioMode() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View overrideModeDialog = inflater.inflate(R.layout.override_mode_spinner, null);
        dialogBuilder.setView(overrideModeDialog);
        dialogBuilder.setTitle("Override Audio Mode");

        final OverrideAudioSpinnerViewHolder audioSpinnerViewHolder = new OverrideAudioSpinnerViewHolder(overrideModeDialog);
        dialogBuilder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mainPresenter.setCurrentAudioMode(audioSpinnerViewHolder.getSelectedAudioMode());
            }
        });
        dialogBuilder.setNegativeButton("Cancel", null);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red));
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.red));
    }

    private void setViewToDeactivated() {
        workModeStatus.setText("Disabled");
        workModeStatus.setTextColor(getResources().getColor(R.color.red));
        switchButton.setChecked(false);
    }

    private void setViewToActivated() {
        workModeStatus.setText("Enabled");
        workModeStatus.setTextColor(getResources().getColor(R.color.green));
        switchButton.setChecked(true);
    }

    private void showTimePickerDialog(String tag) {
        LocalTime currentTime = now();
        RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment()
                .setOnTimeSetListener(this)
                .setStartTime(currentTime.getHourOfDay(), currentTime.getMinuteOfHour())
                .setDoneText("Save")
                .setCancelText("Cancel")
                .setThemeDark();
        rtpd.show(getSupportFragmentManager(), tag);
    }

    private void injectDependencies() {
        ((MainApplication) getApplication()).getMainActivityComponent().inject(this);
        ButterKnife.bind(this);
    }

    private void displayErrorSnackbarWithMessage(String message) {
        View parentView = findViewById(R.id.parent_layout);
        if (parentView != null) {
            Snackbar snackbar = Snackbar.make(parentView, message, Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.errorBackground));
            snackbar.show();
        }
    }

    private void displaySuccessfulSnackbarWithMessage(String message) {
        View parentView = findViewById(R.id.parent_layout);
        if (parentView != null) {
            Snackbar snackbar = Snackbar.make(parentView, message, Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.successfulBackground));
            snackbar.show();
        }
    }

    private void loadAdbanner() {
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
}
