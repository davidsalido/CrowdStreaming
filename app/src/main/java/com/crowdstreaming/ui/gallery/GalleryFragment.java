package com.crowdstreaming.ui.gallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crowdstreaming.R;
import com.crowdstreaming.ui.videoplayer.VideoPlayerActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import wseemann.media.FFmpegMediaMetadataRetriever;


public class GalleryFragment extends Fragment {

    private RecyclerView recyclerView;
    private GalleryAdapter adapter;
    private ArrayList<GalleryListData> galleryListData;
    private ArrayList<File> videoFiles;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }


    @Override
    public void onResume() {
        super.onResume();

        String path = getActivity().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString();

        File directory = new File(path);
        videoFiles = new ArrayList<>(Arrays.asList(directory.listFiles()));

        galleryListData = new ArrayList<>(videoFiles.size());



        for (int i = 0; i < videoFiles.size(); i++) {

            galleryListData.add(new GalleryListData(videoFiles.get(i).getName(), null));
        }


        recyclerView = getActivity().findViewById(R.id.listVideos);
        adapter = new GalleryAdapter(galleryListData, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2 ,GridLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(adapter);


        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();

                int size = videoFiles.size();
                for (int i = 0; i < size; i++) {
                    try {
                        mmr.setDataSource(videoFiles.get(i).getPath());
                        Bitmap b = mmr.getFrameAtTime(0, FFmpegMediaMetadataRetriever.OPTION_CLOSEST);
                        galleryListData.get(i).setThumbail(rotateBitmap(b));

                        if (getActivity() != null)
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        else break;
                    }catch (Exception e){
                        galleryListData.remove(i);
                        videoFiles.remove(i);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                        size -= 1;
                        i -= 1;
                    }
                }

                mmr.release();
            }
        });
        t.start();
    }

    private Bitmap rotateBitmap(Bitmap bitmap){
        Matrix matrix = new Matrix();

        matrix.postRotate(90);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);

        return Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
    }


    public void startVideo(int position){
        Intent intent = new Intent(getContext(), VideoPlayerActivity.class);
        intent.putExtra("path",videoFiles.get(position).getAbsolutePath());
        getActivity().startActivity(intent);
    }
}
