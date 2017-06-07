package com.example.test;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv = (TextView)findViewById(R.id.textview);
        tv.setText(NativeUtils.getCString());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String absPath = getCacheDir()+"/test.js";
                    Log.d(TAG, "writing to "+absPath);
                    FileWriter fw = new FileWriter(absPath);
                    fw.write("console.log('hello world!');\n");
                    fw.flush();
                    fw.close();
                    NativeUtils.startNode("node", absPath);
                } catch (IOException e) {
                    Log.e(TAG, "IOException:" ,e);
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
