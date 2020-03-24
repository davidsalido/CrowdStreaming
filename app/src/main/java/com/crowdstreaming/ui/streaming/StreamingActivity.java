package com.crowdstreaming.ui.streaming;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.crowdstreaming.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class StreamingActivity extends AppCompatActivity implements StreamingView{

    private StreamingPresenter presenter;
    private Button recButton, btfile;

    private Camera mCamera;
    public MyCameraView mPreview;

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

        presenter = new StreamingPresenter(this);
        initRecButton();

        mCamera = getCameraInstance();
        mCamera.setDisplayOrientation(90);
        mPreview = new MyCameraView(this, mCamera);
        final FrameLayout preview =  findViewById(R.id.camera);
        preview.addView(mPreview);

        btfile = findViewById(R.id.btfile);

        btfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            startStreaming();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                t.start();
            }
        });

    }


    private void startStreaming() throws IOException {
        Socket clientSocket = null;
        OutputStream outs = null;

        ParcelFileDescriptor[] mParcelFileDescriptors = ParcelFileDescriptor.createReliablePipe();
        ParcelFileDescriptor mParcelRead = new ParcelFileDescriptor(mParcelFileDescriptors[0]);
        ParcelFileDescriptor mParcelWrite = new ParcelFileDescriptor(mParcelFileDescriptors[1]);

        clientSocket = new Socket( presenter.getPublisher().getAddress() , presenter.getPublisher().getPort() );
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

        int count;
        int totalSent = 0;
        while ((count = in.read(buffer))>0){
            totalSent += count;
            dos.write(buffer, 0, count);
        }
    }

    void rotateMatrix(byte mat[][], int N)
    {
        // Consider all squares one by one
        for (int x = 0; x < N / 2; x++)
        {
            // Consider elements in group of 4 in
            // current square
            for (int y = x; y < N-x-1; y++)
            {
                // store current cell in temp variable
                byte temp = mat[x][y];

                // move values from right to top
                mat[x][y] = mat[y][N-1-x];

                // move values from bottom to right
                mat[y][N-1-x] = mat[N-1-x][N-1-y];

                // move values from left to bottom
                mat[N-1-x][N-1-y] = mat[N-1-y][x];

                // assign temp to left
                mat[N-1-y][x] = temp;
            }
        }
    }

    private void initRecButton(){
        recButton = findViewById(R.id.rec);
        recButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.recButton();
            }
        });
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
