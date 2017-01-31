package com.example.artus.ble_immediatealert;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.util.Log;

import java.util.UUID;

/**
 * @author Artem Emelyanov
 */
public class NotifyAction {
    private final BluetoothGattCharacteristic mCharacteristic;
    private BluetoothGatt mGatt;

    public NotifyAction(BluetoothGatt aGatt, BluetoothGattCharacteristic aCharacteristic) {
        mGatt = aGatt;
        mCharacteristic = aCharacteristic;
    }


    private static byte[] AUTH = new byte[]{0x01, 0x08, 0x48, 0x49, 0x50, 0x51,
                                            0x52, 0x53, 0x54, 0x55, 0x56, 0x57,
                                            0x64, 0x65, 0x66, 0x67, 0x68, 0x69};
    public static UUID AUTH_UUID = UUID.fromString("00000009-0000-3512-2118-0009af100700");


    public void enableNotification(boolean aEnable) {
        BluetoothGattDescriptor notifyDescriptor = mCharacteristic.getDescriptors().get(0);
        notifyDescriptor.setValue(aEnable ?
                                  BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                                          : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        mGatt.writeDescriptor(notifyDescriptor);
    }

    public void sendData() {
        mCharacteristic.setValue(AUTH);
        boolean result = mGatt.setCharacteristicNotification(mCharacteristic, true);
        result = result & mGatt.writeCharacteristic(mCharacteristic);
    }

    public void run() {

    }

    public String readData() {
        mGatt.readCharacteristic(mCharacteristic);

        int properties = mCharacteristic.getProperties();
        int permission = mCharacteristic.getPermissions();
        return String.format("per: %X, properties: %X, %s", permission, properties, mCharacteristic.getUuid());
    }

    void readDescriptor() {
        BluetoothGattDescriptor desc = mCharacteristic.getDescriptors().get(0);
        mGatt.readDescriptor(desc);
    }
}
