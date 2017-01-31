package com.example.artus.ble_immediatealert;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

/**
 * The dialog message fragment.
 *
 * @author Artem Emelyanov
 */
@EFragment(R.layout.message_layout)
public class HeartFragment extends DialogFragment {

    @ViewById(R.id.dialog_read_descriptor_btn)
    Button mChangeDescriptor;

    @ViewById(R.id.dialog_description_label)
    TextView mDescriptionLabel;

    @ViewById(R.id.dialog_hear_rate_label)
    TextView mHeartLabel;

    @ViewById(R.id.dialog_read_heart_btn)
    Button mHeartBtn;

    @ViewById(R.id.switcher_descriptor)
    Switch mSwitcher;

    private HeartAction mAction;
    private IActivityPersistence mPersistence;

    @Click(R.id.dialog_change_descriptor_btn)
    void handleChangeDescriptor() {
        boolean isChecked = mSwitcher.isChecked();
        mAction.enableNotification(isChecked);
    }

    @Click(R.id.dialog_read_descriptor_btn)
    void handleReadDescriptor() {
        mAction.readDescriptor();
    }

    @Click(R.id.dialog_read_heart_btn)
    void handleReadRate() {
        String result  = mAction.readData();
        setHeartRate(String.valueOf(result));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mPersistence = (IActivityPersistence) context;
        mAction = new HeartAction(mPersistence.getGatt(),
                mPersistence.getCharacteristic(HeartAction.UUID_CHAR_HEART_RATE_MEASUREMENT));
    }

    @UiThread
    void setDescriptor(String aValue) {
        mDescriptionLabel.setText(aValue);
    }

    @UiThread
    void setHeartRate(String aValue) {
        mHeartLabel.setText(aValue);
    }
}
