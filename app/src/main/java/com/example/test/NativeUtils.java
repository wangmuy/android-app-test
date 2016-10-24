package com.example.test;

public class NativeUtils {
    static {
        System.loadLibrary("NativeUtils");
    }

    public static native String getCString();
}
