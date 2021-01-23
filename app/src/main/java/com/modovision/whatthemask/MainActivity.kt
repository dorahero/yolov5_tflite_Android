/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.modovision.whatthemask

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.TextureView
import android.widget.FrameLayout
import com.macasaet.fernet.Token
import com.macasaet.fernet.Key
import com.modovision.whatthemask.utils.FLAGS_FULLSCREEN
import io.github.rybalkinsd.kohttp.dsl.httpPost
import okhttp3.Response
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.typeOf


const val KEY_EVENT_ACTION = "key_event_action"
const val KEY_EVENT_EXTRA = "key_event_extra"
private const val IMMERSIVE_FLAG_TIMEOUT = 500L
const val MINIMUM_CONFIDENCE_TF_OD_API = 0.4f
lateinit var token: String



/**
 * Main entry point into our app. This app follows the single-activity pattern, and all
 * functionality is implemented in the form of fragments.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var container: FrameLayout

    private lateinit var textureView: TextureView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Thread(Runnable {
            val key1: InputStream = this.assets.open("ttt.txt")
            val brkey = BufferedReader(InputStreamReader(key1))
            val key = brkey.readLine()
            token = getToken(key)
        }).start()
//        System.out.println(this.assets)
        container = findViewById(R.id.fragment_container)

    }



    override fun onResume() {
        super.onResume()
        // Before setting full screen flags, we must wait a bit to let UI settle; otherwise, we may
        // be trying to set app to immersive mode before it's ready and the flags do not stick
        container.postDelayed({
            container.systemUiVisibility = FLAGS_FULLSCREEN
        }, IMMERSIVE_FLAG_TIMEOUT)
    }

    /** When key down event is triggered, relay it via local broadcast so fragments can handle it */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return when (keyCode) {
//            KeyEvent.KEYCODE_VOLUME_DOWN -> {
//                val intent = Intent(KEY_EVENT_ACTION).apply { putExtra(KEY_EVENT_EXTRA, keyCode) }
//                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
//                true
//            }
            else -> super.onKeyDown(keyCode, event)
        }
    }


//    private val tflite by lazy {
//        Interpreter(
//                FileUtil.loadMappedFile(this, MainActivity.MODEL_PATH),
//                Interpreter.Options().addDelegate(NnApiDelegate()))
//    }
//
//    private val detector by lazy {
//        ObjectDetectionHelper(
//                tflite,
//                FileUtil.loadLabels(this, MainActivity.LABELS_PATH)
//        )
//    }
    companion object {

        /** Helper function used to create a timestamped file */
        private fun createFile(baseFolder: File, format: String, extension: String) =
                File(baseFolder, SimpleDateFormat(format, Locale.TAIWAN)
                        .format(System.currentTimeMillis()) + extension)

        /** Use external media if it is available, our app's file directory otherwise */
        fun getOutputDirectory(context: Context): File {
            val appContext = context.applicationContext
            val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
                File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() } }
            return if (mediaDir != null && mediaDir.exists())
                mediaDir else appContext.filesDir
        }
        fun getToken(key: String): String {
            var token: String
            val timestamp = Timestamp(System.currentTimeMillis())
            val key0 = timestamp.getTime().toString() + "/app"
            // Generate salt
            val fernetKey = Key(key.toString())
            val data = key0
            token = Token.generate(fernetKey, data).serialise()

            try {
                val responseInit: Response = httpPost {
                    host = "modovision-api.eastasia.cloudapp.azure.com"
                    port = 8080
                    path = "/auth/token"
                    body {
                        json {
                            "key" to token
                        }
                    }
                }
                val token = JSONObject(responseInit.body()?.string())["token"]
                return token.toString()
            } catch (e: Exception) {
                Log.d("ERROR", e.toString())
                return ""
            }
        }
    }
}
