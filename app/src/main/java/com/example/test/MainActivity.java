package com.example.test;


import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    SimpleDraweeView mDraweeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDraweeView = (SimpleDraweeView)findViewById(R.id.drawee);
        Log.d(TAG, "MainActivity");
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDraweeView.setImageURI("asset:///p_01.png");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop, clearing caches");
        ImagePipeline ip = Fresco.getImagePipeline();
        ip.clearCaches();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown "+keyCode+", event="+event);
        return super.onKeyDown(keyCode, event);
    }
}
