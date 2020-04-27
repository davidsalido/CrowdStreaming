package com.crowdstreaming.ui.avaliablesstreamings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.aware.WifiAwareSession;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.crowdstreaming.R;
import com.crowdstreaming.net.Subscriber;
import com.crowdstreaming.net.SubscriberSingleton;
import com.crowdstreaming.net.WifiAwareSessionUtillities;
import com.crowdstreaming.ui.watchstreaming.WatchStreamingActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AvaliablesStreamingsFragment extends Fragment implements AvaliablesStreamingsView {

    private ListView streamingList;
    private List<String> devices;
    private ArrayAdapter arrayAdapter;
    private Subscriber subscriber;
    private ProgressBar progressBar;
    private String videoFilePath;
    private SharedPreferences preferences;

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
        progressBar = getActivity().findViewById(R.id.progressBar);
        streamingList.setVisibility(View.VISIBLE);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        WifiAwareSession session = WifiAwareSessionUtillities.getSession();
        subscriber = new Subscriber(AvaliablesStreamingsFragment.this);
        SubscriberSingleton.subscriber = subscriber;
        session.subscribe(Subscriber.CONFIGSUBS,subscriber,null);



        streamingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                streamingList.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                subscriber.realizaConexion();

            }
        });
    }

    public void cambiarVista(){
        progressBar.setVisibility(View.INVISIBLE);


        Intent intent = new Intent(getContext(), WatchStreamingActivity.class);
        intent.putExtra("path",videoFilePath);
        getActivity().startActivity(intent);

        //streamingList.setVisibility(View.VISIBLE);

    }

    @Override
    public void saveFile() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = null;
                    if(preferences.getBoolean("guardar", false)){
                        videoFilePath = createVideoFilePath();
                        file = new File(videoFilePath);
                    }
                    else{
                        file = File.createTempFile("tempvideo",".mp4", getContext().getCacheDir());
                        videoFilePath = file.getAbsolutePath();
                        System.out.println("Path nuestro: " + videoFilePath);
                        file.deleteOnExit();
                    }
                    subscriber.saveFile(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    private String createVideoFilePath(){
        String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        String filename = sdf.format(cal.getTime());
        filename = filename.replaceAll(" ", "_");
        filename = filename.replaceAll(":", "-");
        return getActivity().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + filename + ".mp4";
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



}
