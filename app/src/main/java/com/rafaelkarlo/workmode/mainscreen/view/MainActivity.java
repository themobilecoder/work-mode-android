package com.rafaelkarlo.workmode.mainscreen.view;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.widget.TextView;

import com.rafaelkarlo.workmode.MainApplication;
import com.rafaelkarlo.workmode.R;
import com.rafaelkarlo.workmode.mainscreen.presenter.MainPresenterImpl;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

public class MainActivity extends AppCompatActivity implements MainView {

    @Inject
    AudioManager audioManager;

    @Inject
    MainPresenterImpl mainPresenter;

    @BindView(R.id.switchButton)
    SwitchCompat switchButton;

    @BindView(R.id.workModeStatus)
    TextView workModeStatus;

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

    @OnCheckedChanged(R.id.switchButton)
    public void whenSwitchHasChanged(SwitchCompat switchButton) {
        if (switchButton.isShown()) {
            if (switchButton.isChecked()) {
                mainPresenter.activateWorkMode();
            } else {
                mainPresenter.deactivateWorkMode();
            }
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
        ((MainApplication) getApplication()).getMainActivityComponent().inject(this);
        ButterKnife.bind(this);
    }
}
