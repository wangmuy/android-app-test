package com.example.test;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.eclipsesource.v8.V8;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.example.test", appContext.getPackageName());
    }

    @Test
    public void j2v8Test() throws Exception {
        String script = "var hello = 'hello';\n"
                + "var world = 'world';\n"
                + "hello.concat(world).length;";

        V8 runtime = V8.createV8Runtime();
        int result = runtime.executeIntegerScript(script);
        runtime.close();
        assertEquals(10, result);
    }
}