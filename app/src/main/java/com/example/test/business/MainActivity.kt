package com.example.test.business

import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsCallback
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import androidx.browser.trusted.TrustedWebActivityIntentBuilder
import com.example.test.R
import com.google.androidbrowserhelper.trusted.QualityEnforcer
import com.google.androidbrowserhelper.trusted.TwaLauncher;
import com.google.androidbrowserhelper.trusted.TwaProviderPicker

class MainActivity: AppCompatActivity() {
    companion object {
        private const val TAG = "TestMainActivity"
        private val LAUNCH_URI = Uri.parse("http://127.0.0.1:8000")
    }

    private val customTabsCallback: CustomTabsCallback = QualityEnforcer()
    private var serviceBound = false
    private val launchers = ArrayList<TwaLauncher>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnLaunch).setOnClickListener{v->
            launch(v)
        }
        findViewById<Button>(R.id.btnCustom).setOnClickListener{v->
            launchWithCustomColors(v)
        }
        findViewById<Button>(R.id.btnMultipleOrigins).setOnClickListener{v->
            launchWithMultipleOrigins(v)
        }
        findViewById<Button>(R.id.btnReferrer).setOnClickListener{v->
            launchWithCustomReferrer(v)
        }
    }

    private fun launch(v: View) {
        try {
            val uri = LAUNCH_URI
            val launcher = TwaLauncher(this)
            launcher.launch(uri)
            launchers.add(launcher)
        } catch (e: Exception) {
            Log.e(TAG, "", e)
        }
    }

    private fun launchWithCustomColors(v: View) {
        try {
            val builder = TrustedWebActivityIntentBuilder(LAUNCH_URI)
                .setDefaultColorSchemeParams(
                    CustomTabColorSchemeParams.Builder()
                        .setNavigationBarColor(Color.RED)
                        .setToolbarColor(Color.BLUE)
                        .build()
                )
            val launcher = TwaLauncher(this)
            launcher.launch(builder, customTabsCallback, null, null)
            launchers.add(launcher)
        } catch (e: Exception) {
            Log.e(TAG, "", e)
        }
    }

    private fun launchWithMultipleOrigins(v: View) {
        try {
            val origins = listOf("http://127.0.0.1:8000", "http://localhost:8000")
            val builder = TrustedWebActivityIntentBuilder(LAUNCH_URI)
                .setAdditionalTrustedOrigins(origins)
            val launcher = TwaLauncher(this)
            launcher.launch(builder, customTabsCallback, null, null)
            launchers.add(launcher)
        } catch (e: Exception) {
            Log.e(TAG, "", e)
        }
    }

    private val customTabsServiceConnection = object: CustomTabsServiceConnection() {
        private val SESSION_ID = 45 // arbitrary constant
        private var session: CustomTabsSession? = null

        override fun onServiceDisconnected(name: ComponentName?) {
            session = null
        }

        override fun onCustomTabsServiceConnected(name: ComponentName, client: CustomTabsClient) {
            try {
                session = client.newSession(null, SESSION_ID)
                if (session == null) {
                    val msg = "Couldn't get session from provider."
                    Log.d(TAG, msg)
                    Toast.makeText(this@MainActivity, msg, Toast.LENGTH_LONG).show()
                } else {
                    val intent = TrustedWebActivityIntentBuilder(LAUNCH_URI).build(session!!).intent
                    intent.putExtra(
                        Intent.EXTRA_REFERRER,
                        Uri.parse("android-app://${packageName}?twa=true")
                    )
                    startActivity(intent)
                }
            } catch (e: Exception) {
                Log.e(TAG, "", e)
            }
        }
    }
    private fun launchWithCustomReferrer(v: View) {
        try {
            val action = TwaProviderPicker.pickProvider(packageManager)
            if (!serviceBound) {
                CustomTabsClient.bindCustomTabsService(
                    this,
                    action.provider,
                    customTabsServiceConnection
                )
                serviceBound = true
            }
        } catch (e: Exception) {
            Log.e(TAG, "", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        for (launcher in launchers) {
            launcher.destroy()
        }
    }
}
