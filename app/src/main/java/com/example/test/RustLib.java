package com.example.test;

public class RustLib {
    static {
        System.loadLibrary("rustrocks");
    }

    public static native String hello(String input);
    public static native int callWasm(String absFilePath);
}
