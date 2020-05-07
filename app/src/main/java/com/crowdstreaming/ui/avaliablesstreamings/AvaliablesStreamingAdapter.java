package com.crowdstreaming.ui.avaliablesstreamings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.crowdstreaming.R;

import java.util.ArrayList;

public class AvaliablesStreamingAdapter extends RecyclerView.Adapter<AvaliablesStreamingAdapter.ViewHolder>{

    private ArrayList<AvaliablesStreamingListData> listdata;
    private AvaliablesStreamingsFragment fragment;

    public AvaliablesStreamingAdapter(ArrayList<AvaliablesStreamingListData> listdata,AvaliablesStreamingsFragment fragment ){
        this.fragment = fragment;
        this.listdata = listdata;
    }

    @NonNull
    @Override
    public AvaliablesStreamingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_item_streaming, parent, false);
        AvaliablesStreamingAdapter.ViewHolder viewHolder = new AvaliablesStreamingAdapter.ViewHolder(listItem);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AvaliablesStreamingAdapter.ViewHolder holder, int position) {
        final AvaliablesStreamingListData myListData = listdata.get(position);
        holder.name.setText(myListData.getName());
        holder.mac.setText("MAC: " + myListData.getMac());

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.pulsarDevice();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name, mac;
        private ConstraintLayout constraintLayout;

        private ViewHolder(View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.name);
            this.mac = itemView.findViewById(R.id.mac);
            this.constraintLayout = itemView.findViewById(R.id.streamingconstraint);
        }
    }
}
