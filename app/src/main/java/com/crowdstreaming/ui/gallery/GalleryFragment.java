package com.crowdstreaming.ui.gallery;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

import com.crowdstreaming.R;

import java.util.prefs.PreferencesFactory;

public class GalleryFragment extends Fragment {

    SharedPreferences preferences;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());



        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        Button btn = getActivity().findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean guardar = preferences.getBoolean("guardar",false);
                TextView tv = getActivity().findViewById(R.id.text_gallery);
                tv.setText("adios" + guardar);
            }
        });
    }

}
