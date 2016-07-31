package com.rafaelkarlo.workmode.mainscreen.view;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.rafaelkarlo.workmode.R;
import com.rafaelkarlo.workmode.mainscreen.service.audio.AudioMode;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.rafaelkarlo.workmode.mainscreen.service.audio.AudioMode.NORMAL;
import static com.rafaelkarlo.workmode.mainscreen.service.audio.AudioMode.SILENT;
import static com.rafaelkarlo.workmode.mainscreen.service.audio.AudioMode.UNKNOWN;
import static com.rafaelkarlo.workmode.mainscreen.service.audio.AudioMode.VIBRATE;

public class OverrideAudioSpinnerViewHolder {

    @BindView(R.id.mode_spinner)
    Spinner modeSpinner;

    public OverrideAudioSpinnerViewHolder(View view) {
        ButterKnife.bind(this, view);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(view.getContext(), R.array.modes_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeSpinner.setAdapter(adapter);
    }

    public AudioMode getSelectedAudioMode() {
        String selectedMode = (String) modeSpinner.getSelectedItem();
        switch (selectedMode) {
            case "Normal":
                return NORMAL;
            case "Vibrate":
                return VIBRATE;
            case "Silent":
                return SILENT;
            default:
                return UNKNOWN;
        }
    }
}
