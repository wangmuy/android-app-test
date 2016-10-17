package com.example.test;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * onMeasure延时
 * expect: 4.4 ui线程 蓝色部分长
 * actural: 无效果
 */
public class MeasureDelayView extends View {
    private static final String TAG = "MeasureDelayView";
    private static final int DELAY_MILLIS = 50;

    /*
    onMeasure()调用时机
    View.java
      layout()
        mPrivateFlags3 & PFLAG3_MEASURE_NEEDED_BEFORE_LAYOUT == true
      measure()
        ((mPrivateFlags & PFLAG_FORCE_LAYOUT) == PFLAG_FORCE_LAYOUT ||
            widthMeasureSpec != mOldWidthMeasureSpec ||
            heightMeasureSpec != mOldHeightMeasureSpec) 且
        (cacheIndex < 0 || sIgnoreMeasureCache)
          其中 sIgnoreMeasureCache = (targetSdkVersion < KITKAT)
          其中 cacheIndex = (mPrivateFlags & PFLAG_FORCE_LAYOUT) == PFLAG_FORCE_LAYOUT ? -1 : mMeasureCache.indexOfKey(key)
            其中 key = (高32位==widthMeasureSpec | 低32位heightMeasureSpec)
     */

    public MeasureDelayView(Context context) {
        super(context);
        init();
    }

    public MeasureDelayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MeasureDelayView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public MeasureDelayView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "onMeasure");
        try {
            Log.d(TAG, "onMeasure");
            Thread.sleep(DELAY_MILLIS);
        } catch (InterruptedException e) {
            Log.e(TAG, "", e);
        }
    }

    private Handler mHandler = new Handler();
    private Runnable mRedrawRunnable = new Runnable() {
        @Override
        public void run() {
            requestLayout(); // 置 PFLAG_FORCE_LAYOUT
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
