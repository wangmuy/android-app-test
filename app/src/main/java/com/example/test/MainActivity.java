package com.example.test;


import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv = (TextView)findViewById(R.id.textview);
        String s2 = RustLib.hello("rust jni");
        tv.setText(s2);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
