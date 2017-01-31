package com.example.artus.ble_immediatealert;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public class EditNameDialogFragment extends DialogFragment {

    private EditText mEditText;

    public EditNameDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static EditNameDialogFragment newInstance(BluetoothGattCharacteristic aCh) {
        EditNameDialogFragment frag = new EditNameDialogFragment();
        Bundle args = new Bundle();
        args.putInt("permission", aCh.getPermissions());
        args.putInt("properties", aCh.getProperties());
        args.putString("uuid", aCh.getUuid().toString());
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_name, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final String uuid = getArguments().getString("uuid", "null");
        int properties = getArguments().getInt("properties");
        int permission = getArguments().getInt("permission");
        ((TextView) view.findViewById(R.id.dialog_permission)).setText(String.valueOf(permission));
        ((TextView) view.findViewById(R.id.dialog_properties)).setText(String.valueOf(properties));
        ((TextView) view.findViewById(R.id.dialog_uuid)).setText(uuid);

        mEditText = (EditText) view.findViewById(R.id.txt_your_name);
        getDialog().setTitle(uuid);
        // Show soft keyboard automatically and request focus to field
        mEditText.requestFocus();
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                                                @Override
                                                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                                                        String txt = v.getText().toString();
                                                        EditNameDialogListener listener = (EditNameDialogListener) EditNameDialogFragment.this.getContext();
                                                        listener.onValueChanged(uuid, txt);
                                                        dismiss();
                                                        return true;

                                                    }
                                                    return false;
                                                }
                                            }

        );

        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}

interface EditNameDialogListener {
    void onValueChanged(String aUuid, String aBytes);
}
