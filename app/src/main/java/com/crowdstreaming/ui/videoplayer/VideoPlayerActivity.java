package com.crowdstreaming.ui.videoplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.crowdstreaming.R;

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
    private ImageView play;
    private SeekBar seekBar;
    private Thread progressThread;
    private Boolean progressing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        videoFilePath = getIntent().getExtras().getString("path");
        textureView = findViewById(R.id.videoplayer);
        textureView.setSurfaceTextureListener(this);

        seekBar = findViewById(R.id.seekBar);

        play = findViewById(R.id.play);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mMediaPlayer.isPlaying()) {
                    mMediaPlayer.play();
                    play.setImageResource(android.R.drawable.ic_media_pause);
                }
                else{
                    mMediaPlayer.pause();
                    play.setImageResource(android.R.drawable.ic_media_play);
                }
            }
        });



        progressThread = new Thread(new Runnable() {
            @Override
            public void run() {
                progressing = true;
                while(true){
                    if(progressing) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        final float progress = mMediaPlayer.getPosition() * 100;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                seekBar.setProgress((int) progress);
                            }
                        });
                    }
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            private float pos;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    pos = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                progressing = false;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMediaPlayer.setPosition(pos / 100);
                mMediaPlayer.play();
                progressing = true;
                play.setImageResource(android.R.drawable.ic_media_pause);
            }
        });

        this.getWindow().setStatusBarColor(Color.argb(255,0,0,0));
        this.getWindow().setNavigationBarColor( Color.argb(255,0,0,0));
    }

    private void startPlayVideo(){
        System.out.println("startingg");

        ArrayList<String> options = new ArrayList<String>();
        options.add("--aout=opensles"); // time stretching
        options.add("-vvv"); // verbosity
        options.add("--aout=opensles");
        options.add("--avcodec-codec=h264");
        options.add("--file-logging");
        options.add("--input-repeat=2");
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
