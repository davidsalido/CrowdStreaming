package com.crowdstreaming.ui.watchstreaming;

import androidx.appcompat.app.AppCompatActivity;

import android.net.LocalSocket;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.crowdstreaming.R;
import com.crowdstreaming.net.Subscriber;
import com.crowdstreaming.ui.avaliablesstreamings.AvaliablesStreamingsPresenter;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class WatchStreamingActivity extends AppCompatActivity implements SurfaceHolder.Callback, MediaPlayer.EventListener, IVLCVout.Callback {

    private Subscriber subscriber;
    private SurfaceView surfaceView;
    private Button rep;

    private LibVLC libvlc;
    private MediaPlayer mMediaPlayer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_streaming);

        subscriber = AvaliablesStreamingsPresenter.subscriber;


        surfaceView = findViewById(R.id.surface);
        final SurfaceHolder holder = surfaceView.getHolder();
        holder.addCallback(this);


        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                StreamProxy streamProxy = new StreamProxy();
                streamProxy.start();

            }
        });

        t.start();

        rep = findViewById(R.id.rep);

        rep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<String> options = new ArrayList<String>();
                options.add("--aout=opensles"); // time stretching
                options.add("-vvv"); // verbosity
                options.add("--aout=opensles");
                options.add("--avcodec-codec=h264");
                options.add("--file-logging");
                options.add("--logfile=vlc-log.txt");
                //options.add("--video-filter=rotate {angle=90}");


                libvlc = new LibVLC(getApplicationContext(), options);
                holder.setKeepScreenOn(true);

                // Create media player
                mMediaPlayer = new MediaPlayer(libvlc);
                mMediaPlayer.setEventListener(WatchStreamingActivity.this);

                // Set up video output
                final IVLCVout vout = mMediaPlayer.getVLCVout();
                vout.setVideoView(surfaceView);

                int mHeight = surfaceView.getHeight();
                int mWidth = surfaceView.getWidth();

                vout.setWindowSize(mWidth, mHeight);
                vout.addCallback(WatchStreamingActivity.this);
                vout.attachViews();



                Media m = new Media(libvlc, Uri.parse("http://127.0.0.1:8888/" + getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/salida.mp4"));

                mMediaPlayer.setMedia(m);
                mMediaPlayer.play();
            }
        });
    }



    @Override
    public void surfaceCreated(SurfaceHolder holder) {


    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onEvent(MediaPlayer.Event event) {

    }

    @Override
    public void onSurfacesCreated(IVLCVout vlcVout) {

    }

    @Override
    public void onSurfacesDestroyed(IVLCVout vlcVout) {

    }
}
