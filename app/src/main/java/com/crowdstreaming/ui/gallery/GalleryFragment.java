package com.crowdstreaming.ui.gallery;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crowdstreaming.R;
import com.crowdstreaming.ui.videoplayer.VideoPlayerActivity;
import com.crowdstreaming.ui.watchstreaming.WatchStreamingActivity;

import java.io.File;

import wseemann.media.FFmpegMediaMetadataRetriever;


public class GalleryFragment extends Fragment {

    private RecyclerView recyclerView;
    private GalleryAdapter adapter;
    private GalleryListData[] galleryListData;
    private File[] videoFiles;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    private Bitmap rotateBitmap(Bitmap bitmap){
        Matrix matrix = new Matrix();

        matrix.postRotate(90);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);

        return Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
    }


    @Override
    public void onResume() {
        super.onResume();

        String path = getActivity().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString();

        File directory = new File(path);
        final File[] files = directory.listFiles();
        videoFiles = directory.listFiles();

        String [] names = new String[files.length];
        galleryListData = new GalleryListData[files.length];



        for (int i = 0; i < files.length; i++) {
            names[i] = files[i].getName();

            galleryListData[i] = new GalleryListData(names[i], null);
        }





        recyclerView = getActivity().findViewById(R.id.listVideos);
        adapter = new GalleryAdapter(galleryListData, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);



        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();

                for (int i = 0; i < files.length; i++) {
                    mmr.setDataSource(files[i].getPath());
                    Bitmap b = mmr.getFrameAtTime(0, FFmpegMediaMetadataRetriever.OPTION_CLOSEST);
                    galleryListData[i].setThumbail(rotateBitmap(b));

                    if(getActivity() != null)
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    else break;
                }

                mmr.release();
            }
        });
        t.start();
    }

    public void startVideo(int position){
        Intent intent = new Intent(getContext(), VideoPlayerActivity.class);
        intent.putExtra("path",videoFiles[position].getAbsolutePath());
        getActivity().startActivity(intent);
    }
}
