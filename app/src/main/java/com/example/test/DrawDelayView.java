package com.example.test;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * onDraw延时
 * 效果: 蓝色部分长
 */
public class DrawDelayView extends View {
    private static final String TAG = "DrawDelayView";
    private static final int DELAY_MILLIS = 50;

    public DrawDelayView(Context context) {
        super(context);
        init();
    }

    public DrawDelayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawDelayView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public DrawDelayView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        hardWork();
    }

    private void hardWork() {
//        try {
//            Thread.sleep(DELAY_MILLIS);
//        } catch (InterruptedException e) {
//            Log.e(TAG, "", e);
//        }
        for(int i=0; i < 100000L; ++i);
    }

    private Handler mHandler = new Handler();
    private Runnable mRedrawRunnable = new Runnable() {
        @Override
        public void run() {
            invalidate();
            mHandler.post(this);
        }
    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mHandler.post(mRedrawRunnable);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacks(mRedrawRunnable);
    }
}
