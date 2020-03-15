package com.crowdstreaming.ui.avaliablesstreamings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.crowdstreaming.R;

import java.util.ArrayList;
import java.util.List;

public class AvaliablesStreamingsFragment extends Fragment implements AvaliablesStreamingsView {

    private AvaliablesStreamingsPresenter presenter;
    private ListView streamingList;
    private List<String> devices;
    private ArrayAdapter arrayAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_avaliables, container, false);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        devices = new ArrayList<>();
        streamingList = getActivity().findViewById(R.id.streaminglist);
        arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, devices);
        streamingList.setAdapter(arrayAdapter);

        presenter = new AvaliablesStreamingsPresenter(this);
        presenter.viewCreated();
    }


    @Override
    public void addDevice(String device) {
        devices.add(device);
        arrayAdapter.notifyDataSetChanged();
    }
}
