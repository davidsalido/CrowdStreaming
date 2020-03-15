package com.crowdstreaming.ui.streaming;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.crowdstreaming.R;
import com.google.android.material.snackbar.Snackbar;

public class StreamingActivity extends AppCompatActivity implements StreamingView{

    private StreamingPresenter presenter;
    private Button recButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streaming);

        presenter = new StreamingPresenter(this);
        initRecButton();

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
}
