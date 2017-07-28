package com.example.test;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    private PostEchoServer myServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myServer = new PostEchoServer(4724);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            myServer.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        } catch (IOException e) {
            Log.e(TAG, "", e);
        }
    }

    protected void onPause() {
        myServer.stop();
        super.onPause();
    }
}
