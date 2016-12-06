package com.example.test;


import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnVideoSizeChangedListener {
    private static final String TAG = "MainActivity";

    private MediaPlayer mMediaPlayer;
    private SurfaceView mSurfaceView;

    private int mVideoWidth = 0;
    private int mVideoHeight = 0;
    private boolean mIsVideoReadyToPlay = false;
    private boolean mIsVideoSizeKnown = false;


    private SurfaceHolder.Callback mSurfaceCb = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.d(TAG, "surfaceCreated");
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.d(TAG, "surfaceChanged: w/h="+width+"/"+height);
            playVideo("/data/tmp/test.mp4");
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.d(TAG, "surfaceDestroyed");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mSurfaceView = (SurfaceView)findViewById(R.id.surfaceview);
//        mSurfaceView.setZOrderOnTop(true);
        mSurfaceView.getHolder().addCallback(mSurfaceCb);

//        mSurfaceView.postDelayed(new Runnable() {
//            public void run() {
//                convertActivityFromTranslucent(MainActivity.this);
//            }
//        }, 2000);
    }

    private static void convertActivityFromTranslucent(Activity activity) {
        try {
            Method method = Activity.class.getDeclaredMethod("convertFromTranslucent", new Class<?>[]{});
//            method.setAccessible(true);
            method.invoke(activity);
        } catch (Throwable ignored) {
            Log.e(TAG, "", ignored);
        }
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
        releaseMediaPlayer();
        doCleanup();
    }

    private void playVideo(String path) {
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(new FileInputStream(path).getFD());
            mMediaPlayer.setDisplay(mSurfaceView.getHolder());
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnVideoSizeChangedListener(this);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.prepare();
        } catch(Exception e) {
            Log.e(TAG, "", e);
        }
    }

    private void releaseMediaPlayer() {
        if(mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void doCleanup() {
        mVideoWidth = 0;
        mVideoHeight = 0;
        mIsVideoReadyToPlay = false;
        mIsVideoSizeKnown = false;
    }

    private void startVideoPlayback() {
            mMediaPlayer.start();
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        if(width == 0 || height == 0) {
            Log.d(TAG, "invalid w/h");
            return;
        }
        mIsVideoSizeKnown = true;
        mVideoWidth = width;
        mVideoHeight = height;
        Log.d(TAG, "onVideoSizeChanged: w/h="+mVideoWidth+"/"+mVideoHeight+", mIsVideoReadyToPlay="+mIsVideoReadyToPlay);
        if(mIsVideoReadyToPlay) {
            startVideoPlayback();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mIsVideoReadyToPlay = true;
        Log.d(TAG, "onPrepared: mIsVideoSizeKnown="+mIsVideoSizeKnown);
        if(mIsVideoSizeKnown) {
            startVideoPlayback();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
