package com.example.artus.ble_immediatealert;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private static String TAG = FirstFragment.class.toString();

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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");

        outState.putStringArrayList("array", mData);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
        if (savedInstanceState != null) {
            mData.addAll(savedInstanceState.getStringArrayList("array"));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onActivityCreated");

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach context");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "onAttach activity");
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
        Log.d(TAG, "onAttachFragment");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
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