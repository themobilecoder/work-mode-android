package com.rafaelkarlo.workmode.mainscreen.view;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.rafaelkarlo.workmode.MainApplication;
import com.rafaelkarlo.workmode.R;
import com.rafaelkarlo.workmode.mainscreen.presenter.MainPresenterImpl;

import org.joda.time.LocalTime;

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

    @BindView(R.id.work_start_time_value)
    TextView workStartTimeText;

    @BindView(R.id.work_end_time_value)
    TextView workEndTimeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        injectDependencies();

        mainPresenter.attachView(this);
        mainPresenter.onCreate();
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
        displayErrorSnackbarWithMessage("Start time should be before Stop time");
        switchButton.setChecked(false);
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
}
