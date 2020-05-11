package com.crowdstreaming.ui.watchstreaming;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.aware.WifiAwareSession;
import android.os.Bundle;
import android.os.Environment;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.crowdstreaming.R;
import com.crowdstreaming.net.Publisher;
import com.crowdstreaming.net.Subscriber;
import com.crowdstreaming.net.SubscriberSingleton;
import com.crowdstreaming.net.WifiAwareSessionUtillities;
import com.crowdstreaming.ui.avaliablesstreamings.AvaliablesStreamingsFragment;
import com.crowdstreaming.ui.streaming.StreamingView;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class WatchStreamingActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener, MediaPlayer.EventListener, IVLCVout.Callback, SubscriberObserver, StreamingView {

    private TextureView textureView;

    private LibVLC libvlc;
    private MediaPlayer mMediaPlayer = null;

    private String videoFilePath;
    private TextView finished;
    private StreamProxy streamProxy;

    private Subscriber subscriber;
    private Publisher publisher;

    private Socket clientSocket;
    private OutputStream socketOutput;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_streaming);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        videoFilePath = getIntent().getExtras().getString("path");

        preferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        subscriber = SubscriberSingleton.subscriber;
        subscriber.setObserver(this);

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
        this.getWindow().setStatusBarColor(Color.argb(255,0,0,0));
        this.getWindow().setNavigationBarColor( Color.argb(255,0,0,0));
    }



    private void viewStreaming(){
        System.out.println("viewing");

        ArrayList<String> options = new ArrayList<String>();
        options.add("--aout=opensles"); // time stretching
        options.add("-vvv"); // verbosity
        options.add("--avcodec-codec=h264");
        options.add("--file-logging");
        options.add("--logfile=vlc-log.txt");


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

        if(preferences.getBoolean("compartirVer", false)){
            WifiAwareSession session = WifiAwareSessionUtillities.getSession();
            publisher = new Publisher(WatchStreamingActivity.this, getConnectivityManager());
            session.publish(Publisher.CONFIGPUBL,publisher,null);
        }
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


    public ConnectivityManager getConnectivityManager() {
        return (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    public void startStreamingPublic() {
        clientSocket = null;

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    clientSocket = new Socket( publisher.getAddress() , publisher.getPort() );
                    socketOutput = clientSocket.getOutputStream();
                    subscriber.setNodoTransito(socketOutput);
                    publisher.callSubscriber();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
        t.start();
    }
}
