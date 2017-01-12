package com.example.artus.ble_immediatealert;

import android.support.v4.app.DialogFragment;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

/**
 * @author Artem Emelyanov
 */
@EFragment(R.layout.list_layout)
public class FragmentInfo extends DialogFragment {

    @ViewById(android.R.id.list)
    ListView mListView;

    @FragmentArg
    ArrayList<String> mData;

    @AfterViews
    void init() {
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, mData);
        mListView.setAdapter(adapter);
    }


}
