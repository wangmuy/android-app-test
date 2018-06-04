package com.example.test;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String ACTION_MY_USB = "com.example.test.USB_PERMISSION";
    private TextView mTextView;
    private UsbManager mUsbManager;
    private UsbReceiver mUsbReceiver;
    private PendingIntent mPermissionIntent;
    private IntentFilter mFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.text);

        mUsbManager = (UsbManager)getSystemService(Context.USB_SERVICE);
        mUsbReceiver = new UsbReceiver(this, mTextView);
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_MY_USB), 0);
        mFilter = new IntentFilter(ACTION_MY_USB);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mUsbReceiver, mFilter);
        UsbDevice mTarget = null;
        for(UsbDevice device: mUsbManager.getDeviceList().values()) {
            Log.d(TAG, "device vendorId=" + device.getVendorId());
            Log.d(TAG, "device=" + device);
            if(device.getVendorId() == 2385) {
                mTarget = device;
            }
        }
        if(mTarget != null) {
            mUsbManager.requestPermission(mTarget, mPermissionIntent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mUsbReceiver);
    }

    static class UsbReceiver extends BroadcastReceiver {
        private WeakReference<Activity> mActivityRef;
        private WeakReference<TextView> mTextView;

        public UsbReceiver(Activity h, TextView tv) {
            mActivityRef = new WeakReference<>(h);
            mTextView = new WeakReference<>(tv);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(ACTION_MY_USB.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    boolean result = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
                    final String log = "request permission:" + Boolean.toString(result) + ", device=" + device;
                    final Activity a = mActivityRef.get();
                    if(a != null) {
                        final TextView tv = mTextView.get();
                        a.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv.setText(log);
                            }
                        });
                    }
                    Log.d(TAG, log);
                }
            }
        }
    }
}
