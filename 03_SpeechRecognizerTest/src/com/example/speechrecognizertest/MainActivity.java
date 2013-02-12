package com.example.speechrecognizertest;

import java.util.ArrayList;

import com.example.speechrecognizertest.SpeechRecognizerWrapper.RecognizerFinishedCallback;
import com.example.speechrecognizertest.SpeechRecognizerWrapper.RecognizerState;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements RecognizerFinishedCallback,
        OnCheckedChangeListener {
    private static final String     TAG = "SpeechRecoginize Test";
    private ToggleButton            btnToggle;
    private SpeechRecognizerWrapper mSpeechRecognizerWrapper;
    private EditText                edittext;
    private TextView                txtStatus;
    private TextView                txtRms;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnToggle = (ToggleButton) findViewById(R.id.toggleButton1);
        edittext = (EditText) findViewById(R.id.editText1);
        txtStatus =(TextView)findViewById(R.id.textViewStatus);
        txtRms=(TextView)findViewById(R.id.textViewRms);

        btnToggle.setOnCheckedChangeListener(this);

        mSpeechRecognizerWrapper = new SpeechRecognizerWrapper(getApplicationContext());
        mSpeechRecognizerWrapper.addRecognizerFinishedCallback(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (mSpeechRecognizerWrapper != null) {
            if (isChecked) {
                mSpeechRecognizerWrapper.Start();
            } else {
                mSpeechRecognizerWrapper.Stop();
            }
        }
    }

    @Override
    public void onRecognizerFinished(ArrayList<String> results) {
        edittext.setText("Reseults : " + results.toString());
    }

    @Override
    public void onRecognizerStateChanged(RecognizerState state) {
        Log.d(TAG, state.toString());
        txtStatus.setText(state.toString());
        
        if (state == RecognizerState.Error) {
            Toast.makeText(this, state.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRmsChanged(float rmsdB) {
        txtRms.setText(Float.toString(rmsdB));
    }
}
