package com.example.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

public class ClipRectView extends View {
    private Bitmap mBitmap;
    private Rect mRect;

    public ClipRectView(Context context) {
        super(context);
        init(null, 0);
    }

    public ClipRectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ClipRectView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        mBitmap = ((BitmapDrawable)getResources().getDrawable(R.drawable.bg)).getBitmap();
        mRect = new Rect();
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int w = canvas.getWidth();
        final int h = canvas.getHeight();

        int l = 0;
        canvas.save();
        for(int i=0; i<3; ++i) {
            mRect.set(l, 0, l+100, h);
            canvas.clipRect(mRect, Region.Op.REPLACE);
            canvas.drawBitmap(mBitmap, l, 0, null);
            l += 100;
        }
        canvas.restore();
        canvas.drawBitmap(mBitmap, l, 0, null);
    }
}
