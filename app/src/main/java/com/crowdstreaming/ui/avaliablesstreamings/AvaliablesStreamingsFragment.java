package com.crowdstreaming.ui.avaliablesstreamings;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.crowdstreaming.R;
import com.crowdstreaming.ui.watchstreaming.WatchStreamingActivity;

import java.io.File;
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

        streamingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                presenter.realizaConexion();
            }
        });
    }

    public void cambiarVista(){
        Intent intent = new Intent(getContext(), WatchStreamingActivity.class);
        getActivity().startActivity(intent);
    }


    @Override
    public void addDevice(String device) {
        devices.add(device);
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    public ConnectivityManager getConnectivityManager() {
        return (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    public File carpeta() {
        return getActivity().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
    }
}
