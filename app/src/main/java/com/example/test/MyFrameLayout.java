package com.example.test;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

public class MyFrameLayout extends FrameLayout {
    private static final String TAG = "MyFrameLayout";
    private int myPaddingLeft;
    private int myPaddingTop;
    private int myPaddingRight;
    private int myPaddingBottom;

    public MyFrameLayout(Context context) {
        this(context, null, 0, 0);
    }

    public MyFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context c, AttributeSet attrs) {
        TypedArray a = c.getTheme().obtainStyledAttributes(attrs, R.styleable.MyFrameLayout, 0, 0);
        try {
            final int mypadding = a.getDimensionPixelSize(R.styleable.MyFrameLayout_mypadding, 0);
            if(mypadding != 0) {
                myPaddingLeft = myPaddingTop = myPaddingRight = myPaddingBottom = mypadding;
            }
            else {
                myPaddingLeft = a.getDimensionPixelSize(R.styleable.MyFrameLayout_mypaddingLeft, 0);
                myPaddingTop = a.getDimensionPixelSize(R.styleable.MyFrameLayout_mypaddingTop, 0);
                myPaddingRight = a.getDimensionPixelSize(R.styleable.MyFrameLayout_mypaddingRight, 0);
                myPaddingBottom = a.getDimensionPixelSize(R.styleable.MyFrameLayout_mypaddingBottom, 0);
            }
//            Log.d(TAG, "mypadding: "+myPaddingLeft+"/"+myPaddingTop+"/"+myPaddingRight+"/"+myPaddingBottom);
        } finally {
            a.recycle();
        }
        if(getBackground() != null)
            setBackgroundDrawable(getBackground());
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        super.setBackgroundDrawable(background);
        final int origPaddingLeft = getPaddingLeft();
        final int origPaddingTop = getPaddingTop();
        final int origPaddingRight = getPaddingRight();
        final int origPaddingBottom = getPaddingBottom();
//        Log.d(TAG, "setBackgroundDrawable orig padding="+origPaddingLeft+"/"+origPaddingTop+"/"+origPaddingRight+"/"+origPaddingBottom);
        final int paddingLeft = origPaddingLeft+myPaddingLeft;
        final int paddingTop = origPaddingTop+myPaddingTop;
        final int paddingRight = origPaddingRight+myPaddingRight;
        final int paddingBottom = origPaddingBottom+myPaddingBottom;
//        Log.d(TAG, "setBackgroundDrawable now padding="+paddingLeft+"/"+paddingTop+"/"+paddingRight+"/"+paddingBottom);
        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }

}
