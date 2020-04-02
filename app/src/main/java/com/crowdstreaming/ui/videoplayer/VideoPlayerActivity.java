package com.crowdstreaming.ui.videoplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.crowdstreaming.R;
import com.crowdstreaming.ui.watchstreaming.WatchStreamingActivity;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;

public class VideoPlayerActivity extends AppCompatActivity  implements TextureView.SurfaceTextureListener, MediaPlayer.EventListener, IVLCVout.Callback{

    private TextureView textureView;
    private LibVLC libvlc;
    private MediaPlayer mMediaPlayer = null;
    private String videoFilePath;
    private Button play, pause;
    private ProgressBar progressBar;
    private Thread progressThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        videoFilePath = getIntent().getExtras().getString("path");
        textureView = findViewById(R.id.videoplayer);
        textureView.setSurfaceTextureListener(this);

        progressBar = findViewById(R.id.videoProgress);

        play = findViewById(R.id.play);
        pause = findViewById(R.id.pause);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mMediaPlayer.isPlaying()) {
                    mMediaPlayer.play();
                }
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.pause();
            }
        });

        progressThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    final float progress = mMediaPlayer.getPosition() * 100;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress((int) progress);
                        }
                    });

                }
            }
        });

    }

    private void startPlayVideo(){
        System.out.println("startingg");

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
        mMediaPlayer.setEventListener(VideoPlayerActivity.this);

        // Set up video output
        final IVLCVout vout = mMediaPlayer.getVLCVout();
        vout.setVideoView(textureView);

        int mHeight = textureView.getHeight();
        int mWidth = textureView.getWidth();

        vout.setWindowSize(mWidth, mHeight);
        vout.addCallback(VideoPlayerActivity.this);
        vout.attachViews();

        System.out.println(videoFilePath);

        Media m = new Media(libvlc,videoFilePath);

        mMediaPlayer.setMedia(m);
        mMediaPlayer.play();

        textureView.setRotation(90);
        progressThread.start();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        startPlayVideo();
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
    public void onSurfacesCreated(IVLCVout vlcVout) {

    }

    @Override
    public void onSurfacesDestroyed(IVLCVout vlcVout) {

    }

    @Override
    public void onEvent(MediaPlayer.Event event) {

    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        mMediaPlayer.stop();
    }
}
