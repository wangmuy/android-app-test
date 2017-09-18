package com.example.test;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;

public class FloatingWindowService extends Service {
    private static final String TAG = "FloatingWindowService";

    private static final String ACTION_OPEN_ALERT_WINDOW = "com.example.test.action.OPEN_ALERT";
    private static final String ACTION_OPEN_OVERLAY_WINDOW = "com.example.test.action.OPEN_OVERLAY";

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive intent="+intent);
            onHandleIntent(intent);
        }
    };

    private Dialog mAlertDialog;
    private Dialog mOverlayDialog;

    public FloatingWindowService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        final IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_OPEN_ALERT_WINDOW);
        filter.addAction(ACTION_OPEN_OVERLAY_WINDOW);
        registerReceiver(mReceiver, filter);

        mAlertDialog = new AlertDialog.Builder(this)
                .setTitle("Alert Dialog")
                .setMessage("Annoying...")
                .create();
        mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        mOverlayDialog = new AlertDialog.Builder(this)
                .setTitle("Overlay Dialog")
                .setMessage("Annoying too...")
                .create();
        mOverlayDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand intent="+intent);
        onHandleIntent(intent);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void openAlertWindow(Context context) {
        Intent intent = new Intent(context, FloatingWindowService.class);
        intent.setAction(ACTION_OPEN_ALERT_WINDOW);
//        intent.putExtra(EXTRA_PARAM1, param1);
//        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    public static void openOverlayWindow(Context context) {
        Intent intent = new Intent(context, FloatingWindowService.class);
        intent.setAction(ACTION_OPEN_OVERLAY_WINDOW);
//        intent.putExtra(EXTRA_PARAM1, param1);
//        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_OPEN_ALERT_WINDOW.equals(action)) {
                handleOpenAlertWindow();
            } else if (ACTION_OPEN_OVERLAY_WINDOW.equals(action)) {
                handleOpenOverlayWindow();
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleOpenAlertWindow() {
        if(!mAlertDialog.isShowing()) {
            mAlertDialog.show();
        }
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleOpenOverlayWindow() {
        if(!mOverlayDialog.isShowing()) {
            Log.d(TAG, "handleOpenOverlayWindow show overlay");
            mOverlayDialog.show();
        }
    }
}
