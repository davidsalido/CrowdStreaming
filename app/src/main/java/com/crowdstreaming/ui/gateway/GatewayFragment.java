package com.crowdstreaming.ui.gateway;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crowdstreaming.R;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class GatewayFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<GatewayListData> gatewayListDatas;
    private GatewayAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gateway, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        recyclerView = getActivity().findViewById(R.id.listGateway);

        gatewayListDatas = new ArrayList<>();
        adapter = new GatewayAdapter(gatewayListDatas, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1 ,GridLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(adapter);

        if(checkInternetConnecion()) {
            final Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL("https://securedrop.org/api/v1/directory/");
                        URLConnection con = url.openConnection();
                        InputStream in = con.getInputStream();
                        String encoding = con.getContentEncoding();
                        encoding = encoding == null ? "UTF-8" : encoding;

                        String jsonString = IOUtils.toString(in, encoding);
                        JSONArray arr = new JSONArray(jsonString);


                        for (int i = 0; i < arr.length(); i++) {
                            String title = arr.getJSONObject(i).getString("title");
                            String onionUrl = arr.getJSONObject(i).getString("onion_address");
                            String logoUrl = "https://securedrop.org" + arr.getJSONObject(i).getJSONObject("organization_logo").getString("url");
                            GatewayListData data = new GatewayListData(title, onionUrl, logoUrl);

                            gatewayListDatas.add(data);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    } catch (Exception e) {

                    }

                }
            });
            t.start();
        }
        else{
            Toast.makeText(getContext(),"No dispones de conexión a internet",Toast.LENGTH_LONG).show();
        }


    }

    public void openUrl(String url){
        Uri uri = Uri.parse("http://" + url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private boolean checkInternetConnecion() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
