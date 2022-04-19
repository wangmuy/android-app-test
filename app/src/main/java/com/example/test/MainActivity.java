package com.example.test;


import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv = (TextView)findViewById(R.id.textview);
        String s2 = RustLib.hello("rust jni");
        tv.setText(s2);

        Button btn = findViewById(R.id.rustRocks);
        btn.setOnClickListener(v -> {
            btn.setEnabled(false);
            new Thread(() -> {
                File filesDir = MainActivity.this.getFilesDir();
                filesDir.mkdirs();
                String wasmFileName = "hello.wasm";
                File wasmFile = new File(filesDir, wasmFileName);
                if (!wasmFile.exists()) {
                    try(InputStream is = getAssets().open(wasmFileName);
                        OutputStream os = new FileOutputStream(wasmFile)) {
                        copyFile(is, os);
                    } catch (IOException e) {
                        Log.e(TAG, "", e);
                        tv.post(() -> tv.setText(e.getMessage()));
                    }
                }
                try {
                    long begMs = SystemClock.currentThreadTimeMillis();
                    int result = RustLib.callWasm(wasmFile.getAbsolutePath());
                    long costMs = SystemClock.currentThreadTimeMillis();
                    tv.post(() -> tv.setText("rustRocks result=" + result + ", costMs=" + costMs));
                } finally {
                    btn.post(() -> {
                        btn.setEnabled(true);
                    });
                }
            }).start();
        });
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
