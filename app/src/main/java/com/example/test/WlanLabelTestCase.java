package com.example.test;

import android.os.RemoteException;
import android.widget.TextView;

import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

public class WlanLabelTestCase extends UiAutomatorTestCase {

    protected UiDevice device = null;
    protected String appName;

    public WlanLabelTestCase() {
        this("设置");
    }

    public WlanLabelTestCase(String appName) {
        this.appName = appName;
    }

    public void runApp(String appName) throws UiObjectNotFoundException, RemoteException {
        device = UiDevice.getInstance();
        device.pressHome();
        device.waitForWindowUpdate("", 2000);

        UiObject allAppsButton = new UiObject(new UiSelector().description("应用"));
        allAppsButton.click();
        device.waitForWindowUpdate("", 2000);

        UiScrollable appViews = new UiScrollable(new UiSelector().scrollable(true).resourceId("com.android.launcher3:id/apps_list_view"));
        appViews.setAsVerticalList();

        UiObject settingsApp = appViews.getChildByText(new UiSelector().className(TextView.class.getName()), appName);
        settingsApp.clickAndWaitForNewWindow();

        assertTrue("Unable to detect app", settingsApp != null);
    }

    @Override
    public void setUp() throws RemoteException, UiObjectNotFoundException {
        this.runApp(appName);
    }

    @Override
    public void tearDown() throws RemoteException, UiObjectNotFoundException {
        //Empty for the moment
    }

    public void testWLANSettingExists() {
        UiObject wlanLabel = new UiObject(new UiSelector().className(TextView.class.getName()).text("WLAN"));
        assertTrue("WLAN label not found", wlanLabel != null);
    }
}