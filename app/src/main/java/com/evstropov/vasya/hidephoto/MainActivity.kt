package com.evstropov.vasya.hidephoto

import android.content.Context
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.Button
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import android.content.pm.PackageManager
import android.widget.FrameLayout
import android.R.attr.data
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
import android.support.v4.app.FragmentActivity
import java.io.FileNotFoundException


class MainActivity : AppCompatActivity() {

    lateinit var btnPhoto: Button

    private var mCamera: Camera? = null
    private var mPreview: CameraPreview? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initialize()


    }

    private fun initialize() {
        btnPhoto = findViewById(R.id.btnPhoto)
        mCamera = getCameraInstance()

        // Create our Preview view and set it as the content of our activity.
        mPreview = CameraPreview(this, mCamera)
        val preview = findViewById(R.id.camera_preview) as FrameLayout
        preview.addView(mPreview)

        btnPhoto.setOnClickListener {
            checkCameraHardware(this)
            mCamera?.takePicture({ }, { bytes, camera -> },
                    { data, camera ->
                        val pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE)
                        if (pictureFile == null) {
                            Log.d("Log.d", "Error creating media file, check storage permissions: ")

                        }else{
                            Log.d("Log.d", "media file not null")
                        }

                        try {
                            val fos = FileOutputStream(pictureFile!!)
                            fos.write(data)
                            fos.close()
                            Log.d("Log.d", "File write succesfully")
                        } catch (e: FileNotFoundException) {
                            Log.d("Log.d", "File not found: " + e.message)
                        } catch (e: IOException) {
                            Log.d("Log.d", "Error accessing file: " + e.message)
                        }

                    })
        }


    }

    val MEDIA_TYPE_IMAGE = 1;
    val MEDIA_TYPE_VIDEO = 2;

    /** Create a file Uri for saving an image or video */
    fun getOutputMediaFileUri(type: Int): Uri {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    fun getOutputMediaFile(type: Int): File? {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        var mediaStorageDir: File = File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        var timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date());
        var mediaFile: File;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
            Log.d("Log.d", mediaFile.absolutePath)
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    /** A safe way to get an instance of the Camera object.  */
    fun getCameraInstance(): Camera? {
        var c: Camera? = null
        try {
            c = Camera.open() // attempt to get a Camera instance
        } catch (e: Exception) {
            // Camera is not available (in use or does not exist)
        }

        return c // returns null if camera is unavailable
    }

    /** Check if this device has a camera  */
    private fun checkCameraHardware(context: Context): Boolean {
        return if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            true
        } else {
            // no camera on this device
            false
        }
    }


    private fun makePhotos(count: Int) {
        var mCamera: Camera?
        var cameraCount: Int = 0
        var cameraInfo: Camera.CameraInfo = Camera.CameraInfo()
        cameraCount = Camera.getNumberOfCameras()
        // I pass the getApplicationContext() from the main activity.
        var st: SurfaceTexture? = SurfaceTexture(10);
        mCamera = Camera.open();
        try {
            mCamera.setPreviewTexture(st)
        } catch (e: IOException) {
            println("Failed to setPreviewTexture")
        }

        mCamera.setPreviewCallback(Camera.PreviewCallback { bytes: ByteArray, camera: Camera ->
            println("onPreview callback called")
        })
        Thread.sleep(1000);
        st = null
        mCamera.startPreview();
    }
}
