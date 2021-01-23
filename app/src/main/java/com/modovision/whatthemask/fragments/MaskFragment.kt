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

package com.modovision.whatthemask.fragments

import android.content.Intent
import android.graphics.*
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.viewpager.widget.ViewPager
import com.modovision.whatthemask.BuildConfig
import com.modovision.whatthemask.R
import com.modovision.whatthemask.utils.padWithDisplayCutout
import com.modovision.whatthemask.utils.showImmersive
import detection.customview.OverlayView
import detection.env.Logger
import detection.env.Utils
import detection.tflite.Classifier
import detection.tflite.YoloV5Classifier
import detection.tracking.MultiBoxTracker
import kotlinx.android.synthetic.main.fragment_mask.*
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.math.max


/** Fragment used to present the user with a gallery of photos taken */
class MaskFragment internal constructor() : Fragment() {

//    private var  MINIMUM_CONFIDENCE_TF_OD_API = 0.4f
//
//    private val LOGGER: Logger = Logger()
//
//    val TF_OD_API_INPUT_SIZE = 640
//
//    private val TF_OD_API_IS_QUANTIZED = true
//
//    private val TF_OD_API_MODEL_FILE = "best.tflite"
//
//    private val TF_OD_API_LABELS_FILE = "file:///android_asset/lplabel.txt"
//
//    // Minimum detection confidence to track a detection.
//    private val MAINTAIN_ASPECT = true
//    private val sensorOrientation = 90
//
//    private var detector: Classifier? = null
//
//    private val frameToCropTransform: Matrix? = null
//    private val cropToFrameTransform: Matrix? = null
//    private val tracker: MultiBoxTracker? = null
//    private val trackingOverlay: OverlayView? = null
//
//    protected var previewWidth = 0
//    protected var previewHeight = 0
//
//    private lateinit var sourceBitmap: Bitmap
//    private lateinit var cropBitmap: Bitmap
//    private lateinit var copyBitmap: Bitmap


    /** AndroidX navigation arguments */
    private val args: MaskFragmentArgs by navArgs()

    private lateinit var mediaList: MutableList<File>

//    private lateinit var bitmap: Bitmap
//    private lateinit var backBitmap: Bitmap
//
//    private var oHeight:Float = 0.0f
//    private var oWidth:Float = 0.0f


//    private  var detectButton:android.widget.ImageButton? = null
//    private lateinit var results: List<Classifier.Recognition>


    /** Adapter class used to present a fragment containing one photo or video as a page */
    inner class MediaPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getCount(): Int = 1
        override fun getItem(position: Int): Fragment = PhotoFragment.create(mediaList[0])
        override fun getItemPosition(obj: Any): Int = POSITION_NONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mark this as a retain fragment, so the lifecycle does not get restarted on config change
        retainInstance = true

        // Get root directory of media from navigation arguments
        val rootDirectory = File(args.rootDirectory)

        // Walk through all files in the root directory
        // We reverse the order of the list to present the last photos first
        mediaList = rootDirectory.listFiles { file ->
            EXTENSION_WHITELIST.contains(file.extension.toUpperCase(Locale.ROOT))
        }?.sortedDescending()?.toMutableList() ?: mutableListOf()



//        camera_capture_button_mask.setOnClickListener {
////            println(requireActivity().assets)
//            imageView2.setImageResource(0)
//
//            try {
//                detector = YoloV5Classifier.create(
//                        requireActivity().assets,
//                        TF_OD_API_MODEL_FILE,
//                        TF_OD_API_LABELS_FILE,
//                        TF_OD_API_IS_QUANTIZED,
//                        TF_OD_API_INPUT_SIZE)
////                val results: List<Classifier.Recognition> = detector.recognizeImage(cropBitmap)
////                handleResult(copyBitmap, results)
//            }  catch (e:IOException) {
//            }
//            val handler = Handler()
//
//            Thread(Runnable {
//                val results: List<Classifier.Recognition> = detector!!.recognizeImage(cropBitmap)
//                handler.post { handleResult(copyBitmap, results) }
//            }).start()
////            val results: List<Classifier.Recognition> = detector!!.recognizeImage(cropBitmap)
////            handleResult(copyBitmap, results)
//        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_mask, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Checking media files list

        // Populate the ViewPager and implement a cache of two media items
        val mediaViewPager = view.findViewById<ViewPager>(R.id.photo_view_pager).apply {
            offscreenPageLimit = 2
            adapter = MediaPagerAdapter(childFragmentManager)
        }
//        System.out.println(mediaList[0])
//        bitmap = BitmapFactory.decodeFile(mediaList[0].toString())
//
//        copyBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
//
//        cropBitmap = Utils.processBitmap(bitmap, TF_OD_API_INPUT_SIZE)

//        System.out.println(cropBitmap.width)

        // Make sure that the cutout "safe area" avoids the screen notch if any
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // Use extension method to pad "inside" view containing UI using display cutout's bounds
            view.findViewById<ConstraintLayout>(R.id.cutout_safe_area).padWithDisplayCutout()
        }

        // mask and save

//        detectButton = view.findViewById(R.id.camera_capture_button_mask)

//        view.findViewById<ImageButton>(R.id.camera_capture_button_mask).setOnClickListener {
////            println(requireActivity().assets)
//            imageView2.setImageResource(0)
////            photo_view_pager.setImageResource(0)
//
////            try {
////                detector = YoloV5Classifier.create(
////                        requireActivity().assets,
////                        TF_OD_API_MODEL_FILE,
////                        TF_OD_API_LABELS_FILE,
////                        TF_OD_API_IS_QUANTIZED,
////                        TF_OD_API_INPUT_SIZE)
//////                val results: List<Classifier.Recognition> = detector.recognizeImage(cropBitmap)
//////                handleResult(copyBitmap, results)
////            }  catch (e:IOException) {
////            }
////            val handler = Handler()
////
////            Thread(Runnable {
////                val results: List<Classifier.Recognition> = detector!!.recognizeImage(cropBitmap)
////                handler.post { handleResult(copyBitmap, results) }
////            }).start()
//            val results: List<Classifier.Recognition> = detector!!.recognizeImage(cropBitmap)
//            handleResult(copyBitmap, results)
//        }


//        this.imageView.setImageBitmap(sourceBitmap);

        // Handle back button press
        view.findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            Navigation.findNavController(requireActivity(), R.id.fragment_container).navigateUp()
        }

        // Handle share button press
        view.findViewById<ImageButton>(R.id.share_button).setOnClickListener {

            mediaList.getOrNull(mediaViewPager.currentItem)?.let { mediaFile ->

                // Create a sharing intent
                val intent = Intent().apply {
                    // Infer media type from file extension
                    val mediaType = MimeTypeMap.getSingleton()
                            .getMimeTypeFromExtension(mediaFile.extension)
                    // Get URI from our FileProvider implementation
                    val uri = FileProvider.getUriForFile(
                            view.context, BuildConfig.APPLICATION_ID + ".provider", mediaFile)
                    // Set the appropriate intent extra, type, action and flags
                    putExtra(Intent.EXTRA_STREAM, uri)
                    type = mediaType
                    action = Intent.ACTION_SEND
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                }

                // Launch the intent letting the user choose which app to share with
                startActivity(Intent.createChooser(intent, getString(R.string.share_hint)))
            }
        }

        // Handle delete button press
        view.findViewById<ImageButton>(R.id.delete_button).setOnClickListener {

            mediaList.getOrNull(mediaViewPager.currentItem)?.let { mediaFile ->

                AlertDialog.Builder(view.context, android.R.style.Theme_Material_Dialog)
                        .setTitle(getString(R.string.delete_title))
                        .setMessage(getString(R.string.delete_dialog))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes) { _, _ ->

                            // Delete current photo
                            mediaFile.delete()

                            // Send relevant broadcast to notify other apps of deletion
                            MediaScannerConnection.scanFile(
                                    view.context, arrayOf(mediaFile.absolutePath), null, null)

                            // Notify our view pager
                            mediaList.removeAt(mediaViewPager.currentItem)
//                            mediaViewPager.adapter?.notifyDataSetChanged()

                            Navigation.findNavController(requireActivity(), R.id.fragment_container).navigateUp()

                        }
                        .setNegativeButton(android.R.string.no, null)
                        .create().showImmersive()
            }
        }
    }
}