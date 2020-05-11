package com.crowdstreaming.ui.streaming;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.FrameLayout;

import com.crowdstreaming.R;
import com.crowdstreaming.net.Publisher;
import com.crowdstreaming.net.WifiAwareSessionUtillities;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class StreamingActivity extends AppCompatActivity implements StreamingView{

    private FloatingActionButton recButton;
    private Publisher publisher;
    private Camera mCamera;
    private MyCameraView mPreview;
    private MediaRecorder mMediaRecorder;
    private boolean streaming = false;
    private Thread streamingThread, cameraThread;
    private Socket  clientSocket;
    private SharedPreferences preferences;
    private volatile List<Pair> socketOutputs;

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

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        mCamera = getCameraInstance();
        mCamera.setDisplayOrientation(90);
        mPreview = new MyCameraView(this, mCamera);
        final FrameLayout preview =  findViewById(R.id.camera);
        preview.addView(mPreview);

        socketOutputs = new ArrayList<>();

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
            }
            }
        });
        streamingThread.start();
    }



    private void startStreaming() throws IOException {
        clientSocket = null;
        OutputStream outs = null;

        clientSocket = new Socket( publisher.getAddress() , publisher.getPort() );
        outs = clientSocket.getOutputStream();
        DataOutputStream outputStream = new DataOutputStream(outs);
        socketOutputs.add(new Pair(outputStream,false));

        publisher.callSubscriber();


    }


    private void startRecording() throws IOException {

        ParcelFileDescriptor[] mParcelFileDescriptors = ParcelFileDescriptor.createReliablePipe();
        ParcelFileDescriptor mParcelRead = new ParcelFileDescriptor(mParcelFileDescriptors[0]);
        ParcelFileDescriptor mParcelWrite = new ParcelFileDescriptor(mParcelFileDescriptors[1]);


        int video_width = 1920;
        int video_height = 1080;

        List<Camera.Size> list =  mCamera.getParameters().getSupportedVideoSizes();


        for(Camera.Size c:list){
            System.out.println(c.width + " " + c.height);
        }

        // initialize recording hardware

        mMediaRecorder = new MediaRecorder();

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

        //Metadatos de los videos guardados en MPEG_2
        byte[] metadata = hexStringToByteArray("474000110000B00D0000C300000001E1E0E85F74ECFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");

        FileOutputStream fos = null;
        System.out.println("Ajustes: " + preferences.getBoolean("guardar", false));
        if(preferences.getBoolean("guardar", false)){
            String path = createVideoFilePath();
            File file = new File(path);
            fos = new FileOutputStream(file);
        }

        int count;
        int totalRead = 0;
        while ((count = in.read(buffer))>0 && !Thread.interrupted()){
            if(!socketOutputs.isEmpty()){
                for(Pair pair:socketOutputs) {
                    if (!pair.metadataSent) {
                        System.out.println("Metadata mandados");
                        pair.socketOutput.write(metadata, 0, metadata.length);
                        pair.metadataSent = true;
                    }
                    else{
                        pair.socketOutput.write(buffer, 0, count);
                        //System.out.println("Enviando a " + pair.socketOutput);
                    }

                }
            }
            if(fos != null) fos.write(buffer, 0, count);
            if(totalRead == 0){
                metadata = Arrays.copyOf(buffer,4096);
            }
            totalRead++;
        }
    }

    private String createVideoFilePath(){
        String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        String filename = sdf.format(cal.getTime());
        filename = filename.replaceAll(" ", "_");
        filename = filename.replaceAll(":", "-");
        return getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + filename + ".mp4";
    }

    private void stopStreaming(){
        cameraThread.interrupt();
        streaming = false;
        mMediaRecorder.release();
        WifiAwareSessionUtillities.restart();
        try {
            if(clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initRecButton(){
        recButton = findViewById(R.id.rec);
        recButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!streaming){
                    streaming = true;
                    recButton.setImageResource(R.drawable.square);
                    cameraThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                        try {
                            WifiAwareSession session = WifiAwareSessionUtillities.getSession();
                            publisher = new Publisher(StreamingActivity.this, getConnectivityManager());
                            session.publish(Publisher.CONFIGPUBL,publisher,null);

                            startRecording();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        }
                    });
                    cameraThread.start();
                }
                else{
                    stopStreaming();
                    streaming = false;
                    recButton.setImageResource(R.drawable.videocam);
                }

            }
        });
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        if(streaming) stopStreaming();
    }


    public ConnectivityManager getConnectivityManager() {
        return (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    private class Pair{

        private DataOutputStream socketOutput;
        private Boolean metadataSent;

        public Pair(DataOutputStream socketOutput, Boolean metadataSent){
            this.socketOutput = socketOutput;
            this.metadataSent = metadataSent;
        }


        public DataOutputStream getSocketOutput() {
            return socketOutput;
        }

        public void setSocketOutput(DataOutputStream socketOutput) {
            this.socketOutput = socketOutput;
        }

        public Boolean getMetadataSent() {
            return metadataSent;
        }

        public void setMetadataSent(Boolean metadataSent) {
            this.metadataSent = metadataSent;
        }
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}
