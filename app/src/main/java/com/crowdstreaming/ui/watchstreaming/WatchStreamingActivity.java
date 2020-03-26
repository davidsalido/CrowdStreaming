package com.crowdstreaming.ui.watchstreaming;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.crowdstreaming.R;
import com.crowdstreaming.net.Subscriber;
import com.crowdstreaming.net.SubscriberSingleton;
import com.crowdstreaming.ui.avaliablesstreamings.AvaliablesStreamingsFragment;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;

public class WatchStreamingActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener, MediaPlayer.EventListener, IVLCVout.Callback, SubscriberObserver {

    private TextureView textureView;

    private LibVLC libvlc;
    private MediaPlayer mMediaPlayer = null;

    private String videoFilePath;
    private TextView finished;
    private StreamProxy streamProxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_streaming);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        videoFilePath = getIntent().getExtras().getString("path");
        SubscriberSingleton.subscriber.setObserver(this);
        finished = findViewById(R.id.finished);

        textureView = findViewById(R.id.texture);
        textureView.setSurfaceTextureListener(this);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                streamProxy = new StreamProxy();
                streamProxy.start();

            }
        });

        t.start();

    }



    private void viewStreaming(){
        System.out.println("viewing");

        ArrayList<String> options = new ArrayList<String>();
        options.add("--aout=opensles"); // time stretching
        options.add("-vvv"); // verbosity
        options.add("--aout=opensles");
        options.add("--avcodec-codec=h264");
        options.add("--file-logging");
        options.add("--logfile=vlc-log.txt");
        options.add("--video-filter=rotate {angle=90}");


        libvlc = new LibVLC(getApplicationContext(), options);

        // Create media player
        mMediaPlayer = new MediaPlayer(libvlc);
        mMediaPlayer.setEventListener(WatchStreamingActivity.this);

        // Set up video output
        final IVLCVout vout = mMediaPlayer.getVLCVout();
        vout.setVideoView(textureView);

        int mHeight = textureView.getHeight();
        int mWidth = textureView.getWidth();

        vout.setWindowSize(mWidth, mHeight);
        vout.addCallback(WatchStreamingActivity.this);
        vout.attachViews();

        System.out.println(videoFilePath);

        Media m = new Media(libvlc, Uri.parse("http://127.0.0.1:8888/" + videoFilePath));

        mMediaPlayer.setMedia(m);
        mMediaPlayer.play();

        textureView.setRotation(90);
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

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        viewStreaming();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void stopStreaming() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SubscriberSingleton.subscriber.setObserver(null);
                SubscriberSingleton.subscriber.closeSocket();
                textureView.setVisibility(View.INVISIBLE);
                streamProxy.stop();
                finished.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        streamProxy.stop();
    }
}
