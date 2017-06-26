package com.example.test;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private Handler H = new Handler();
    private TextView mInfo;
    private ImageView mSrcImg;
    private ImageView mDstImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInfo = (TextView) findViewById(R.id.info);
        mSrcImg = (ImageView) findViewById(R.id.srcImg);
        mDstImg = (ImageView) findViewById(R.id.dstImg);

        Log.d(TAG, "OpenCV load "+OpenCVUtils.init());

        new Thread(new Runnable() {
            @Override
            public void run() {
                final String assetImg = "screenshot.png";
                final String srcImg = getCacheDir()+"/screenshot.png";
                final String dstImg = getCacheDir()+"/screenshot-canny.png";

                // copy from assets to cache dir
                try {
                    InputStream is = getAssets().open(assetImg);
                    FileOutputStream fos = new FileOutputStream(new File(srcImg));
                    int len;
                    byte[] buffer = new byte[1024];
                    while((len=is.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                    is.close();
                } catch (IOException e) {
                    Log.e(TAG, "", e);
                }

                // read
                Mat image = Imgcodecs.imread(srcImg);
                Log.d(TAG, "img w/h="+image.width()+"/"+image.height());

                // canny edge
                int threadshold1 = 70;
                int threadshold2 = 100;
                Mat im_canny = new Mat();
                long beg = System.currentTimeMillis();
                Imgproc.Canny(image, im_canny, threadshold1, threadshold2);
                final long opTime = System.currentTimeMillis() - beg;
                boolean writeOk = Imgcodecs.imwrite(dstImg, im_canny);
                Log.d(TAG, "canny op "+writeOk);

                final Bitmap srcBmp = BitmapFactory.decodeFile(srcImg);
                final Bitmap dstBmp = BitmapFactory.decodeFile(dstImg);

                // show images
                H.post(new Runnable() {
                    @Override
                    public void run() {
                        mInfo.setText("Canny operation time: "+opTime);
                        mSrcImg.setImageBitmap(srcBmp);
                        mDstImg.setImageBitmap(dstBmp);
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
