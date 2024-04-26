package com.example.test;


import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv = findViewById(R.id.textview);

        Button btn = findViewById(R.id.btn1);
        btn.setOnClickListener((v) -> {
            tv.setText(NativeUtils.getCString());
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
