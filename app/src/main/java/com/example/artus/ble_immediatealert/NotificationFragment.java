package com.example.artus.ble_immediatealert;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.common.primitives.Bytes;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;
import java.util.UUID;

/**
 * @author Artem Emelyanov
 */
@EFragment(R.layout.dialog_fragment)
public class NotificationFragment extends DialogFragment {
    @ViewById(R.id.btn_send_data)
    Button mSendDataBtn;

    @ViewById(R.id.btn_read_data)
    Button mReadDataBtn;

    @ViewById(R.id.btn_enable_notification)
    Button mEnableNotificationBtn;

    @ViewById(R.id.btn_read_descriptor)
    Button mReadDescriptorBtn;

    @ViewById(R.id.txt_optionalInfo)
    TextView mOptionalInfo;

    @ViewById(R.id.chk_switch)
    CheckBox mCheckBox;

    private BluetoothGatt mGatt;
    private BluetoothGattCharacteristic mCharacteristic;
    private NotifyAction mAction;
    private IActivityPersistence mPersistence;

    @Click(R.id.btn_read_data)
    void handleReadData() {
        String read = mAction.readData();
        int data= 0;
    }

    @Click(R.id.btn_read_descriptor)
    void handleReadDescriptor() {
        mAction.readDescriptor();
    }

    @Click(R.id.btn_send_data)
    void handleSendData() {
        mAction.sendData();
    }

    @Click(R.id.btn_enable_notification)
    void handleSwitched() {
        boolean isCheched = mCheckBox.isChecked();
        mAction.enableNotification(isCheched);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mPersistence = (IActivityPersistence) context;
        mAction = new NotifyAction(mPersistence.getGatt(),
                mPersistence.getCharacteristic(NotifyAction.AUTH_UUID));
    }
    @UiThread
    void setReadValue(String aValue) {
        mOptionalInfo.setText(aValue);
    }

    @UiThread
    void setReadValue(byte[] aValue) {
        List<Byte> objectArray = Bytes.asList(aValue);
        String value = objectArray.toString();
        setReadValue(value);
    }
}

interface IActivityPersistence {
    BluetoothGattCharacteristic getCharacteristic(UUID aUUID);

    BluetoothGatt getGatt();
}
