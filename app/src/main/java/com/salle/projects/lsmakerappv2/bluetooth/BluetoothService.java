package com.salle.projects.lsmakerappv2.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.salle.projects.lsmakerappv2.bluetooth.callbacks.BtDiscoveryCallback;
import com.salle.projects.lsmakerappv2.model.BtDevice;
import com.salle.projects.lsmakerappv2.services.UartService;
import com.salle.projects.lsmakerappv2.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BluetoothService {

    private static final String TAG = BluetoothService.class.getName();

    private static BluetoothService instance;
    private static Object mutex = new Object();

    public static final String SCAN_STOPPED = "com.lasalle.lsmaker_remote.ACTION_SCAN_STOPPED";
    private final long SCAN_PERIOD = 5000; //scanning for 5 seconds
    private final int UART_PROFILE_CONNECTED = 20;
    private final int UART_PROFILE_DISCONNECTED = 21;

    // Own attributes
    private boolean serviceStarted = false;
    private Activity binderActivity = null;

    // Bluetooth attributes
    private List<BluetoothDevice> deviceList;
    private Map<String, Integer> devRssiValues;
    private UartService uartService = null;
    private Handler mHandler;
    private BluetoothAdapter mBluetoothAdapter = null;
    private int mState = UART_PROFILE_DISCONNECTED;

    // Device to connect
    private BtDevice mDevice = null;

    // UART service connected/disconnected
    private ServiceConnection mServiceConnection;

    // Bluetooth Device Discovery Callback
    private BtDiscoveryCallback mCallback;

    // Bluetooth Device Discovery Handlers
    private ScanCallback mScanCallback;
    private final BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            Log.d(TAG, "Device found: "+ device.getName());
            mCallback.onDeviceDiscovered(device, rssi);
        }
    };


    private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            //final Intent mIntent = intent;
            //*********************//
            if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
                mState = UART_PROFILE_CONNECTED;
            }

            //*********************//
            if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
                mState = UART_PROFILE_DISCONNECTED;
                uartService.close();
            }


            //*********************//
            if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
                uartService.enableTXNotification();
            }
            //*********************//
            if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {
                // Do what you need to do with the data received.
            }
            //*********************//
            if (action.equals(UartService.DEVICE_DOES_NOT_SUPPORT_UART)){
                uartService.disconnect();
            }
        }
    };

    public static BluetoothService getInstance() {
        BluetoothService result = instance;
        if (result == null) {
            synchronized (mutex) {
                result = instance;
                if (result == null)
                    instance = result = new BluetoothService();
            }
        }
        return result;
    }

    private BluetoothService() {}


    /**
     * Method that initializes the service.
     *
     * This method mus be called first before using any other functionality, otherwise correct
     * execution can not be assured.
     *
     * @param context context that first calls the service. This context must not be destroyed
     *                while the service is working.
     */
    private void initialize(final Context context) {
        mServiceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder rawBinder) {
                uartService = ((UartService.LocalBinder) rawBinder).getService();
                Log.d(TAG, "onServiceConnected uartService= " + uartService);
                if (!uartService.initialize(context)) {
                    Log.e(TAG, "Unable to initialize Bluetooth");
                }
            }

            public void onServiceDisconnected(ComponentName classname) {
                ////     uartService.disconnect(mDevice);
                uartService = null;
            }
        };
    }

    /**
     * Method that binds the smartphone to a bluetooth device.
     *
     * @param device the bluetooth device selected from the scanned list
     * @param context the activity's context to use to initialize the binding.
     * @return true if the binding was successfully achieved. False otherwise.
     */
    public Boolean connect(BtDevice device, Context context) {
        Log.d(TAG, "CONNECT");
        uartService = new UartService();
        Log.d(TAG, "onServiceConnected uartService= " + uartService);
        if (!uartService.initialize(context)) {
            Log.e(TAG, "Unable to initialize Bluetooth");
        }

        String deviceAddress = device.getAddress();
        BluetoothDevice deviceAux = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);
        mDevice = new BtDevice(deviceAux.getName(), deviceAux.getAddress(), null);
        Log.d(TAG, "... device.address==" + mDevice + "mserviceValue" + uartService);
        if (uartService.connect(deviceAddress)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBluetoothAdapter.getBluetoothLeScanner().stopScan(mScanCallback);
            } else {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
            return true;
        } else {
            mDevice = null;
            return false;
        }
    }

    /**
     * Method that sends a message to the currently binded bluetooth device.
     *
     * @param message the message to be send. Be sure to follow the API message format.
     * @return true if the message was send successfully. False otherwise.
     */
    public boolean sendMessage(byte[] message) {
        //send data to service
        Log.d(TAG, Utils.bytesToHex(message));
        uartService.writeRXCharacteristic(message);
        return true;
    }

    /**
     * Method that disconnects the smartphone from the currently binded bluetooth device.
     *
     * @return true if the unbinding was successful. False otherwise.
     */
    public boolean disconnect() {
        if (mDevice!=null)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBluetoothAdapter.getBluetoothLeScanner().stopScan(mScanCallback);
            } else {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
            try {
                uartService.disconnect();
                mDevice = null;
            } catch (NullPointerException e) {
                Log.d(TAG, e.getMessage());
            }
        }
        return true;
    }

    /**
     * Method that checks if the current smartphone is compatible with the Bluetooth Low Energy (BLE)
     * characteristics.
     *
     * @param manager the activity's PackageManager
     * @param bluetoothManager the activity's BluetoothManager
     * @return true if the smartphone is compatible. False otherwise.
     */
    public boolean checkDeviceCompatibility(Context context,PackageManager manager, BluetoothManager bluetoothManager) {
        mHandler = new Handler();

        if (!manager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return false;
        }
        //mBluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothAdapter = ((BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        return true;
    }

    /**
     * Method that checks if the bluetooth is enabled.
     *
     * If bluetooth isn't enabled, the method will ask the user permission to enable it.
     * If bluetooth is enabled or permission to enable it is given, then the service starts scanning
     * for devices.
     *
     * @param activityCaller activity that calls the method to let it call Activity's functionalities.
     */
    public void enableBluetooth(Activity activityCaller, int resultCode) {
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activityCaller.startActivityForResult(enableBtIntent, resultCode);
        } else {
            // Starts scanning for devices.
            startScanningDevices();
        }
    }

    /**
     * Method that stops a current scanning process.
     */
    public void pauseBluetooth() {
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            scanLeDevice(false);
        }
    }

    /**
     * Method that starts scanning for bluetooth devices inside effective radius.
     */
    public void startScanningDevices() {
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            if (deviceList == null) deviceList = new ArrayList<>();
            if (devRssiValues == null) devRssiValues = new HashMap<>();
            scanLeDevice(true);
        }
    }

    /**
     * Method that enables or disables device's scanning.
     *
     * A scanning process wil have a duration of {SCAN_PERIOD} milliseconds.
     *
     * @param enable true to start a scanning process, false to stop it.
     */
    private void scanLeDevice(final boolean enable) {
        //Log.d(TAG, "Scan Le Device: "+ enable);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final BluetoothLeScanner bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

            if (enable) {
                // Stops scanning after a pre-defined scan period.
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bluetoothLeScanner.stopScan(mScanCallback);
                    }
                }, SCAN_PERIOD);
                bluetoothLeScanner.startScan(mScanCallback);
            } else {
                bluetoothLeScanner.stopScan(mScanCallback);
            }

        } else {
            if (enable) {
                // Stops scanning after a pre-defined scan period.
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    }
                }, SCAN_PERIOD);

                mBluetoothAdapter.startLeScan(mLeScanCallback);
            } else {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }

    }

    /**
     * Method that initializes the service.
     *
     * This method mus be called first before using any other functionality, otherwise correct
     * execution can not be assured.
     *
     * @param binderActivity activity that first calls the service. This activity must not be
     *                       destroye while the service is working.
     */
    public void service_init(Activity binderActivity, BtDiscoveryCallback callback) {
        this.binderActivity = binderActivity;
        this.mCallback = callback;
        initialize(binderActivity);
        Intent bindIntent = new Intent(binderActivity, UartService.class);
        binderActivity.bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mScanCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    Log.d(TAG, "Device found: "+ result.getDevice().getName());
                    mCallback.onDeviceDiscovered(result.getDevice(), result.getRssi());
                }

                @Override
                public void onBatchScanResults(List<ScanResult> results) {
                    super.onBatchScanResults(results);
                }

                @Override
                public void onScanFailed(int errorCode) {
                    super.onScanFailed(errorCode);
                }
            };
        }

        LocalBroadcastManager.getInstance(binderActivity).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
        serviceStarted = true;
    }

    /**
     * Method that stops the service.
     */
    public void service_stop() {
        if (serviceStarted) {
            try {
                LocalBroadcastManager.getInstance(binderActivity).unregisterReceiver(UARTStatusChangeReceiver);
            } catch (Exception ignore) {
                Log.e(TAG, ignore.toString());
            }

            binderActivity.unbindService(mServiceConnection);
            if (uartService != null) {
                uartService.stopSelf();
                uartService = null;
            }
            serviceStarted = false;
        }
    }

    private IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
        return intentFilter;
    }

    /**
     * Getters and Setters
     */
    public BtDevice getDevice() {
        return mDevice;
    }

    public void setDevice(BtDevice device) {
        mDevice = device;
    }
}
