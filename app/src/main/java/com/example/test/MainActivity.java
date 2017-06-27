package com.example.test;


import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point3;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    private String COPY_DIR_PATH;
    private Handler H = new Handler();
    private TextView mInfo;
    private ImageView mSrcImg;
    private ImageView mDstImg;
    private Bitmap mSrcBmp;
    private Bitmap mDstBmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInfo = (TextView) findViewById(R.id.info);
        mSrcImg = (ImageView) findViewById(R.id.srcImg);
        mDstImg = (ImageView) findViewById(R.id.dstImg);

        COPY_DIR_PATH = getCacheDir() + "/assets/";
        Log.d(TAG, "OpenCV load " + OpenCVUtils.init());

        new Thread(new Runnable() {
            @Override
            public void run() {
                // copy from assets to cache dir
                File copyDir = new File(COPY_DIR_PATH);
                if (!copyDir.exists()) {
                    copyDir.mkdirs();
                }
                copyAssets(COPY_DIR_PATH);

                long beg = System.currentTimeMillis();
//                testCV();
                testFindTemplate();
                final long opTime = System.currentTimeMillis() - beg;

                // show images
                H.post(new Runnable() {
                    @Override
                    public void run() {
                        final String info = "Operation time: " + opTime + "ms";
                        Log.d(TAG, info);
                        mInfo.setText(info);
                        mSrcImg.setImageBitmap(mSrcBmp);
                        mDstImg.setImageBitmap(mDstBmp);
                    }
                });
            }
        }).start();
    }

    private void testCV() {
        final String srcImg = COPY_DIR_PATH + "screenshot.png";
        final String dstImg = COPY_DIR_PATH + "screenshot-canny.png";
        // read
        Mat image = Imgcodecs.imread(srcImg);
        Log.d(TAG, "img w/h=" + image.width() + "/" + image.height());

        // canny edge
        int threadshold1 = 70;
        int threadshold2 = 100;
        Mat im_canny = new Mat();

        Imgproc.Canny(image, im_canny, threadshold1, threadshold2);
        boolean writeOk = Imgcodecs.imwrite(dstImg, im_canny);
        Log.d(TAG, "canny op " + writeOk);

        mSrcBmp = BitmapFactory.decodeFile(srcImg);
        mDstBmp = BitmapFactory.decodeFile(dstImg);
    }

    private void testFindTemplate() {
        final String src = COPY_DIR_PATH + "screenshot.png";
        final String sch = COPY_DIR_PATH + "wifi_w_pwd.png";
        Mat srcImg = Imgcodecs.imread(src);
        Mat schImg = Imgcodecs.imread(sch);
        ArrayList<Point3> matches = OpenCVUtils.findAll(srcImg, schImg, 0.9, 10, true);
        Bitmap mark = OpenCVUtils.markMatchesToBitmap(srcImg, schImg, matches);

        mSrcBmp = BitmapFactory.decodeFile(src);
        mDstBmp = mark;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * copy assets, not including sub folders
     * @param outDir
     */
    private void copyAssets(String outDir) {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e(TAG, "Failed to get asset file list.", e);
        }
        if (files != null) for (String filename : files) {
            if(filename.equals("images") || filename.equals("sounds") || filename.equals("webkit")) {
                Log.d(TAG, "skipping "+filename);
                continue;
            }
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(filename);
                File outFile = new File(outDir, filename);
                out = new FileOutputStream(outFile);
                copyFile(in, out);
            } catch (IOException e) {
                Log.e(TAG, "Failed to copy asset file: " + filename, e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
            }
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
}
