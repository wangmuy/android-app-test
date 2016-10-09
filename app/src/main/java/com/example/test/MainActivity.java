package com.example.test;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ViewGroup mRootView;
    private View mClipRectView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRootView = (ViewGroup) findViewById(R.id.rootView);
        mClipRectView = findViewById(R.id.clipView);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mClipRectView.post(new Runnable() {
//            @Override
//            public void run() {
//                mClipRectView.invalidate();
//                mClipRectView.post(this);
//            }
//        });
    }
}
