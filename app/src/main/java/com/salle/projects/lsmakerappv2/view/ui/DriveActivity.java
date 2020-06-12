package com.salle.projects.lsmakerappv2.view.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.salle.projects.lsmakerappv2.R;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class DriveActivity extends AppCompatActivity {

    private Button btnBack, btnMode;
    private Button btnSlowest, btnSlow, btnNormal, btnFast;
    private JoystickView jvControl;
    private IndicatorSeekBar iVelocity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving);
        initViews();
    }

    private void initViews() {
        btnBack = (Button) findViewById(R.id.activity_driving_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnMode = (Button) findViewById(R.id.activity_driving_mode);
        btnMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        jvControl = (JoystickView) findViewById(R.id.activity_driving_joystick);
        jvControl.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {

            }
        });

        iVelocity = (IndicatorSeekBar) findViewById(R.id.activity_driving_seekBar);
        iVelocity.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {

            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

            }
        });

        btnSlowest = (Button) findViewById(R.id.activity_driving_slowest_btn);
        btnSlowest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnSlow = (Button) findViewById(R.id.activity_driving_slow_btn);
        btnSlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnNormal = (Button) findViewById(R.id.activity_driving_normal_btn);
        btnNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnFast = (Button) findViewById(R.id.activity_driving_fast_btn);
        btnFast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
