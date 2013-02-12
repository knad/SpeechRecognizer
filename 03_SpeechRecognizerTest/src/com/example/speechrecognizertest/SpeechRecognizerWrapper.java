package com.example.speechrecognizertest;

import java.util.ArrayList;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Toast;

public class SpeechRecognizerWrapper implements RecognitionListener {
    private static boolean             DEBUG = true;
    private static final String        TAG   = "SpeechRecognizerWrapper";

    private Context                    mAppContext;
    private SpeechRecognizer           mSpeechRecognizer;
    private boolean                    mRecognitionReady;
    private boolean                    mIsListening;

    private RecognizerFinishedCallback mCallback;
    private RecognizerState            mRecognitonState;

    public enum RecognizerState {
        onReadyForSpeech,
        onEndOfSpeech,
        onBeginingOfSpeech,
        Stop,
        Error,
    }

    public SpeechRecognizerWrapper(Context mAppContext) {
        super();
        this.mAppContext = mAppContext;
        mRecognitionReady = true;
        mIsListening = false;

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(mAppContext
                .getApplicationContext());
        mSpeechRecognizer.setRecognitionListener(this);
    }

    public void Start() {
        mIsListening = true;
        try {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, mAppContext.getPackageName());
            intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

            if (mRecognitionReady) {
                mSpeechRecognizer.startListening(intent);
            }
        } catch (ActivityNotFoundException e) {
            Toast.makeText(mAppContext, "ActivityNotFoundException", Toast.LENGTH_SHORT).show();
            mIsListening = false;
        }
    }

    public void Stop() {
        mIsListening = false;
        mRecognitonState = RecognizerState.Stop;
        mSpeechRecognizer.stopListening();
        mCallback.onRecognizerStateChanged(mRecognitonState);
    }

    public void addRecognizerFinishedCallback(RecognizerFinishedCallback callback) {
        mCallback = callback;
    }

    public void onBeginningOfSpeech() {
        if (DEBUG) Log.w(TAG, "onBeginningOfSpeech()");

        mRecognitonState = RecognizerState.onBeginingOfSpeech;
        mCallback.onRecognizerStateChanged(mRecognitonState);
        mRecognitionReady = false;
    }

    public void onBufferReceived(byte[] buffer) {
        // Log.d(TAG,"onBufferReceived()");
    }

    public void onEndOfSpeech() {
        if (DEBUG) Log.w(TAG, "onEndOfSpeech()");

        mRecognitonState = RecognizerState.onEndOfSpeech;
        mCallback.onRecognizerStateChanged(mRecognitonState);
        mRecognitionReady = true;
    }

    public void onError(int error) {
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                Log.e(TAG, "ERROR_AUDIO");
                break;

            case SpeechRecognizer.ERROR_CLIENT:
                Log.e(TAG, "ERROR_CLIENT");
                mRecognitionReady = true;
                break;

            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                Log.e(TAG, "ERROR_INSUFFICIENT_PERMISSIONS");
                break;

            case SpeechRecognizer.ERROR_NETWORK:
                Log.e(TAG, "ERROR_NETWORK");
                mRecognitonState = RecognizerState.Error;
                mCallback.onRecognizerStateChanged(mRecognitonState);
                mRecognitionReady = false;
                break;

            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                Log.e(TAG, "ERROR_NETWORK_TIMEOUT");
                mRecognitionReady = true;
                break;

            case SpeechRecognizer.ERROR_NO_MATCH:
                if (DEBUG) Log.d(TAG, "ERROR_NO_MATCH");

                mRecognitionReady = true;
                break;

            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                if (DEBUG) Log.e(TAG, "ERROR_RECOGNIZER_BUSY");

                mRecognitionReady = false;
                break;

            case SpeechRecognizer.ERROR_SERVER:
                Log.e(TAG, "ERROR_SERVER");
                mRecognitionReady = true;
                break;

            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                if (DEBUG) Log.d(TAG, "ERROR_SPEECH_TIMEOUT");

                mRecognitionReady = true;
                break;

            default:
                break;
        }

        if (mIsListening) Start();
    }

    public void onEvent(int eventType, Bundle params) {
        if (DEBUG) Log.d(TAG, "onEvent()");
    }

    public void onPartialResults(Bundle partialResults) {
        Log.d(TAG, "onPartialResults()");

        ReceiveResults(partialResults);
    }

    public void onReadyForSpeech(Bundle params) {
        if (DEBUG) Log.w(TAG, "onReadyForSpeech()");
        mRecognitonState = RecognizerState.onReadyForSpeech;
        mCallback.onRecognizerStateChanged(mRecognitonState);

        mRecognitionReady = false;
    }

    public void onResults(Bundle results) {
        if (DEBUG) Log.d(TAG, "onResults()");

        mRecognitionReady = true;

        ReceiveResults(results);

        if (mIsListening) Start();
    }

    private void ReceiveResults(Bundle results) {
        ArrayList<String> recData = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        // float[] scores = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);

        if (DEBUG) {
            String data = "";
            for (int i = 0; i < recData.size(); i++) {
                data += recData.get(i) + " , ";
            }
            Toast.makeText(mAppContext, data, Toast.LENGTH_LONG).show();
            Log.d(TAG, data);
        }
        mCallback.onRecognizerFinished(recData);
    }

    public void onRmsChanged(float rmsdB) {
        // if(DEBUG) Log.d(TAG,"onRmsChanged(): " +rmsdB);
        mCallback.onRmsChanged(rmsdB);
    }

    public RecognizerState getRecognitonState() {
        return mRecognitonState;
    }

    public interface RecognizerFinishedCallback {
        public void onRecognizerFinished(ArrayList<String> results);

        public void onRecognizerStateChanged(RecognizerState state);

        public void onRmsChanged(float rmsdB);
    }
}
