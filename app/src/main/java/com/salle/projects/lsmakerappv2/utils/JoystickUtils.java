package com.salle.projects.lsmakerappv2.utils;

import android.util.Log;

public class JoystickUtils {

    private static final String TAG = JoystickUtils.class.getName();

    public static int getDirectionFromParams(int angle, int strength) {
        //Log.d(TAG, "Angle: " + angle + ", Strength: " + strength);
        if (isRightTurn(angle)) {
            return getRightTurnValue(angle, strength);
        } else {
            if (isLeftTurn(angle)) {
                return -getLeftTurnValue(angle, strength);
            }
        }
        return 0;
    }

    private static boolean isLeftTurn(int angle) {
        return angle > 90 && angle < 270;
    }

    private static boolean isRightTurn(int angle) {
        return angle < 90 || angle > 270;
    }

    private static int getRightTurnValue(int angle, int strength) {
        int res = (strength/100);
        if (angle < 90) {
            res = ((angle * (-100))/90)+100;
        } else {
            if (angle > 270) {
                angle = 360 - angle; // MIN Value would be 271 and MAX value 359;
                res = ((angle * (-100))/90)+100;
            }
        }
        res = res - (strength/100);
        return res;
    }

    private static int getLeftTurnValue(int angle, int strength) {
        int res = (strength/100);
        if (angle < 180) {
            // We want to be always working with values between 0 and 90;
            angle = 180 - angle;
        } else {
            // Knowing that the Min value at this case would be 180 and because an angle of 0
            // would mean 100% of that direction...
            angle = angle-180;
        }
        angle = ((angle * (-100))/90)+100;
        res = angle - (strength/90);
        return res;
    }
}
