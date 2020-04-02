package com.crowdstreaming.ui.gallery;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.crowdstreaming.R;
import com.crowdstreaming.ui.watchstreaming.WatchStreamingActivity;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder>{

    private GalleryListData[] listdata;
    private GalleryFragment fragment;

    // RecyclerView recyclerView;
    public GalleryAdapter(GalleryListData[] listdata, GalleryFragment fragment) {
        this.listdata = listdata;
        this.fragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final GalleryListData myListData = listdata[position];
        holder.textView.setText(listdata[position].getPath());
        holder.imageView.setImageBitmap(listdata[position].getThumbail());
        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fragment.startVideo(position);

            }
        });
    }


    @Override
    public int getItemCount() {
        return listdata.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;
        private ConstraintLayout constraintLayout;

        private ViewHolder(View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.imageView);
            this.textView = itemView.findViewById(R.id.textView2);
            constraintLayout = itemView.findViewById(R.id.constraint);
        }
    }
}



