package com.example.test;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final long DELAY_MILLIS = 0;

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView)findViewById(R.id.textview);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTextView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTextView.setText(NativeUtils.getCString());
            }
        }, DELAY_MILLIS);
    }
}
