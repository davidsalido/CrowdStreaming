package com.crowdstreaming.ui.streaming;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.crowdstreaming.R;

public class StreamingFragment extends Fragment {

    SharedPreferences preferences;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_streaming, container, false);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

}
