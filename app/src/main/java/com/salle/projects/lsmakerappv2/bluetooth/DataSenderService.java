package com.salle.projects.lsmakerappv2.bluetooth;

import android.app.IntentService;
import android.content.Intent;

import com.salle.projects.lsmakerappv2.bluetooth.adapters.DataSenderAdapter;
import com.salle.projects.lsmakerappv2.view.managers.DrivingDataManager;

/**
 * Service that retrieve information from the current DrivingFragment and sends it to the
 * Service to communicate with the robot.
 *
 * @author Eduard de Torres
 * @version 1.0.1
 */
public class DataSenderService extends IntentService {

    private static final String TAG = DataSenderService.class.getName();
    private static final int WAIT_TIME = 100; //0.1 seconds

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public DataSenderService() {
        super("DataSenderServiceThread");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public DataSenderService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        DataSenderAdapter dataSenderAdapter = new DataSenderAdapter();
        DrivingDataManager observer = DrivingDataManager.getInstance();
        boolean moving = false;

        while (observer.isRunning()) {
            int speed = observer.getSpeed();
            int turn = observer.getTurn();
            int acceleration = 0;

            //Log.d("DATASENDER", speed + " " + turn);
            if (speed != 0 || turn != 0 || moving) {
                moving = true;
                //Log.d(TAG, speed + " " + turn);
                BluetoothService.getInstance().sendMessage(
                        dataSenderAdapter.generateMovementFrame(speed, acceleration, turn));
                if (speed == 0 && turn == 0) {
                    moving = false;
                }
            }

            try {
                Thread.sleep(WAIT_TIME);
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }
        }

    }

}
