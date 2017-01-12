package com.example.artus.ble_immediatealert;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import java.util.List;

/**
 * Created by artus on 13.12.16.
 */
public class GatListener extends BluetoothGattCallback {
    private static String TAG = GatListener.class.toString();

    interface CharacterisListener {
        void onAddServices(List<BluetoothGattService> aServices);

        void onAddCharacteristic(List<BluetoothGattCharacteristic> aCharacteristic);
    }

    private final CharacterisListener mListener;

    public GatListener(CharacterisListener aListener) {
        mListener = aListener;
    }

    private void printCallBack(String aMethod, BluetoothGatt aGatt, int aStatus) {
        List<BluetoothGattService> services = aGatt.getServices();
        Log.d(TAG, String.format("%s size %s, status %d", aMethod, services.size(), aStatus));
        for (BluetoothGattService item : services) {
            String description = String.format("uuid %s", item.getUuid());
            Log.d(TAG, String.format("service item %s ", description));
        }
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
        printCallBack("onCharacteristicRead", gatt, status);
    }

    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
        printCallBack("onCharacteristicWrite", gatt, status);
    }

    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
        printCallBack("onCharacteristicChanged", gatt, 0);
    }

    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorRead(gatt, descriptor, status);
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