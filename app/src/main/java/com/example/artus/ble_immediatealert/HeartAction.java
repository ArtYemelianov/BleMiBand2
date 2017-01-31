package com.example.artus.ble_immediatealert;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

import java.util.UUID;

/**
 * Created by artus on 30.01.17.
 */

public class HeartAction {
    private final BluetoothGattCharacteristic mCharacteristic;
    private BluetoothGatt mGatt;

    private static final String BASE_UUID = "0000%s-0000-1000-8000-00805f9b34fb";
    public static final UUID UUID_CHAR_HEART_RATE_MEASUREMENT = UUID.fromString(String.format(BASE_UUID, "2a37"));

    public HeartAction(BluetoothGatt aGatt, BluetoothGattCharacteristic aCharacteristic) {
        mGatt = aGatt;
        mCharacteristic = aCharacteristic;
    }

    public void enableNotification(boolean aEnable) {
        boolean result = mGatt.setCharacteristicNotification(mCharacteristic, true);

        BluetoothGattDescriptor notifyDescriptor = mCharacteristic.getDescriptors().get(0);

        byte[] value = aEnable ?
                BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
//        value = BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;

        notifyDescriptor.setValue(value);

        mGatt.writeDescriptor(notifyDescriptor);
    }

    public String readData() {
        int properties = mCharacteristic.getProperties();
        int permission = mCharacteristic.getPermissions();
        return String.format("per: %X, properties: %X, %s", permission, properties, mCharacteristic.getUuid());
    }

    public boolean readDescriptor() {
        BluetoothGattDescriptor desc = mCharacteristic.getDescriptors().get(0);
        return mGatt.readDescriptor(desc);
    }
}
