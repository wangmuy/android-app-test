package com.example.test;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.quickjs.JSArray;
import com.quickjs.JSContext;
import com.quickjs.JSFunction;
import com.quickjs.JSObject;
import com.quickjs.JavaCallback;
import com.quickjs.JavaVoidCallback;
import com.quickjs.QuickJS;
import com.quickjs.plugin.ConsolePlugin;
import com.quickjs.plugin.SetTimeoutPlugin;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity___";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv = findViewById(R.id.tv);

        Button btn1 = findViewById(R.id.btn1);
        btn1.setText("testSimple");
        btn1.setOnClickListener(v -> {
            new Thread(() -> {
                try {
                    testSimple(tv);
                } catch (Exception e) {
                    Log.e(TAG, "", e);
                }
            }).start();
        });

        Button btn2 = findViewById(R.id.btn2);
        btn2.setText("testJSPOD");
        btn2.setOnClickListener(v -> {
            new Thread(() -> {
                try {
                    testJSPOD(tv);
                } catch (Exception e) {
                    Log.e(TAG, "", e);
                }
            }).start();
        });

        Button btn3 = findViewById(R.id.btn3);
        btn3.setText("testJSFunction");
        btn3.setOnClickListener(v -> {
            new Thread(() -> {
                try {
                    testJSFunction(tv);
                } catch (Exception e) {
                    Log.e(TAG, "", e);
                }
            }).start();
        });

        Button btn4 = findViewById(R.id.btn4);
        btn4.setText("testPlugin");
        btn4.setOnClickListener(v -> {
            new Thread(() -> {
                try {
                    testPlugin(tv);
                } catch (Exception e) {
                    Log.e(TAG, "", e);
                }
            }).start();
        });
    }

    private void testSimple(TextView tv) {
        try (QuickJS quickJS = QuickJS.createRuntime()) {
            try (JSContext jsContext = quickJS.createContext()) {
                int result = jsContext.executeIntegerScript("var a = 2+10;\n a;", "file.js");
                String msg = "testSimple result=" + result;
                Log.d(TAG, msg);
                tv.post(() -> {
                    tv.setText(msg);
                });
            }
        }
    }

    private void testJSPOD(TextView tv) {
        try (QuickJS quickJS = QuickJS.createRuntime()) {
            try (JSContext jsContext = quickJS.createContext()) {
                String result = "";

                // JSObject
                JSObject user = new JSObject(jsContext).set("name", "Wiki").set("age", 18).set("time", System.currentTimeMillis());
                String name = String.valueOf(user.getString("name"));
                String age = String.valueOf(user.getInteger("age"));
                String time = String.valueOf(user.getDouble("time"));
                String objStr = "name=" + name + ", age=" + age + ", time=" + time;
                result += objStr + "\n";

                // JSArray
                JSArray array = new JSArray(jsContext).push(1).push(3.14).push(true).push("Hello World");
                String vali = String.valueOf(array.getInteger(0));
                String vald = String.valueOf(array.getDouble(1));
                String arrayStr = "vali=" + vali + ", vald=" + vald;
                result += arrayStr + "\n";

                String finalResult = result;
                Log.d(TAG, finalResult);
                tv.post(() -> {
                    tv.setText(finalResult);
                });
            }
        }
    }

    private static class Console {
        int count = 0;

        @JavascriptInterface
        public void log(String msg) {
            count++;
            Log.d(TAG, "log: " + msg);
        }

        @JavascriptInterface
        public void info(String msg) {
            count++;
            Log.i(TAG, "info: " + msg);
        }

        @JavascriptInterface
        public void error(String msg) {
            count++;
            Log.e(TAG, "error: " + msg);
        }

        @JavascriptInterface
        public int count() {
            return count;
        }
    }

    private void testJSFunction(TextView tv) {
        try (QuickJS quickJS = QuickJS.createRuntime()) {
            try (JSContext jsContext = quickJS.createContext()) {
                // JSFunction
                final String[] result = {""};
                JSFunction log = new JSFunction(jsContext, new JavaVoidCallback() {
                    @Override
                    public void invoke(JSObject receiver, JSArray args) {
                        Log.d(TAG, "invoke tid=" + Thread.currentThread().getId());
                        String msg = args.getString(0);
                        result[0] += msg + "\n";
                        Log.d(TAG, result[0]);
                        tv.post(() -> {
                            tv.setText(result[0]);
                        });
                    }
                });
                JSFunction message = new JSFunction(jsContext, new JavaCallback() {
                    @Override
                    public Object invoke(JSObject receiver, JSArray args) {
                        return "Hello World";
                    }
                });
                jsContext.set("console", new JSObject(jsContext).set("log", log).set("message", message));
                jsContext.executeVoidScript("console.log(console.message())", null);

                // addJavascriptInterface
                jsContext.addJavascriptInterface(new Console(), "console");
                jsContext.executeVoidScript("console.log('Hello World')", null);
                int count = jsContext.executeIntegerScript("console.count()", null);
                String interfaceStr = String.valueOf(count);
                result[0] += interfaceStr + "\n";
                Log.d(TAG, "testJSFunction tid=" + Thread.currentThread().getId());
                Log.d(TAG, interfaceStr);
                tv.post(() -> {
                    tv.setText(result[0]);
                });
            }
        }
    }

    private void testPlugin(TextView tv) throws Exception {
        try (QuickJS quickJS = QuickJS.createRuntimeWithEventQueue()) {
            try (JSContext jsContext = quickJS.createContext()) {
                final String[] result = {""};
                jsContext.addPlugin(new ConsolePlugin() {
                    @Override
                    public void println(int priority, String msg) {
//                        super.println(priority, msg); // tag=QuickJS
                        Log.println(priority, TAG, msg);
                        result[0] += msg;
                    }
                });
                jsContext.addPlugin(new SetTimeoutPlugin());

                jsContext.executeVoidScript("var counter = 0;\n" +
                        "function print1() {\n" +
                        "  console.log(counter++);\n" +
                        "  setTimeout(print1, 10);\n" +
                        "}\n" +
                        "print1();", null);
                Thread.sleep(200);
                Log.d(TAG, result[0]);
                tv.post(() -> {
                    tv.setText(result[0]);
                });
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
