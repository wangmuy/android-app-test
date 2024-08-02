package com.example.test.business

import android.content.ComponentName
import android.content.ContextWrapper
import android.content.Intent
import android.os.Binder
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.media.MediaBrowserServiceCompat
import com.example.test.R

class MainActivity: AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivityTAG"
    }
    private lateinit var contentTv: TextView
    private lateinit var listSpinner: Spinner

    private lateinit var playPauseBtn: Button
    private lateinit var prevBtn: Button
    private lateinit var nextBtn: Button

    private val LOCK = Any()
    private var mediaBrowser: MediaBrowserCompat? = null
    private val connectionCallback = object: MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            val msg = "onConnected, tid=${Thread.currentThread().id}, callingPid=${Binder.getCallingPid()}"
            Log.d(TAG, msg)
            logContent(msg)
            try {
                synchronized(LOCK) {
                    mediaBrowser?.sessionToken.also { token ->
                        val mediaController = MediaControllerCompat(this@MainActivity, token!!)
                        MediaControllerCompat.setMediaController(this@MainActivity, mediaController)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "", e)
            }
            val mediaController = MediaControllerCompat.getMediaController(this@MainActivity)
            updateTransportControls(mediaController)
        }

        override fun onConnectionSuspended() {
            val msg = "onConnectionSuspended, tid=${Thread.currentThread().id}, callingPid=${Binder.getCallingPid()}"
            Log.d(TAG, msg)
            logContent(msg)
        }

        override fun onConnectionFailed() {
            val msg = "onConnectionFailed, tid=${Thread.currentThread().id}, callingPid=${Binder.getCallingPid()}"
            Log.d(TAG, msg)
            logContent(msg)
            disconnectMediaBrowser()
        }
    }
    private val controllerCallback = object: MediaControllerCompat.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            val msg = "onMetadataChanged, tid=${Thread.currentThread().id}, callingPid=${Binder.getCallingPid()}, metadata=${getMetaDataStr(metadata)}"
            Log.d(TAG, msg)
            logContent(msg)
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            val msg = "onPlaybackStateChanged, tid=${Thread.currentThread().id}, callingPid=${Binder.getCallingPid()}, state=$state"
            Log.d(TAG, msg)
            logContent(msg)
        }

        override fun onSessionDestroyed() {
            val msg = "onSessionDestroyed, tid=${Thread.currentThread().id}, callingPid=${Binder.getCallingPid()}"
            Log.d(TAG, msg)
            logContent(msg)
            disconnectMediaBrowser()
        }
    }

    private fun getMetaDataStr(metadata: MediaMetadataCompat?): String {
        if (metadata == null) {
            return ""
        }
        val sb = StringBuilder()
        sb.appendLine("description=${metadata.description}")
        sb.appendLine("mediaMetadata=${metadata.mediaMetadata}")
        sb.appendLine("bundle=${metadata.bundle}")
        sb.appendLine("keySet=${metadata.keySet().joinToString(", ")}")
        return sb.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        contentTv = findViewById(R.id.contentTv)
        listSpinner = findViewById(R.id.svcListSpn)
        playPauseBtn = findViewById(R.id.playPauseBtn)
        prevBtn = findViewById(R.id.prevBtn)
        nextBtn = findViewById(R.id.nextBtn)

        findViewById<Button>(R.id.listSvcBtn).setOnClickListener{v->
            Thread {
                getMediaBrowserServices().let {list->
                    val text = list.joinToString("\n")
                    Log.d(TAG, "services: $text")
                    logContent(text)
                    runOnUiThread {
                        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, list)
                        listSpinner.adapter = adapter
                    }
                }
            }.start()
        }

        findViewById<Button>(R.id.bindSvcBtn).setOnClickListener{v->
            listSpinner.selectedItem?.toString()?.let {
                newConnectMediaBrowser(ComponentName.unflattenFromString(it))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disconnectMediaBrowser()
    }

    private fun logContent(content: String) {
        contentTv.post{
            contentTv.text = content
        }
    }

    private fun getMediaBrowserServices(): List<String> {
        val intent = Intent(MediaBrowserServiceCompat.SERVICE_INTERFACE)
        val services = packageManager.queryIntentServices(intent, 0)
        val list = mutableListOf<String>()
        for (info in services) {
            list.add("${info.serviceInfo.packageName}/${info.serviceInfo.name}")
        }
        return list
    }

    private fun newConnectMediaBrowser(component: ComponentName) {
        Log.d(TAG, "newConnect: $component")
        playPauseBtn.setText("play")
        try {
            synchronized(LOCK) {
                disconnectMediaBrowser()
                val rootHints = Bundle().apply {
//                    putBoolean("UCAR", true)
                }
                val ctx = object: ContextWrapper(this) {
                    override fun getPackageName(): String {
//                        return "whitelisted.app"
                        return super.getPackageName()
                    }
                }
                mediaBrowser = MediaBrowserCompat(this, component, connectionCallback, rootHints)
                mediaBrowser?.connect()
            }
        } catch (e: Exception) {
            Log.e(TAG, "", e)
        }
    }

    private fun disconnectMediaBrowser() {
        runOnUiThread{
            updateTransportControls(null)
        }
        synchronized(LOCK) {
            try {
                val mediaController = MediaControllerCompat.getMediaController(this)
                mediaController?.unregisterCallback(controllerCallback)
            } catch (e: Exception) {
                Log.e(TAG, "", e)
            }

            try {
                mediaBrowser?.disconnect()
            } catch (e: Exception) {
                Log.e(TAG, "", e)
            } finally {
                mediaBrowser = null
            }
        }
    }

    private fun updateTransportControls(mediaController: MediaControllerCompat?) {
        try {
            playPauseBtn.setOnClickListener {v ->
                if (mediaController == null) {
                    return@setOnClickListener
                }
                val pbState = mediaController.playbackState.state
                if (pbState == PlaybackStateCompat.STATE_PLAYING) {
                    mediaController.transportControls.pause()
                    (v as Button).setText("play")
                } else {
                    mediaController.transportControls.play()
                    (v as Button).setText("pause")
                }
            }
            prevBtn.setOnClickListener{v->
                mediaController?.transportControls?.skipToPrevious()
            }
            nextBtn.setOnClickListener{v->
                mediaController?.transportControls?.skipToNext()
            }
            val metadata = mediaController?.metadata
            val pbState = mediaController?.playbackState
            when (pbState?.state) {
                PlaybackStateCompat.STATE_PLAYING -> playPauseBtn.setText("pause")
                else -> playPauseBtn.setText("play")
            }
            val msg = "updateTransportControls metadata=${getMetaDataStr(metadata)}, pbState=$pbState"
            Log.d(TAG, msg)
            logContent(msg)
            mediaController?.registerCallback(controllerCallback)
        } catch (e: Exception) {
            Log.e(TAG, "", e)
        }
    }
}
