package com.example.artus.ble_immediatealert;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import java.util.List;
import java.util.UUID;

/**
 * Created by artus on 13.12.16.
 */
public class GatListener extends BluetoothGattCallback {

    interface CharacterisListener {
        void onStored(BluetoothGatt tt, BluetoothGattService aCh);

        void onAddServices(List<BluetoothGattService> aServices);

        void onAddCharacteristic(List<BluetoothGattCharacteristic> aCharacteristic);
    }

    private final CharacterisListener mListener;

    public GatListener(CharacterisListener aListener) {
        mListener = aListener;
    }

    private void printCharacteristic(String aMethod, BluetoothGattService aService) {
        List<BluetoothGattCharacteristic> services = aService.getCharacteristics();
        Log.d("printCharacteristic", String.format("%s size %s", aMethod, services.size()));
        for (BluetoothGattCharacteristic item : services) {
            String description = String.format("uuid %s", item.getUuid());
            Log.d("Characteristic", description);
        }
    }

    private void printCallBack(String aMethod, BluetoothGatt aGatt) {
        List<BluetoothGattService> services = aGatt.getServices();
        Log.d("PrintCalbacck", String.format("%s size %s", aMethod, services.size()));
        for (BluetoothGattService item : services) {
            String description = String.format("uuid %s", item.getUuid());
            Log.d("Services", description);
        }
    }

    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        printCallBack("onConnectionStateChange", gatt);
    }

    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        printCallBack("onServicesDiscovered", gatt);
        BluetoothGattService service = gatt.getService(UUID.fromString("00001802-0000-1000-8000-00805f9b34fb"));

        mListener.onAddServices(gatt.getServices());

        for (BluetoothGattService item : gatt.getServices()) {
            mListener.onAddCharacteristic(item.getCharacteristics());
        }

        if (service != null) {
            printCharacteristic("onServicesDiscovered", service);
            mListener.onStored(gatt, service);
        } else {
            Log.d("onServicesDiscovered", "Uuid is null");
        }

    }

    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        printCallBack("onCharacteristicRead", gatt);
    }

    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        printCallBack("onCharacteristicWrite", gatt);
    }

    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        printCallBack("onCharacteristicChanged", gatt);
    }

    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        printCallBack("onDescriptorRead", gatt);
    }

    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        printCallBack("onDescriptorWrite", gatt);
    }

    public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
        printCallBack("onReliableWriteCompleted", gatt);
    }

    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        printCallBack("onReadRemoteRssi", gatt);
    }

    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
        printCallBack("onMtuChanged", gatt);
    }
}
