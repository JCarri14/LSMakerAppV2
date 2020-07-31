package com.salle.projects.lsmakerappv2.view.managers;

public class DrivingDataManager {

    // Singleton attributes
    private static DrivingDataManager instance;
    private static Object mutex = new Object();

    // Constants
    public String JOYSTICK_MODE_VERT = "Joystick (Vertical)";
    public String JOYSTICK_MODE_HOR = "Joystick (Horizontal)";
    public String DEVICE_ROTATION_MODE = "Device Rotation";

    // Data attributes
    private int speed;
    private int turn;
    private boolean running;
    private String dataSource;

    public static DrivingDataManager getInstance() {
        DrivingDataManager result = instance;
        if (result == null) {
            synchronized (mutex) {
                result = instance;
                if (result == null)
                    instance = result = new DrivingDataManager();
            }
        }
        return result;
    }

    private DrivingDataManager() {
        this.dataSource = JOYSTICK_MODE_VERT;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public void setSpeedAndTurn(int speed, int turn) {
        setSpeed(speed);
        setTurn(turn);
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }
}
