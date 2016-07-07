package com.rafaelkarlo.workmode;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.widget.TextView;

import com.rafaelkarlo.workmode.service.WorkModeService;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

public class MainScreen extends AppCompatActivity {

    @Inject
    WorkModeService workModeService;

    @Inject
    AudioManager audioManager;

    @BindView(R.id.switchButton)
    SwitchCompat switchButton;

    @BindView(R.id.workModeStatus)
    TextView workModeStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        injectDependencies();
        checkActivationStatus();
    }

    @OnCheckedChanged(R.id.switchButton)
    public void whenSwitchHasChanged(SwitchCompat switchButton) {
        if (switchButton.isChecked()) {
            workModeService.activate();
            setViewToActivated();
        } else {
            workModeService.deactivate();
            setViewToDeactivated();
        }
    }

    private void checkActivationStatus() {
        if (workModeService.isActivated()) {
            setViewToActivated();
        } else {
            setViewToDeactivated();
        }
    }

    private void setViewToDeactivated() {
        workModeStatus.setText("Deactivated");
        workModeStatus.setTextColor(getResources().getColor(R.color.red));
        switchButton.setChecked(false);
    }

    private void setViewToActivated() {
        workModeStatus.setText("Activated");
        workModeStatus.setTextColor(getResources().getColor(R.color.green));
        switchButton.setChecked(true);
    }

    private void injectDependencies() {
        ((MainApplication) getApplication()).getMainScreenComponent().inject(this);
        ButterKnife.bind(this);
    }

}
