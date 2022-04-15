package com.example.test;

public class RustLib {
    static {
        System.loadLibrary("rustlib");
    }

    public static native String hello(String input);
}
