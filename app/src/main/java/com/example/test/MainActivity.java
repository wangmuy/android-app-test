package com.example.test;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onClickMeasureDelay(View v) {
        startActivity(new Intent(this, MeasureDelayActivity.class));
    }

    public void onClickLayoutDelay(View v) {
        startActivity(new Intent(this, LayoutDelayActivity.class));
    }

    public void onClickDrawDelay(View v) {
        startActivity(new Intent(this, DrawDelayActivity.class));
    }
}
