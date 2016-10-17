package com.example.test;

import android.app.Activity;
import android.os.Bundle;

public class DrawDelayActivity extends Activity {
    private static final String TAG = "LayoutDelayActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.draw_delay);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
