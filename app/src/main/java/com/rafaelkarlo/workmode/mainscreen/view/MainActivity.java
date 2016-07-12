package com.rafaelkarlo.workmode.mainscreen.view;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rafaelkarlo.workmode.MainApplication;
import com.rafaelkarlo.workmode.R;
import com.rafaelkarlo.workmode.mainscreen.presenter.MainPresenterImpl;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements MainView {

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
    public void setStartTime() {
        Toast.makeText(this, "Setting Start Time", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.set_end_time_button)
    public void setEndTime() {
        Toast.makeText(this, "Setting End Time", Toast.LENGTH_SHORT).show();
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

    private void injectDependencies() {
        ((MainApplication) getApplication()).getMainActivityComponent().inject(this);
        ButterKnife.bind(this);
    }
}
