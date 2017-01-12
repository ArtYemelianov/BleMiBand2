package com.example.artus.ble_immediatealert;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@EFragment(R.layout.fragment_first)
public class FirstFragment extends Fragment {

    @ViewById(R.id.list)
    ListView mListView;

    @FragmentArg("title")
    String mTitle;
    @FragmentArg("array")
    ArrayList<String> mData;

    // newInstance constructor for creating fragment with arguments
    public void construct(String title) {
        construct(title, new ArrayList<String>());
    }


    // newInstance constructor for creating fragment with arguments
    public void construct(String title, ArrayList<String> aArray) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putStringArrayList("array", aArray);
        setArguments(args);
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTitle = getArguments().getString("title", "Unknown");
        mData = getArguments().getStringArrayList("array");
        if (mData == null) {
            mData = new ArrayList<>();
        }
    }

    @AfterViews
    public void init() {
        ListAdapter adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, mData);
        mListView.setAdapter(adapter);
    }

    private FragmentListener listener() {
        return (FragmentListener) getActivity();
    }

    @ItemClick(R.id.list)
    void handleItemClick(int aPosition) {
        String uuid = mData.get(aPosition);
        listener().handleItemClick(UUID.fromString(uuid));
//        BluetoothGattCharacteristic ch = mCharacteristics.get(aPosition);
//        DialogFragment dialog = EditNameDialogFragment.newInstance(ch);
//        dialog.show(manager, "dialog");
    }

    @UiThread
    public void addData(List<String> aData) {
        mData.addAll(aData);
        ArrayAdapter adapter = (ArrayAdapter) mListView.getAdapter();
        adapter.notifyDataSetChanged();
    }

    public interface FragmentListener {
        void handleItemClick(UUID aUUID);
    }
}