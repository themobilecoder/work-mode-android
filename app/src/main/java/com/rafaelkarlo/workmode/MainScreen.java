package com.rafaelkarlo.workmode;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.rafaelkarlo.workmode.service.WorkModeService;

import javax.inject.Inject;

public class MainScreen extends AppCompatActivity {

    @Inject
    WorkModeService workModeService;

    @Inject
    AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        injectDependencies();
    }

    private void injectDependencies() {
        ((MainApplication) getApplication()).getMainScreenComponent().inject(this);
    }

}
