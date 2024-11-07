package com.example.test.util;

public class SystemPropertiesUtil {

    private static final Class<?> SP = getSystemPropertiesClass();

    public static String get(String key) {
        try {
            return (String) SP.getMethod("get", String.class).invoke((Object) null, key);
        } catch (Exception var2) {
            return null;
        }
    }

    public static String get(String key, String def) {
        try {
            return (String) SP.getMethod("get", String.class, String.class).invoke((Object) null, key, def);
        } catch (Exception var3) {
            return def;
        }
    }

    public static boolean getBoolean(String key, boolean def) {
        try {
            return (Boolean) SP.getMethod("getBoolean", String.class, Boolean.TYPE).invoke((Object) null, key, def);
        } catch (Exception var3) {
            return def;
        }
    }

    public static int getInt(String key, int def) {
        try {
            return (Integer) SP.getMethod("getInt", String.class, Integer.TYPE).invoke((Object) null, key, def);
        } catch (Exception var3) {
            return def;
        }
    }

    public static long getLong(String key, long def) {
        try {
            return (Long) SP.getMethod("getLong", String.class, Long.TYPE).invoke((Object) null, key, def);
        } catch (Exception var4) {
            return def;
        }
    }

    public static void set(String key, String val) {
        try {
            SP.getMethod("set", String.class, String.class).invoke((Object) null, key, val);
        } catch (Exception var3) {
        }
    }

    private static Class<?> getSystemPropertiesClass() {
        try {
            return Class.forName("android.os.SystemProperties");
        } catch (ClassNotFoundException var1) {
            return null;
        }
    }

    private SystemPropertiesUtil() {
        throw new AssertionError();
    }

}
