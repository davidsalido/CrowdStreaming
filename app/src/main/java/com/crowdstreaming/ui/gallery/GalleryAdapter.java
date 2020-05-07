package com.crowdstreaming.ui.gallery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.crowdstreaming.R;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder>{

    private ArrayList<GalleryListData> listdata;
    private GalleryFragment fragment;

    // RecyclerView recyclerView;
    public GalleryAdapter(ArrayList<GalleryListData> listdata, GalleryFragment fragment) {
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
        final GalleryListData myListData = listdata.get(position);
        holder.textView.setText(listdata.get(position).getPath());
        holder.imageView.setImageBitmap(listdata.get(position).getThumbail());
        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.startVideo(position);
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
            this.imageView = itemView.findViewById(R.id.imageGallery);
            this.textView = itemView.findViewById(R.id.titlegallery);
            constraintLayout = itemView.findViewById(R.id.galleryconstraint);
        }
    }
}



