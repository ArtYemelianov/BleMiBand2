package com.example.artus.ble_immediatealert;

import java.util.GregorianCalendar;

/**
 * @author Artem Emelyanov
 */
public class BatteryInfo {
    public static final byte DEVICE_BATTERY_NORMAL = 0;
    public static final byte DEVICE_BATTERY_CHARGING = 1;
    private byte[] mData;

    public BatteryInfo(byte[] data) {
        mData = data;
    }

    public int getLevelInPercent() {
        if (mData.length >= 2) {
            return mData[1];
        }
        return 50; // actually unknown
    }

    public BatteryState getState() {
        if (mData.length >= 3) {
            int value = mData[2];
            switch (value) {
                case DEVICE_BATTERY_NORMAL:
                    return BatteryState.BATTERY_NORMAL;
                case DEVICE_BATTERY_CHARGING:
                    return BatteryState.BATTERY_CHARGING;
//                case DEVICE_BATTERY_CHARGING:
//                    return BatteryState.BATTERY_CHARGING;
//                case DEVICE_BATTERY_CHARGING_FULL:
//                    return BatteryState.BATTERY_CHARGING_FULL;
//                case DEVICE_BATTERY_CHARGE_OFF:
//                    return BatteryState.BATTERY_NOT_CHARGING_FULL;
            }
        }
        return BatteryState.UNKNOWN;
    }

    public int getLastChargeLevelInParcent() {
        if (mData.length >= 20) {
            return mData[19];
        }
        return 50; // actually unknown
    }

    public static int toUint16(byte... bytes) {
        return (bytes[0] & 0xff) | ((bytes[1] & 0xff) << 8);
    }

    public GregorianCalendar getLastChargeTime() {
        GregorianCalendar lastCharge = new GregorianCalendar();

        if (mData.length >= 18) {
            byte[] value = new byte[]{
                    mData[10], mData[11], mData[12], mData[13], mData[14], mData[15], mData[16],
                    mData[17]};
            if (value.length >= 7) {
                int year = toUint16(value[0], value[1]);
                GregorianCalendar timestamp = new GregorianCalendar(
                        year,
                        (value[2] & 0xff) - 1,
                        value[3] & 0xff,
                        value[4] & 0xff,
                        value[5] & 0xff,
                        value[6] & 0xff
                );

                lastCharge = timestamp;
            }
        }

        return lastCharge;
    }

    public int getNumCharges() {
//        if (mData.length >= 10) {
//            return ((0xff & mData[7]) | ((0xff & mData[8]) << 8));
//
//        }
        return -1;
    }

    public enum BatteryState {
        UNKNOWN,
        BATTERY_NORMAL,
        BATTERY_LOW,
        BATTERY_CHARGING,
        BATTERY_CHARGING_FULL,
        BATTERY_NOT_CHARGING_FULL
    }
}
