package com.crowdstreaming.ui.streaming;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.wifi.aware.WifiAwareSession;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.crowdstreaming.R;
import com.crowdstreaming.net.Publisher;
import com.crowdstreaming.net.WifiAwareSessionUtillities;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

public class StreamingActivity extends AppCompatActivity implements StreamingView{

    private FloatingActionButton recButton;
    private Publisher publisher;
    private Camera mCamera;
    public MyCameraView mPreview;
    private boolean streaming = false;
    private Thread streamingThread;
    private Socket clientSocket;

    public static Camera getCameraInstance()
    {
        Camera c=null;
        try{
            c=Camera.open();
        }catch(Exception e){
            e.printStackTrace();
        }
        return c;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streaming);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initRecButton();

        mCamera = getCameraInstance();
        mCamera.setDisplayOrientation(90);
        mPreview = new MyCameraView(this, mCamera);
        final FrameLayout preview =  findViewById(R.id.camera);
        preview.addView(mPreview);

        this.getWindow().setStatusBarColor(Color.argb(255,0,0,0));
        this.getWindow().setNavigationBarColor( Color.argb(255,0,0,0));
    }

    public void startStreamingPublic(){
        streamingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    startStreaming();
                } catch (IOException e) {
                    System.out.println("Subscriber desconectado");
                } catch (InterruptedException e) {

                }
                finally {
                    try {
                        streaming = false;
                        if(clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        streamingThread.start();
    }


    private void startStreaming() throws IOException, InterruptedException, SocketException {
        clientSocket = null;
        OutputStream outs = null;

        ParcelFileDescriptor[] mParcelFileDescriptors = ParcelFileDescriptor.createReliablePipe();
        ParcelFileDescriptor mParcelRead = new ParcelFileDescriptor(mParcelFileDescriptors[0]);
        ParcelFileDescriptor mParcelWrite = new ParcelFileDescriptor(mParcelFileDescriptors[1]);

        clientSocket = new Socket( publisher.getAddress() , publisher.getPort() );
        outs = clientSocket.getOutputStream();
        DataOutputStream dos = new DataOutputStream(outs);



        int video_width = 1920;
        int video_height = 1080;

        List<Camera.Size> list =  mCamera.getParameters().getSupportedVideoSizes();


        for(Camera.Size c:list){
            System.out.println(c.width + " " + c.height);
        }

        // initialize recording hardware

        MediaRecorder mMediaRecorder = new MediaRecorder();

        mCamera.getParameters().setRotation(90);
        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();


        mMediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);


        // set TS
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_2_TS);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        mMediaRecorder.setVideoSize(video_width, video_height);
        mMediaRecorder.setVideoFrameRate(30);

        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

        mMediaRecorder.setOutputFile(mParcelWrite.getFileDescriptor());
        mMediaRecorder.prepare();
        mMediaRecorder.start();

        InputStream in = new ParcelFileDescriptor.AutoCloseInputStream(mParcelRead);

        byte[] buffer = new byte[4096];

        publisher.callSubscriber();

        int count;
        int totalSent = 0;
        while ((count = in.read(buffer))>0 && !Thread.interrupted()){
            totalSent += count;
            dos.write(buffer, 0, count);
            System.out.println("escribiendo");
        }
    }

    private void stopStreaming(){
        streamingThread.interrupt();
    }

    private void initRecButton(){
        recButton = findViewById(R.id.rec);
        recButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!streaming){
                    WifiAwareSession session = WifiAwareSessionUtillities.getSession();

                    publisher = new Publisher(StreamingActivity.this, getConnectivityManager());
                    session.publish(Publisher.CONFIGPUBL,publisher,null);

                    streaming = true;

                }
                else{
                    stopStreaming();
                    streaming = false;
                }

            }
        });
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        if(streaming) stopStreaming();
    }

    @Override
    public void showMessage(String msg) {
        Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    @Override
    public ConnectivityManager getConnectivityManager() {
        return (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
    }
}
