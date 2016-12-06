package com.example.test;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceView;

public class MySurfaceView extends SurfaceView {

    public MySurfaceView(Context context) {
        super(context);
        init();
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        ;
    }
}
