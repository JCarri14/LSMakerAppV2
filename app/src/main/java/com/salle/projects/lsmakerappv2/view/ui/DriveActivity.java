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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.salle.projects.lsmakerappv2.R;
import com.salle.projects.lsmakerappv2.bluetooth.BluetoothService;
import com.salle.projects.lsmakerappv2.bluetooth.DataSenderService;
import com.salle.projects.lsmakerappv2.services.TiltService;
import com.salle.projects.lsmakerappv2.utils.JoystickUtils;
import com.salle.projects.lsmakerappv2.view.managers.DrivingDataManager;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class DriveActivity extends AppCompatActivity {

    private static final String TAG = DriveActivity.class.getName();

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

        if (mManager == null) { mManager = DrivingDataManager.getInstance(); }
        mManager.setRunning(false);
        if (mManager != null && mManager.getDataSource().equals(DrivingDataManager.ROTATION_MODE)) {
            try {
                TiltService.stopService();
                this.unregisterReceiver(tiltReceiver);
            } catch(Exception e) {
                //e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mManager == null) { mManager = DrivingDataManager.getInstance(); }
        if (BluetoothService.getInstance().getDevice() != null) {
            mManager.setRunning(true);
            Intent mDataSenderServiceIntent = new Intent(this, DataSenderService.class);
            startService(mDataSenderServiceIntent);
        }
        if (mManager != null && mManager.getDataSource().equals(DrivingDataManager.ROTATION_MODE)) {
            // Turn visible the device icon
            ivIcon = findViewById(R.id.activity_driving_icon);
            ivIcon.setVisibility(View.VISIBLE);

            // Make invisible the joystick command
            jvControl.setVisibility(View.GONE);

            // Setting the current control mode
            mManager.setDataSource(DrivingDataManager.ROTATION_MODE);
/*
            // Accelerometer
            TiltService.initializeService(this);
            this.registerReceiver(tiltReceiver, intentFilter);*/
        }
    }

    private void initViews() {
        btnBack = (Button) findViewById(R.id.activity_driving_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnMode = (Button) findViewById(R.id.activity_driving_mode);
        btnMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayModeDialog();
            }
        });

        jvControl = (JoystickView) findViewById(R.id.activity_driving_joystick);
        jvControl.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                int turn = JoystickUtils.getDirectionFromParams(angle, strength);
                Log.d(TAG, "Current direction: " + turn);
                mManager.setTurn(turn);
            }
        });

        int orientation = getResources().getConfiguration().orientation;
        if (orientation != Configuration.ORIENTATION_LANDSCAPE) {
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
        }
        btnSlowest = (Button) findViewById(R.id.activity_driving_slowest_btn);
        btnSlowest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mManager.setSpeed(10);
            }
        });

        btnSlow = (Button) findViewById(R.id.activity_driving_slow_btn);
        btnSlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mManager.setSpeed(30);
            }
        });

        btnNormal = (Button) findViewById(R.id.activity_driving_normal_btn);
        btnNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mManager.setSpeed(60);
            }
        });

        btnFast = (Button) findViewById(R.id.activity_driving_fast_btn);
        btnFast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mManager.setSpeed(90);
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
            builder.setBackground(getResources().getDrawable(R.drawable.back_white_rad, null));
        } else {
            builder.setBackground(ContextCompat.getDrawable(this, R.drawable.back_white_rad));
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
        builder.setItems(mModeItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (mModeItems[i].equals(DrivingDataManager.JOYSTICK_MODE)) {
                    dialogInterface.dismiss();
                    setOnJoystickMode();
                } else {
                    if (mModeItems[i].equals(DrivingDataManager.ROTATION_MODE)) {
                        dialogInterface.dismiss();
                        setOnDeviceRotationMode();
                    }
                }
            }
        });
        builder.show();
    }

    /*****************************************************************************
     * *****************************  MODES SETUP  *******************************/
    @SuppressLint("SourceLockedOrientationActivity")
    private void setOnDeviceRotationMode() {
        if (!mManager.getDataSource().equals(DrivingDataManager.ROTATION_MODE)) {
            // Setting the current control mode
            mManager.setDataSource(DrivingDataManager.ROTATION_MODE);

            int orientation = getResources().getConfiguration().orientation;
            if (orientation != Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                // Turn visible the device icon
                ivIcon = findViewById(R.id.activity_driving_icon);
                ivIcon.setVisibility(View.VISIBLE);

                // Make invisible the joystick command
                jvControl.setVisibility(View.GONE);

                // Accelerometer
                TiltService.initializeService(this);
                this.registerReceiver(tiltReceiver, intentFilter);
            }
        }
    }

    private void setOnJoystickMode() {
        if (!mManager.getDataSource().equals(DrivingDataManager.JOYSTICK_MODE)) {
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                if (mManager.getDataSource().equals(DrivingDataManager.ROTATION_MODE)) {
                    if (ivIcon != null) {
                        ivIcon.setVisibility(View.GONE);
                        // Stopping Service from reading rotation data
                        try {
                            TiltService.stopService();
                            this.unregisterReceiver(tiltReceiver);
                        } catch (Exception e) {
                            //e.printStackTrace();
                        }
                    }
                }
            }

            if (jvControl == null) {
                jvControl = findViewById(R.id.activity_driving_joystick);
            }
            // Make invisible the joystick command
            jvControl.setVisibility(View.VISIBLE);

            // Setting the current control mode
            mManager.setDataSource(DrivingDataManager.JOYSTICK_MODE);
        }
    }

    /** IntentFilter to configure the broadcast receiver */
    private IntentFilter intentFilter = new IntentFilter(TiltService.TILT_DATA_UPDATED);

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int orientation = getResources().getConfiguration().orientation;
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                if (mManager.getDataSource().equals(DrivingDataManager.JOYSTICK_MODE)) {
                    setRequestedOrientation(newConfig.orientation);
                }
            }
        }
    }
}
