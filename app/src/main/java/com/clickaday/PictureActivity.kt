package com.clickaday


import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import com.clickaday.databinding.ActivityTakepictureBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import com.clickaday.CameraTools as CamTools


class PictureActivity : AppCompatActivity() {
    /**
     * Constant for the picture activity
     * @author Mathieu Castera
     */
    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var viewBinding: ActivityTakepictureBinding
        private var imageCapture: ImageCapture? = null
        private lateinit var cameraExecutor: ExecutorService
        private lateinit var outputOptions: ImageCapture.OutputFileOptions
        var lensFacing = CameraSelector.DEFAULT_FRONT_CAMERA

        const val FILENAME_FORMAT = "yyyy_MM_dd_HH_mm_ss"
        var NAME_CURRENT_PICTURE = ""
        const val PICTURE_TYPE = "jpeg"
        const val PICTURE_EXTENTION = ".jpg"
        const val PICTURE_FIRST_CHARACTERE = "D_"
    }

    /**
     * On create function
     * @author Mathieu Castera
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityTakepictureBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        imageCapture = CamTools.startCamera(this, lensFacing, viewBinding.viewFinder, this)

        // Set up the listeners for take photo and video capture buttons
        viewBinding.imageCaptureButton.setOnClickListener {

            if (!MainActivity.IMAGE_DIR_TMP.exists()) {
                // Créer le dossier s'il n'existe pas
                MainActivity.IMAGE_DIR_TMP.mkdirs()
            }

            setDataPicture()
            CamTools.takePhoto(this, imageCapture!!, outputOptions) { isSaved ->
                if (isSaved) {
                    goToVerifActivity()
                } else {
                    Toast.makeText(this, "Image not saved", Toast.LENGTH_LONG).show()
                }
            }
        }

        //il y a deux methode : soit find ViewById, soit viewBinding et apres on fait
        // setOnClickListener ou juste un .setOnClickListener
        viewBinding.returnCameraButton.setOnClickListener {
            lensFacing = CamTools.returnCamera(lensFacing)
            imageCapture = CamTools.startCamera(this, lensFacing, viewBinding.viewFinder, this)
        }
        viewBinding.returnActivityCameraButton.setOnClickListener { closeActivity() }
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun setDataPicture() {
        // Create time stamped name and MediaStore entry.
        val customName = PICTURE_FIRST_CHARACTERE
        val timeStamp =
            SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())
        val pictureName = "$customName$timeStamp"
        NAME_CURRENT_PICTURE = pictureName

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, pictureName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/${PICTURE_TYPE}")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/${MainActivity.NAME_PICTURES_FOLDER_TMP}")
        }
        // Create output options object which contains file + metadata
        outputOptions = ImageCapture.OutputFileOptions.Builder(
            contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
        ).build()
    }

    /**
     * Close current activity
     * @author Mathieu Castera
     */
    private fun closeActivity() {
        finish()
    }

    private fun goToVerifActivity() {
        val intent = Intent(this, VerifActivity::class.java)
        startActivity(intent)
        closeActivity()
    }

    /**
     * OnDestroy function we the activity is kill
     * @author Mathieu Castera
     */
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

}