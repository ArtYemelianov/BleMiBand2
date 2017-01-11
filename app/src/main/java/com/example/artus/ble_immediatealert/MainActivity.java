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
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements EditNameDialogListener {

    Handler mHandler;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mBluetoothDevice;
    TextView mLabel;
    List<String> mData = new ArrayList<>();

    ListView mListView;
    List<BluetoothGattService> mServices = new ArrayList<>();
    List<BluetoothGattCharacteristic> mCharacteristic = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new Handler(Looper.getMainLooper());
        mListView = (ListView) findViewById(R.id.list);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentManager manager = getSupportFragmentManager();
                BluetoothGattCharacteristic ch = mCharacteristic.get(position);
                DialogFragment dialog = EditNameDialogFragment.newInstance(ch);
                dialog.show(manager, "dialog");
            }
        });
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mData);

        mListView.setAdapter(adapter);

        mLabel = (TextView) MainActivity.this.findViewById(R.id.label_main);

        Button connect = (Button) findViewById(R.id.btn_connect);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleConnect();
            }
        });

        Button trrigger = (Button) findViewById(R.id.btn_trigger_alert);
        trrigger.setEnabled(false);
        trrigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleTrigger();
            }
        });

        Button putValue = (Button) findViewById(R.id.put_value);
        putValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
                if (mCh == null) {
                    Toast.makeText(MainActivity.this, "Cha Is null", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(MainActivity.this, "CSet value", Toast.LENGTH_SHORT).show();
                BluetoothGattCharacteristic ch = mCh.getCharacteristics().get(0);
                ch.setValue(new byte[]{02});
                mGatt.writeCharacteristic(ch);
            }
        });

        Button batteryLevel = (Button) findViewById(R.id.btn_battery_level);
        batteryLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getSupportFragmentManager();
                DialogFragment dialog = EditNameDialogFragment.newInstance(null);
                dialog.show(manager, "dialog");
            }
        });


        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "Ble not support", Toast.LENGTH_SHORT).show();
            connect.setEnabled(false);
            trrigger.setEnabled(false);
        }
    }

    public BluetoothGatt mGatt;
    private BluetoothGattService mCh;
    private final GatListener.CharacterisListener mListener = new GatListener.CharacterisListener() {
        @Override
        public void onStored(BluetoothGatt gatt, BluetoothGattService aCh) {
            mCh = aCh;
            mGatt = gatt;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    findViewById(R.id.put_value).setEnabled(true);
                }
            });
        }

        @Override
        public void onAddServices(List<BluetoothGattService> aServices) {
            mServices.addAll(aServices);
        }

        @Override
        public void onAddCharacteristic(List<BluetoothGattCharacteristic> aCharacteristic) {
            mCharacteristic.addAll(aCharacteristic);
            List data = Lists.transform(aCharacteristic, new Function<BluetoothGattCharacteristic, String>() {
                @Override
                public String apply(BluetoothGattCharacteristic input) {
                    return input.getUuid().toString();
                }
            });
            mData.addAll(data);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    ((ArrayAdapter) mListView.getAdapter()).notifyDataSetChanged();
                }
            });

        }
    };

    private void handleTrigger() {
        if (mBluetoothDevice != null) {
            BluetoothGatt bluetoothGatt = mBluetoothDevice.connectGatt(this, false, new GatListener(mListener));
            bluetoothGatt.connect();
            List<BluetoothGattService> services = bluetoothGatt.getServices();
            for (BluetoothGattService item : services) {
                String description = String.format("uuid %s", item.getUuid());
                Log.d("Services", description);
            }
            bluetoothGatt.discoverServices();
        }
    }

    private void handleConnect() {
// Initializes Bluetooth adapter.
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

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothAdapter.stopLeScan(mCallBack);
                }
            }, 10000);

            mBluetoothAdapter.startLeScan(mCallBack);
        } else {
            mBluetoothAdapter.stopLeScan(mCallBack);
        }
    }

    private final BluetoothAdapter.LeScanCallback mCallBack = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
            String address = bluetoothDevice.getName();
            Log.d("LE Bluetooth", String.format("address %s", address));
            if (address.contains("Angel")) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mLabel.setText(bluetoothDevice.getAddress());
                        findViewById(R.id.btn_trigger_alert).setEnabled(true);
                    }
                });

                mBluetoothDevice = bluetoothDevice;
            }
        }
    };

    @Override
    public void onValueChanged(final String aUuid, String aValue) {
        BluetoothGattCharacteristic characteristic = Iterables.find(mCharacteristic, new Predicate<BluetoothGattCharacteristic>() {
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

