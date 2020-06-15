package com.salle.projects.lsmakerappv2.view.ui;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.salle.projects.lsmakerappv2.R;
import com.salle.projects.lsmakerappv2.services.TiltService;
import com.salle.projects.lsmakerappv2.utils.JoystickUtils;
import com.salle.projects.lsmakerappv2.view.managers.DrivingDataManager;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class DriveActivity extends AppCompatActivity {

    // UI attributes
    private Button btnBack, btnMode;
    private Button btnSlowest, btnSlow, btnNormal, btnFast;
    private ImageView ivIcon;
    private JoystickView jvControl;
    private IndicatorSeekBar iVelocity;

    // Data Manager
    private DrivingDataManager mManager;

    // Dialog mode options
    private CharSequence[] mModeItems = new CharSequence[]{DrivingDataManager.JOYSTICK_MODE,
            DrivingDataManager.ROTATION_MODE};

    /** Broadcast receiver to listen to the TiltService's changes.*/
    private BroadcastReceiver tiltReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //*********************//
            if (action.equals(TiltService.TILT_DATA_UPDATED)) {
                //currentSpeedAngle = TiltService.getRoll();
                if (mManager == null) { mManager = DrivingDataManager.getInstance();}
                mManager.setTurn(getTurn(TiltService.getPitch()));
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving);
        mManager = DrivingDataManager.getInstance();
        initViews();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mManager != null && mManager.getDataSource().equals(DrivingDataManager.ROTATION_MODE)) {
            TiltService.stopService();
            this.unregisterReceiver(tiltReceiver);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mManager != null && mManager.getDataSource().equals(DrivingDataManager.ROTATION_MODE)) {
            TiltService.initializeService(this);
            this.registerReceiver(tiltReceiver, intentFilter);
        }
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
                int turn = JoystickUtils.getDirectionFromParams(angle, strength);
                mManager.setTurn(turn);
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
                mManager.setSpeed(seekBar.getProgress());
            }
        });

        btnSlowest = (Button) findViewById(R.id.activity_driving_slowest_btn);
        btnSlowest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mManager.setSpeed(iVelocity.getProgress());
            }
        });

        btnSlow = (Button) findViewById(R.id.activity_driving_slow_btn);
        btnSlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mManager.setSpeed(iVelocity.getProgress());
            }
        });

        btnNormal = (Button) findViewById(R.id.activity_driving_normal_btn);
        btnNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mManager.setSpeed(iVelocity.getProgress());
            }
        });

        btnFast = (Button) findViewById(R.id.activity_driving_fast_btn);
        btnFast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mManager.setSpeed(iVelocity.getProgress());
            }
        });
    }


    /**
     * Method that returns the current turn set by the smartphone's orientation.
     *
     * The current turn can be calculated using the initial turn (considered 0º or horizontal
     * position) and the last turn angle gotten from the Tilt service. Maximum turn will be
     * achieved on a tilt >= 45º.
     *
     * @return the current turn set by the smartphone's orientation
     */
    private int getTurn(Float turnAngle) {
        if (mManager.isRunning()) {
            float angle = turnAngle;
            if (angle > 45) angle = 45;
            if (angle < -45) angle = -45;
            return Math.round(angle * 100 / 45);
        }
        return 0;
    }

    /*****************************************************************************
     * ***************************  MODES DIALOG  ******************************/
    private void displayModeDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Choose the Driving Mode");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setIcon(getResources().getDrawable(R.drawable.ic_control, null));
        }
        //.setBackground(getResources().getDrawable(R.drawable.back_white_rad, null))
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setSingleChoiceItems(mModeItems, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private void setupOnDeviceRotationMode() {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation != Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        // Turn visible the device icon
        ivIcon = findViewById(R.id.activity_driving_icon);
        ivIcon.setVisibility(View.VISIBLE);

        // Make invisible the joystick command
        jvControl.setVisibility(View.GONE);

        // Setting the current control mode
        mManager.setDataSource(DrivingDataManager.ROTATION_MODE);

        // Accelerometer
        TiltService.initializeService(this);
        this.registerReceiver(tiltReceiver, intentFilter);
    }

    /** IntentFilter to configure the broadcast receiver */
    private IntentFilter intentFilter = new IntentFilter(TiltService.TILT_DATA_UPDATED);

}
