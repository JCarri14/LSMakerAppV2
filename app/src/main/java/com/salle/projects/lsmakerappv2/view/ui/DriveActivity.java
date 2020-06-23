package com.salle.projects.lsmakerappv2.view.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
    private ImageView btnBack;
    private ImageButton btnMode;
    private Button btnBackHor, btnModeHor;
    private Button btnPlay, btnSlowest, btnSlow, btnNormal, btnFast;
    private ImageView ivIcon;
    private JoystickView jvControl;
    private IndicatorSeekBar iVelocity;

    // Data Manager
    private DrivingDataManager mManager;

    // Dialog mode options
    private CharSequence[] mModeItems;

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

        mManager.JOYSTICK_MODE_VERT = getResources().getString(R.string.driving_mode_joystick_vert);
        mManager.JOYSTICK_MODE_HOR = getResources().getString(R.string.driving_mode_joystick_hor);
        mManager.DEVICE_ROTATION_MODE = getResources().getString(R.string.driving_mode_device_rotation);

        mModeItems = new CharSequence[]{mManager.JOYSTICK_MODE_VERT, mManager.JOYSTICK_MODE_HOR,
                        mManager.DEVICE_ROTATION_MODE};
        TiltService.initializeService(this);
        initViews();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mManager != null && mManager.getDataSource().equals(mManager.DEVICE_ROTATION_MODE)) {
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
        int orientation = getResources().getConfiguration().orientation;
        if (mManager != null && orientation == Configuration.ORIENTATION_LANDSCAPE) {
            btnPlay = findViewById(R.id.act_driving_play_btn);
            btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (btnPlay.getText().toString().equals(getString(R.string.driving_action_start))) {
                        btnPlay.setText(getString(R.string.driving_action_stop));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            btnPlay.setBackgroundTintList(getResources().getColorStateList(R.color.colorError));
                        } else {
                            btnPlay.setBackgroundColor(ContextCompat.getColor(DriveActivity.this, R.color.colorError));
                        }
                        mManager.setSpeed(50);
                    } else {
                        btnPlay.setText(getString(R.string.driving_action_start));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            btnPlay.setBackgroundTintList(getResources().getColorStateList(R.color.colorSalle));
                        } else {
                            btnPlay.setBackgroundColor(ContextCompat.getColor(DriveActivity.this, R.color.colorSalle));
                        }
                        mManager.setSpeed(0);
                    }
                }
            });
        }
        if (mManager != null && mManager.getDataSource().equals(mManager.DEVICE_ROTATION_MODE)) {
            // Turn visible the device icon
            ivIcon = findViewById(R.id.activity_driving_icon);
            ivIcon.setVisibility(View.VISIBLE);

            // Make invisible the joystick command
            jvControl.setVisibility(View.GONE);

            // Accelerometer
            TiltService.initializeService(this);
            this.registerReceiver(tiltReceiver, intentFilter);
        } else {
            initJoystick();
        }

    }

    private void initViews() {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation != Configuration.ORIENTATION_LANDSCAPE) {
            btnBack = (ImageView) findViewById(R.id.act_driving_back);
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });

            btnMode = (ImageButton) findViewById(R.id.act_driving_mode);
            btnMode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    displayModeDialog();
                }
            });
        } else {

            btnBackHor = (Button) findViewById(R.id.act_driving_back_hor);
            btnBackHor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });

            btnModeHor = (Button) findViewById(R.id.act_driving_mode_hor);
            btnModeHor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    displayModeDialog();
                }
            });
        }

        jvControl = (JoystickView) findViewById(R.id.act_driving_joystick);
        jvControl.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                int turn = JoystickUtils.getDirectionFromParams(angle, strength);
                Log.d(TAG, "Current direction: " + turn);
                mManager.setTurn(turn);
            }
        });

        if (orientation != Configuration.ORIENTATION_LANDSCAPE) {
            iVelocity = (IndicatorSeekBar) findViewById(R.id.act_driving_seekBar);
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
        } else {
            btnPlay = findViewById(R.id.act_driving_play_btn);
            btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (btnPlay.getText().toString().equals(getString(R.string.driving_action_start))) {
                        btnPlay.setText(getString(R.string.driving_action_stop));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            btnPlay.setBackgroundTintList(getResources().getColorStateList(R.color.colorError));
                        } else {
                            btnPlay.setBackgroundColor(ContextCompat.getColor(DriveActivity.this, R.color.colorError));
                        }
                        mManager.setSpeed(50);
                    } else {
                        btnPlay.setText(getString(R.string.driving_action_start));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            btnPlay.setBackgroundTintList(getResources().getColorStateList(R.color.colorSalle));
                        } else {
                            btnPlay.setBackgroundColor(ContextCompat.getColor(DriveActivity.this, R.color.colorSalle));
                        }
                        mManager.setSpeed(0);
                    }
                }
            });
        }

        btnSlowest = (Button) findViewById(R.id.act_driving_slowest_btn);
        btnSlowest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mManager.setSpeed(10);
                if (iVelocity != null) iVelocity.setProgress(10);
            }
        });

        btnSlow = (Button) findViewById(R.id.act_driving_slow_btn);
        btnSlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mManager.setSpeed(30);
                if (iVelocity != null) iVelocity.setProgress(30);
            }
        });

        btnNormal = (Button) findViewById(R.id.act_driving_normal_btn);
        btnNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mManager.setSpeed(60);
                if (iVelocity != null) iVelocity.setProgress(60);
            }
        });

        btnFast = (Button) findViewById(R.id.act_driving_fast_btn);
        btnFast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mManager.setSpeed(90);
                if (iVelocity != null) iVelocity.setProgress(90);
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
            builder.setBackground(getResources().getDrawable(R.drawable.back_white_rad, null));
        } else {
            builder.setBackground(ContextCompat.getDrawable(this, R.drawable.back_white_rad));
        }
        //.setBackground(getResources().getDrawable(R.drawable.back_white_rad, null))
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.setItems(mModeItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (mModeItems[i].equals(mManager.JOYSTICK_MODE_VERT)) {
                    dialogInterface.dismiss();
                    setOnJoystickMode(true);
                } else {
                    if (mModeItems[i].equals(mManager.DEVICE_ROTATION_MODE)) {
                        dialogInterface.dismiss();
                        setOnDeviceRotationMode();
                    } else {
                        if (mModeItems[i].equals(mManager.JOYSTICK_MODE_HOR)) {
                            dialogInterface.dismiss();
                            setOnJoystickMode(false);
                        }
                    }
                }
            }
        });
        builder.show();
    }

    /*****************************************************************************
     * *****************************  MODES SETUP  *******************************/
    private void setOnDeviceRotationMode() {
        if (!mManager.getDataSource().equals(mManager.DEVICE_ROTATION_MODE)) {
            // Setting the current control mode
            mManager.setDataSource(mManager.DEVICE_ROTATION_MODE);

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

    private void setOnJoystickMode(boolean verticalMode) {

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (ivIcon == null) ivIcon = findViewById(R.id.activity_driving_icon);
            ivIcon.setVisibility(View.GONE);
            if (mManager.getDataSource().equals(mManager.DEVICE_ROTATION_MODE)) {
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

            if (verticalMode) {
                // Setting the current control mode
                mManager.setDataSource(mManager.JOYSTICK_MODE_VERT);
                // Forcing Screen Orientation as desired
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                // Setting the current control mode
                mManager.setDataSource(mManager.JOYSTICK_MODE_HOR);
                initJoystick();
            }
        } else {
            if (verticalMode) {
                // Setting the current control mode
                mManager.setDataSource(mManager.JOYSTICK_MODE_VERT);
                initJoystick();
            } else {
                mManager.setDataSource(mManager.JOYSTICK_MODE_HOR);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }
    }

    private void initJoystick() {
        if (jvControl == null) jvControl = findViewById(R.id.act_driving_joystick);
        // Make invisible the joystick command
        jvControl.setVisibility(View.VISIBLE);
        jvControl.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                int turn = JoystickUtils.getDirectionFromParams(angle, strength);
                Log.d(TAG, "Current direction: " + turn);
                mManager.setTurn(turn);
            }
        });
    }

    /** IntentFilter to configure the broadcast receiver */
    private IntentFilter intentFilter = new IntentFilter(TiltService.TILT_DATA_UPDATED);

}
