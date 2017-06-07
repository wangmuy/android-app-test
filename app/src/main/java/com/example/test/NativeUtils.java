package com.example.test;

/**
 * Deprecated jni:
 * put android.useDeprecatedNdk=true in gradle.properties
 */
public class NativeUtils {
    static {
        System.loadLibrary("node");
        System.loadLibrary("NativeUtils");
    }

    public static native String getCString();

    public static native int startNode(String... args);
}
