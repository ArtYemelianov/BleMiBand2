package com.example.artus.ble_immediatealert;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import com.google.common.primitives.Bytes;

import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by artus on 13.12.16.
 */
public class GatListener extends BluetoothGattCallback {
    private static String TAG = GatListener.class.toString();

    interface CharacterisListener {
        void onAddServices(List<BluetoothGattService> aServices);

        void onAddCharacteristic(List<BluetoothGattCharacteristic> aCharacteristic);

        void onBatteryRead(byte[] aValue, int aLevel, BatteryInfo.BatteryState aState, GregorianCalendar aLastTime);

        void onAuthRead(byte[] value, int aLevel);

        void onAuthReadDescriptor(byte[] aValue);

        void onCharacteristicChanged(String aValue);
    }

    private final CharacterisListener mListener;

    public GatListener(CharacterisListener aListener) {
        mListener = aListener;
    }

    private void printCallBack(String aMethod, BluetoothGatt aGatt, int aStatus) {
        List<BluetoothGattService> services = aGatt.getServices();
        Log.d(TAG, String.format("%s size %s, status %d", aMethod, services.size(), aStatus));
//        for (BluetoothGattService item : services) {
//            String description = String.format("uuid %s", item.getUuid());
//            Log.d(TAG, String.format("service item %s ", description));
//        }
    }

    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        printCallBack(String.format("onConnectionStateChange, new state %d", newState), gatt, status);
        gatt.discoverServices();
    }


    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);
        printCallBack("onServicesDiscovered", gatt, status);
        mListener.onAddServices(gatt.getServices());

        for (BluetoothGattService item : gatt.getServices()) {
            mListener.onAddCharacteristic(item.getCharacteristics());

            enableNotifications(item, gatt);
        }
    }

    private void enableNotifications(BluetoothGattService aService, BluetoothGatt aBluetoothGatt) {
        for (BluetoothGattCharacteristic item : aService.getCharacteristics()) {
            for (BluetoothGattDescriptor descriptor : item.getDescriptors()) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                aBluetoothGatt.writeDescriptor(descriptor);
            }
        }
    }

    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicRead(gatt, characteristic, status);
        if (characteristic.getUuid().equals(MainActivity.BATTERY_INFO_CHARACTERISTIC)) {
            handleBattryInfo(gatt, characteristic);
        } else if (characteristic.getUuid().equals(NotifyAction.AUTH_UUID)) {
            handleAuthRead(gatt, characteristic);
        }
        printCallBack("onCharacteristicRead", gatt, status);
    }

    private void handleAuthRead(BluetoothGatt aGatt, BluetoothGattCharacteristic aCharacteristic) {
        byte[] arr = aCharacteristic.getValue();
        mListener.onAuthRead(arr, 0);
    }

    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
        printCallBack("onCharacteristicWrite", gatt, status);
    }

    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
        printCallBack("onCharacteristicChanged", gatt, 0);
        List<Byte> objectArray = Bytes.asList(characteristic.getValue());
        String value = objectArray.toString();
        Log.d(TAG, String.format("value is %s %s ", characteristic.getUuid(), value));
        mListener.onCharacteristicChanged(value);
    }

    private void handleBattryInfo(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        byte[] value = characteristic.getValue();
        BatteryInfo info = new BatteryInfo(value);
        int level = info.getLevelInPercent();
        BatteryInfo.BatteryState state = info.getState();

        GregorianCalendar lastTime = info.getLastChargeTime();
        mListener.onBatteryRead(value, level, state, lastTime);
    }

    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorRead(gatt, descriptor, status);
        mListener.onAuthReadDescriptor(descriptor.getValue());
        printCallBack("onDescriptorRead", gatt, status);
    }

    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorWrite(gatt, descriptor, status);
        printCallBack("onDescriptorWrite", gatt, status);
    }

    public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
        super.onReliableWriteCompleted(gatt, status);
        printCallBack("onReliableWriteCompleted", gatt, status);
    }

    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        super.onReadRemoteRssi(gatt, rssi, status);
        printCallBack("onReadRemoteRssi", gatt, status);
    }

    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
        super.onMtuChanged(gatt, mtu, status);
        printCallBack("onMtuChanged", gatt, status);
    }

}