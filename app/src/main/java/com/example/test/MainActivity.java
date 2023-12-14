package com.example.test;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.hippo.quickjs.android.JSContext;
import com.hippo.quickjs.android.JSFunction;
import com.hippo.quickjs.android.JSNumber;
import com.hippo.quickjs.android.JSRuntime;
import com.hippo.quickjs.android.JSValue;
import com.hippo.quickjs.android.Method;
import com.hippo.quickjs.android.QuickJS;
import com.hippo.quickjs.android.TypeAdapter;

import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MyMainActivity";

    private TextView mTv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTv1 = (TextView) findViewById(R.id.tv1);

        Button btn1 = (Button) findViewById(R.id.btn1);
        btn1.setOnClickListener(v -> {
            testJSEval();
        });

        Button btn2 = (Button) findViewById(R.id.btn2);
        btn2.setOnClickListener(v -> {
            testJSCallJavaMethod();
        });

        Button btn3 = (Button) findViewById(R.id.btn3);
        btn3.setOnClickListener(v -> {
            testJSFunctionCallback();
        });

        Button btn4 = (Button) findViewById(R.id.btn4);
        btn4.setOnClickListener(v -> {
            testPromise();
        });

        Button btn5 = (Button) findViewById(R.id.btn5);
        btn5.setOnClickListener(v -> {
            testConvertJSValueToJavaValue();
        });
    }

    private void testJSEval() {
        QuickJS quickJS = new QuickJS.Builder().build();
        try (JSRuntime runtime = quickJS.createJSRuntime()) {
            try (JSContext context = runtime.createJSContext()) {
                String script1 = "" +
                        "function fibonacci(n) {" +
                        " if (n == 0 || n == 1) return n;" +
                        " return fibonacci(n - 1) + fibonacci(n - 2);" +
                        "}";
                context.evaluate(script1, "fibonacci.js");

                String script2 = "fibonacci(10);";
                int result = context.evaluate(script2, "fibonacci.js", int.class);
                mTv1.setText(Integer.toString(result));
            }
        }
    }

    private void testJSCallJavaMethod() {
        QuickJS quickJS = new QuickJS.Builder().build();
        try (JSRuntime runtime = quickJS.createJSRuntime()) {
            try (JSContext context = runtime.createJSContext()) {
                Integer integer = 0;
                JSFunction zeroCompareTo = context.createJSFunction(integer, Method.create(Integer.class, Integer.class.getMethod("compareTo", Integer.class)));
                context.getGlobalObject().setProperty("zeroCompareTo", zeroCompareTo);
                int i1 = context.evaluate("zeroCompareTo(1)", "test.js", int.class);
                int i2 = context.evaluate("zeroCompareTo(-1)", "test.js", int.class);
                String s1 = String.format(Locale.getDefault(), "%d %d", i1, i2);

                JSFunction javaAbs = context.createJSFunctionS(Math.class, Method.create(Math.class, Math.class.getMethod("abs", int.class)));
                context.getGlobalObject().setProperty("javaAbs", javaAbs);
                int i3 = context.evaluate("javaAbs(1)", "test.js", int.class);
                int i4 = context.evaluate("javaAbs(-1)", "test.js", int.class);
                String s2 = String.format(Locale.getDefault(), "%d %d", i3, i4);

                mTv1.setText(s1 + ", " + s2);
            } catch (Exception e) {
                Log.e(TAG, "", e);
            }
        }
    }

    private void testJSFunctionCallback() {
        QuickJS quickJS = new QuickJS.Builder().build();
        try (JSRuntime runtime = quickJS.createJSRuntime()) {
            try (JSContext context = runtime.createJSContext()) {
                JSValue plusFunction = context.createJSFunction((ctx, args) -> {
                    int a = args[0].cast(JSNumber.class).getInt();
                    int b = args[1].cast(JSNumber.class).getInt();
                    int sum = a + b;
                    return context.createJSNumber(sum);
                });
                context.getGlobalObject().setProperty("plus", plusFunction);
                int result = context.evaluate("plus(1, 2)", "test.js", Integer.class);
                mTv1.setText(Integer.toString(result));
            }
        }
    }

    private void testPromise() {
        QuickJS quickJS = new QuickJS.Builder().build();
        try (JSRuntime runtime = quickJS.createJSRuntime()) {
            try (JSContext context = runtime.createJSContext()) {
                context.evaluate("a = 1;Promise.resolve().then(() => { a = 2 })", "test.js");
                int i1 = context.getGlobalObject().getProperty("a").cast(JSNumber.class).getInt();
                boolean pendingDone = context.executePendingJob();
                int i2 = context.getGlobalObject().getProperty("a").cast(JSNumber.class).getInt();
                mTv1.setText(String.format(Locale.getDefault(), "%d %s %d", i1, pendingDone, i2));
            }
        }
    }

    public interface Calculator {
        double plus(double a, double b);
        double minus(double a, double b);
        double multiplies(double a, double b);
        double divides(double a, double b);
        void noop();
    }

    private static class AtomicIntegerTypeAdapter extends TypeAdapter<AtomicInteger> {
        @Override
        public JSValue toJSValue(Depot depot, Context context, AtomicInteger value) {
            return context.createJSNumber(value.get());
        }

        @Override
        public AtomicInteger fromJSValue(Depot depot, Context context, JSValue value) {
            return new AtomicInteger(value.cast(JSNumber.class).getInt());
        }
    }

    private void testConvertJSValueToJavaValue() {
        QuickJS quickJS = new QuickJS.Builder().registerTypeAdapter(AtomicInteger.class, new AtomicIntegerTypeAdapter()).build();
        try (JSRuntime runtime = quickJS.createJSRuntime()) {
            try (JSContext context = runtime.createJSContext()) {
                String[] result = context.evaluate("['hello', 'world']", "test.js", String[].class);

                Calculator calculator = context.evaluate("" +
                        "a = {\n" +
                        "  plus: function(a, b) { return a + b },\n" +
                        "  minus: function(a, b) { return a - b },\n" +
                        "  multiplies: function(a, b) { return a * b },\n" +
                        "  divides: function(a, b) { return a / b },\n" +
                        "  noop: function() { }\n" +
                "}","test.js", Calculator.class);
                double d1 = calculator.divides(8, 2);

                AtomicInteger atomicInteger = context.evaluate("1", "test.js", AtomicInteger.class);

                mTv1.setText(String.format(Locale.getDefault(), "%s %f %d", Arrays.toString(result), d1, atomicInteger.get()));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
