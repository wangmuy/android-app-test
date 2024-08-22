package com.example.test.tasks

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.test.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity: AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivitySend"
    }

    private val coroutine = lifecycleScope

    private lateinit var previewImg: ImageView
    private lateinit var contentTv: TextView
    private lateinit var shareSheetTextBtn: Button
    private lateinit var shareSheetImgBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        previewImg = findViewById(R.id.previewImg)
        contentTv = findViewById(R.id.contentTv)
        shareSheetTextBtn = findViewById<Button>(R.id.shareSheetTextBtn).also {
            it.setOnClickListener {
                onShareSheetText()
            }
        }
        shareSheetImgBtn = findViewById<Button>(R.id.shareSheetImgBtn).also {
            it.setOnClickListener {
                onShareSheetImg()
            }
        }

        when (intent?.action) {
             Intent.ACTION_SEND,
             Intent.ACTION_SEND_MULTIPLE -> {
                    handleReceive(intent)
            }
        }
    }

    private fun onShareSheetText() {
        var shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "HelloWorld!")
            type = "text/plain"
        }
        shareIntent = Intent.createChooser(shareIntent, null)
        startActivity(shareIntent)
    }

    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {uri->
        Log.d(TAG, "picked uri=$uri")
        if (uri != null) {
            var shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
//                    val uri = Uri.parse("content://media/external/images/media/1000015334")
                putExtra(Intent.EXTRA_STREAM, uri)
                type = "image/jpg"
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            shareIntent = Intent.createChooser(shareIntent, null)
            startActivity(shareIntent)
        }
    }
    private fun onShareSheetImg() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun handleReceive(intent: Intent) {
        try {
            when (intent.action) {
                Intent.ACTION_SEND -> {
                    if ("text/plain" == intent.type) {
                        contentTv.text = intent.getStringExtra(Intent.EXTRA_TEXT)
                    } else if (intent.type?.startsWith("image/") == true) {
                        (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
                            coroutine.launch {
                                val bitmap = withContext(Dispatchers.IO) {
                                    MediaStore.Images.Media.getBitmap(contentResolver, it)
                                }
                                previewImg.setImageBitmap(bitmap)
                            }
                        }
                    }
                }

                Intent.ACTION_SEND_MULTIPLE -> {
                    intent.getParcelableArrayListExtra<Parcelable>(Intent.EXTRA_STREAM)?.let {
                        // todo
                    }
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "", e)
            contentTv.text = e.stackTraceToString()
        }
    }
}
