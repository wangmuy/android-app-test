package com.example.test;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * onLayout延时
 * expect: 4.4 ui线程 蓝色部分长
 * actural: 无效果
 */
public class LayoutDelayView extends View {
    private static final String TAG = "LayoutDelayView";
    private static final int DELAY_MILLIS = 50;

    /*
    onLayout()调用时机
    View.java
      layout()
        (changed || (mPrivateFlags & PFLAG_LAYOUT_REQUIRED) == PFLAG_LAYOUT_REQUIRED)
     */

    public LayoutDelayView(Context context) {
        super(context);
        init();
    }

    public LayoutDelayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LayoutDelayView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public LayoutDelayView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.d(TAG, "onLayout");
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
            requestLayout();
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
