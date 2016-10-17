package com.example.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 不同边框方式 影响的内部redraw次数
 */
public class BorderView extends View {
    private Paint mPaint;
    private Bitmap mBg;
    private Bitmap mBg2;

    private IBorderDrawer mBorderDrawer;

    public BorderView(Context context) {
        super(context);
        init(null, 0);
    }

    public BorderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public BorderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        mPaint = new Paint();
        mPaint.setColor(Color.GRAY);
        mBg = ((BitmapDrawable) getResources().getDrawable(R.drawable.bg)).getBitmap();
        mBg2 = ((BitmapDrawable)getResources().getDrawable(R.drawable.bg2)).getBitmap();

        setLayerType(View.LAYER_TYPE_HARDWARE, null);
//        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

//        mBorderDrawer = new ClipBorderDrawer();
        mBorderDrawer = new PaintStrokeBorderDrawer();
//        mBorderDrawer = new PathBorderDrawer();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int w = canvas.getWidth();
        final int h = canvas.getHeight();

        // 边框
        mBorderDrawer.onDraw(canvas);

        // 内部
        canvas.clipRect(0, 0, w, h, Region.Op.REPLACE);
        canvas.clipRect(50, 50, w-50, h-50, Region.Op.INTERSECT);
//        canvas.drawRect(0, 0, w, h, mPaint);
        canvas.drawBitmap(mBg2, 50, 50, null);
    }

    interface IBorderDrawer {
        public Paint getPaint();
        public void onDraw(Canvas canvas);
    }

    /** 边框 clip rect 方式
     * 1x redraw on LAYER_TYPE_SOFTWARE, 2x redraw on LAYER_TYPE_HARDWARE
     * */
    static class ClipBorderDrawer implements IBorderDrawer {
        private Paint paint;

        public ClipBorderDrawer() {
            paint = new Paint();
            paint.setColor(Color.YELLOW);
        }

        @Override
        public Paint getPaint() {
            return paint;
        }

        @Override
        public void onDraw(Canvas canvas) {
            final int w = canvas.getWidth();
            final int h = canvas.getHeight();

            canvas.clipRect(0,  0, w, h);
            canvas.clipRect(10, 10, w-10, h-10, Region.Op.DIFFERENCE);
            canvas.drawRect(0, 0, w, h, paint);
//            canvas.drawBitmap(mBg2, 0, 0, null);
        }
    }

    /** 边框 paint stroke 方式
     * 1x redraw on LAYER_TYPE_SOFTWARE, 2x redraw on LAYER_TYPE_HARDWARE
     * */
    static class PaintStrokeBorderDrawer implements IBorderDrawer {
        private Paint paint;

        public PaintStrokeBorderDrawer() {
            paint = new Paint();
            paint.setColor(Color.YELLOW);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(10.0f);
        }

        @Override
        public Paint getPaint() {
            return paint;
        }

        @Override
        public void onDraw(Canvas canvas) {
            final int w = canvas.getWidth();
            final int h = canvas.getHeight();

            canvas.drawRect(0, 0, w, h, paint);
//            canvas.drawBitmap(mBg2, 0, 0, null);
        }
    }

    /** 边框 path1 方式 */
    static class PathBorderDrawer implements IBorderDrawer {
        private Paint paint;
        private Path path1;
        private Path path2;

        public PathBorderDrawer() {
            paint = new Paint();
            paint.setColor(Color.YELLOW);
            path1 = new Path();
            path2 = new Path();
        }

        @Override
        public Paint getPaint() {
            return paint;
        }

        @Override
        public void onDraw(Canvas canvas) {
            final int w = canvas.getWidth();
            final int h = canvas.getHeight();

            path1.reset();

            // 4 rects. 1x redraw on LAYER_TYPE_SOFTWARE, 2x redraw on LAYER_TYPE_HARDWARE
//            path1.addRect(0, 0, 10, h-10, Path.Direction.CW);
//            path1.addRect(10, 0, w, 10, Path.Direction.CW);
//            path1.addRect(w-10, 10, w, h, Path.Direction.CW);
//            path1.addRect(0, h-10, w-10, h, Path.Direction.CW);

            // path1 op. 1x redraw on LAYER_TYPE_SOFTWARE, 2x redraw on LAYER_TYPE_HARDWARE
            path1.addRect(0, 0, w, h, Path.Direction.CW);
            path2.reset();
            path2.addRect(10, 10, w-10, h-10, Path.Direction.CW);
            path1.op(path2, Path.Op.DIFFERENCE);

            canvas.drawPath(path1, paint);
        }
    }
}
