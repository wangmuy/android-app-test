package com.example.test;

import android.util.Log;

import org.opencv.android.OpenCVLoader;

/**
 * Created by wangzhongdi on 2017/6/26.
 */

public class OpenCVUtils {
    private static final String TAG = "OpenCVUtils";
    private OpenCVUtils(){}

    static {
        System.loadLibrary("opencv_java3");
    }

    public static boolean init() {
        return OpenCVLoader.initDebug();
    }
}
