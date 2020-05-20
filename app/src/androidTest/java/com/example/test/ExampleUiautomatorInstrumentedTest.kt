package com.example.test

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.*
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

private const val PACKAGE_NAME = "com.example.test"
private const val LAUNCH_TIMEOUT = 5000L

/**
 * 注意: 可能需要设置手机, 允许后台启动应用
 */
@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 18)
class ExampleUiautomatorInstrumentedTest {
    private lateinit var device: UiDevice;

    @Before
    fun startMainActivityFromHomeScreen() {
        val instrumentation = InstrumentationRegistry.getInstrumentation();
        device = UiDevice.getInstance(instrumentation);

        device.pressHome();
        val launcherPackage: String = device.launcherPackageName;
        assertThat(launcherPackage, notNullValue());
        device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);

//        val context = instrumentation.context;
        val context = ApplicationProvider.getApplicationContext<Context>();
        val intent = context.packageManager.getLaunchIntentForPackage(PACKAGE_NAME).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        };
        println("test intent=$intent");
        context.startActivity(intent);

        device.wait(Until.hasObject(By.pkg(PACKAGE_NAME).depth(0)), LAUNCH_TIMEOUT);
        assertEquals(PACKAGE_NAME, device.currentPackageName);
    }

    @Test
    fun hasTextGuestMagicNumber() {
        val guestMagicNumberTextView: UiObject = device.findObject(
                UiSelector().className("androidx.recyclerview.widget.RecyclerView")
                        .instance(0)
                        .childSelector(UiSelector().text("Guest Magic Number"))
        );
        assertThat(guestMagicNumberTextView, notNullValue());
        guestMagicNumberTextView.text = "Uiautomator Guest Magic Number";
    }
}
