package com.crowdstreaming.ui.gateway;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.crowdstreaming.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GatewayAdapter extends RecyclerView.Adapter<GatewayAdapter.ViewHolder>{

    private ArrayList<GatewayListData> listdata;
    private GatewayFragment fragment;

    // RecyclerView recyclerView;
    public GatewayAdapter(ArrayList<GatewayListData> listdata, GatewayFragment fragment) {
        this.listdata = listdata;
        this.fragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_item_gateway, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final GatewayListData myListData = listdata.get(position);

        holder.textView.setText(listdata.get(position).getTitle());
        Picasso.get().load(myListData.getLogoUrl()).into(holder.imageView);

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.openUrl(myListData.getOnionUrl());
            }
        });
    }


    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;
        private ConstraintLayout constraintLayout;

        private ViewHolder(View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.imageView);
            this.textView = itemView.findViewById(R.id.titlegateway);
            constraintLayout = itemView.findViewById(R.id.gatewayconstraint);
        }
    }
}



