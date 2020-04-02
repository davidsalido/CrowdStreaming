package com.crowdstreaming.ui.gateway;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.crowdstreaming.R;

import info.guardianproject.netcipher.proxy.OrbotHelper;
import info.guardianproject.netcipher.proxy.StatusCallback;


public class GatewayFragment extends Fragment {

    private WebView webView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        return inflater.inflate(R.layout.fragment_gateway, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();


        webView = getActivity().findViewById(R.id.webview);
        GenericWebViewClient webViewClient = new GenericWebViewClient();

        webViewClient.setRequestCounterListener(new GenericWebViewClient.RequestCounterListener() {
            @Override
            public void countChanged(int requestCount) {
                System.out.println("request count: " + requestCount);
            }
        });

        webView.setWebViewClient(webViewClient);
        webView.loadUrl("http://33y6fjyhs3phzfjj.onion");

    }
}
