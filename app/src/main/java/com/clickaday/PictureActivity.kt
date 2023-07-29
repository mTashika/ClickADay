package com.clickaday

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.clickaday.databinding.ActivityTakepictureBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class PictureActivity : AppCompatActivity() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var viewBinding: ActivityTakepictureBinding
        private var imageCapture: ImageCapture? = null
        private var lensFacing = CameraSelector.DEFAULT_FRONT_CAMERA
        private lateinit var cameraExecutor: ExecutorService

        private const val TAG = "ClickADay"
        private const val FILENAME_FORMAT = "yyyy_MM_dd_HH_mm_ss"
        var NAME_CURRENT_PICTURE =""
        private const val PICTURE_TYPE="jpeg"
        const val PICTURE_EXTENTION = ".jpg"
        private const val PICTURE_FIRST_CHARACTERE = "D_"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityTakepictureBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        startCamera(lensFacing)

        // Set up the listeners for take photo and video capture buttons
        viewBinding.imageCaptureButton.setOnClickListener { takePhoto() }
        viewBinding.returnCameraButton.setOnClickListener{returnCamera()}
        viewBinding.returnActivityCameraButton.setOnClickListener{closeActivity()}
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun returnCamera() {
        if (lensFacing == CameraSelector.DEFAULT_FRONT_CAMERA) lensFacing = CameraSelector.DEFAULT_BACK_CAMERA
        else if (lensFacing == CameraSelector.DEFAULT_BACK_CAMERA) lensFacing = CameraSelector.DEFAULT_FRONT_CAMERA
        startCamera(lensFacing)
    }

    private fun takePhoto() {

        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val customName= PICTURE_FIRST_CHARACTERE
        val timeStamp = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val pictureName = "$customName$timeStamp"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, pictureName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/${PICTURE_TYPE}")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/${MainActivity.PICTURES_FOLDER}")

        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()


        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }
                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults){
                    //val msg = "Photo capture succeeded"
                    //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    //Log.d(TAG, msg)
                    NAME_CURRENT_PICTURE=pictureName
                    val intent = Intent(this@PictureActivity, VerifActivity::class.java)
                    startActivity(intent)
                    closeActivity()
                }
            }
        )

    }

    private fun closeActivity() {
        finish()
    }

    private fun startCamera(cameraSelector: CameraSelector) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder().build()

            // Select back camera as a default


            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)


            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))

    }
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}