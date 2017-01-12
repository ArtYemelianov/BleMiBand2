package com.example.artus.ble_immediatealert;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity implements EditNameDialogListener {
    private static String TAG = MainActivity.class.toString();
    private static UUID ALERT_LEVEL_CHARACTERISTIC = UUID.fromString("00002a06-0000-1000-8000-00805f9b34fb");
    /* Views */
    @ViewById(R.id.label_main)
    TextView mLabel;

    @ViewById(R.id.list)
    ListView mListView;

    @ViewById(R.id.btn_search)
    Button mSearchBtn;

    @ViewById(R.id.btn_battery_level)
    Button mBatteryLevelBtn;

    @ViewById(R.id.btn_connect)
    Button mConnectBtn;

    @ViewById(R.id.btn_trigger_alert)
    Button mTriggerAlertBtn;


    private Handler mHandler;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothGatt mGatt;
    BluetoothDevice mBluetoothDevice;

    List<String> mData = new ArrayList<>();


    List<BluetoothGattService> mServices = new ArrayList<>();
    List<BluetoothGattCharacteristic> mCharacteristics = new ArrayList<>();

    @ItemClick(R.id.list)
    void handleItemClick(int aPosition) {
        FragmentManager manager = getSupportFragmentManager();
        BluetoothGattCharacteristic ch = mCharacteristics.get(aPosition);
        DialogFragment dialog = EditNameDialogFragment.newInstance(ch);
        dialog.show(manager, "dialog");
    }

    @Click(R.id.btn_search)
    void handleSearch() {
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        } else {
            scanLeDevice(true);
        }
    }

    @Click(R.id.btn_connect)
    void handleConnect() {
        if (mBluetoothDevice != null) {
            mGatt = mBluetoothDevice.connectGatt(this, true, new GatListener(mListener));
            List<BluetoothGattService> services = mGatt.getServices();
            for (BluetoothGattService item : services) {
                String description = String.format("uuid %s", item.getUuid());
                Log.d(TAG, String.format("handleConnect, %s", description));
            }
        }
    }

    @Click(R.id.btn_trigger_alert)
    void handleTriggerAlert() {
        BluetoothGattCharacteristic ch = Iterables.find(mCharacteristics, new Predicate<BluetoothGattCharacteristic>() {
            @Override
            public boolean apply(BluetoothGattCharacteristic input) {
                return input.getUuid().equals(ALERT_LEVEL_CHARACTERISTIC);
            }
        });
        if (ch != null) {
            ch.setValue(new byte[]{02});
            mGatt.writeCharacteristic(ch);
        } else {
            showMessage("The immediate service not found");
        }
    }

    @UiThread
    void showMessage(String aText) {
        Toast.makeText(this, aText, Toast.LENGTH_SHORT).show();
    }

    @Click(R.id.btn_battery_level)
    void handleBatteryLevel() {
        //do nothing
    }

    @AfterViews
    public void init() {
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mData);
        mListView.setAdapter(adapter);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            showMessage("Ble not support");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler(Looper.getMainLooper());
    }

    private final GatListener.CharacterisListener mListener = new GatListener.CharacterisListener() {
        public void onStored(BluetoothGatt gatt, BluetoothGattService aCh) {
            mGatt = gatt;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mTriggerAlertBtn.setEnabled(true);
                }
            });
        }

        @Override
        public void onAddServices(List<BluetoothGattService> aServices) {
            mServices.addAll(aServices);
        }

        @Override
        public void onAddCharacteristic(List<BluetoothGattCharacteristic> aCharacteristic) {
            mCharacteristics.addAll(aCharacteristic);

            //stores uuids
            final List data = Lists.transform(aCharacteristic, new Function<BluetoothGattCharacteristic, String>() {
                @Override
                public String apply(BluetoothGattCharacteristic input) {
                    return input.getUuid().toString();
                }
            });
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mData.addAll(data);
                    if (mData.contains(ALERT_LEVEL_CHARACTERISTIC.toString())) {
                        mTriggerAlertBtn.setEnabled(true);
                    }
                    ((ArrayAdapter) mListView.getAdapter()).notifyDataSetChanged();
                }
            });

        }
    };

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothAdapter.stopLeScan(mStopedLeCallBack);
                }
            }, 10000);

            mBluetoothAdapter.startLeScan(mStartedLeCallBack);
        } else {
            mBluetoothAdapter.stopLeScan(mStopedLeCallBack);
        }
    }

    @UiThread
    void onDeviceFound(BluetoothDevice aDevice) {
        mLabel.setBackgroundColor(Color.GREEN);
        mLabel.setText(aDevice.getAddress());
        mConnectBtn.setEnabled(true);
        mBluetoothDevice = aDevice;
    }

    private final BluetoothAdapter.LeScanCallback mStopedLeCallBack = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice aBluetoothDevice, int i, byte[] bytes) {
            String address = aBluetoothDevice.getName();
            Log.d(TAG, String.format("mStopedLeCallBack, address %s", address));
        }
    };

    private final BluetoothAdapter.LeScanCallback mStartedLeCallBack = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice aBluetoothDevice, int i, byte[] bytes) {
            String address = aBluetoothDevice.getName();
            Log.d(TAG, String.format("mStartedLeCallBack, address %s", address));
            if (address.contains("Band")) {
                mBluetoothAdapter.stopLeScan(null);
                onDeviceFound(aBluetoothDevice);
            }
        }
    };

    @Override
    public void onValueChanged(final String aUuid, String aValue) {
        BluetoothGattCharacteristic characteristic = Iterables.find(mCharacteristics, new Predicate<BluetoothGattCharacteristic>() {
            public boolean apply(BluetoothGattCharacteristic uuid) {
                return uuid.getUuid().equals(UUID.fromString(aUuid));
            }
        });
        Integer data = Integer.parseInt(aValue, 16);
        final BigInteger bi = BigInteger.valueOf(data);
        final byte[] bytes = bi.toByteArray();
        characteristic.setValue(bytes);
        mGatt.writeCharacteristic(characteristic);
    }
}

