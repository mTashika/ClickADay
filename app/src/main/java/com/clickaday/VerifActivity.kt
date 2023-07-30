package com.clickaday


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Bundle
import android.os.Environment
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class VerifActivity : AppCompatActivity() {
    /**
     * On Create function
     * @author Mathieu Castera
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verifpicture)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        displayImage()
        savePicture()
        returnPictureActivity()
    }

    /**
     * Correct the orientation of one picture
     * @author Mathieu Castera
     */
    fun correctOrientation(currentFile: File): Bitmap? {
        val bitmap = BitmapFactory.decodeFile(currentFile.path)

// Récupérer l'orientation de l'image à l'aide de ExifInterface
        val exif = ExifInterface(currentFile.path)
        val orientation =
            exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)

// Corriger l'orientation de l'image si nécessaire
        val rotatedBitmap = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90F)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180F)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270F)
            else -> bitmap
        }
        return rotatedBitmap
    }

    /**
     * Doublon - function to rotate the picture bitmap
     * @author Mathieu Castera
     */
    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    /**
     * Display image (big one)
     * @author Mathieu Castera
     */
    private fun displayImage() {
        val pictureView = findViewById<ImageView>(R.id.picture_view_verif)
        val directory = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            MainActivity.PICTURES_FOLDER
        )

        if (directory.exists() && !directory.listFiles()?.isEmpty()!!) {
            val lastFile = directory.listFiles()!!.get(directory.listFiles()?.size?.minus(1)!!)
            val bitmap = correctOrientation(lastFile)
            pictureView.setImageBitmap(bitmap)
        }
    }

    /**
     * Return to the picture activity
     * @author Mathieu Castera
     */
    private fun returnPictureActivity() {
        val returnButton = findViewById<Button>(R.id.doPictureAgainButton)
        returnButton.setOnClickListener {
            deleteCurrentPicture()
            finish()
        }
    }

    /**
     * Delete the picture (saved in the folder)
     * @author Mathieu Castera
     */
    private fun deleteCurrentPicture() {
        val fileToDelete = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "${MainActivity.PICTURES_FOLDER}/${PictureActivity.NAME_CURRENT_PICTURE}${PictureActivity.PICTURE_EXTENTION}"
        )
        if (fileToDelete.delete()) {
            val msg = getString(R.string.picture_delete)
            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        } else {
            val msg = getString(R.string.ERROR)
            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Return back arrow to go to previous activity
     * @author Mathieu Castera
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            deleteCurrentPicture()
            finish()
            true
        } else false
    }

    /**
     * Save the current picture
     * @author Mathieu Castera
     */
    private fun savePicture() {
        val descriptionText = findViewById<EditText>(R.id.editTextDescription)
        val saveButton = findViewById<Button>(R.id.saveButton)
        saveButton.setOnClickListener {
            val textWithSpace = descriptionText.text.toString()
            if (textWithSpace != "") {
                val text = textWithSpace.replace(" ", "_")

                val oldName =
                    PictureActivity.NAME_CURRENT_PICTURE // Replace with the actual old name of the picture file
                val newName =
                    "${PictureActivity.NAME_CURRENT_PICTURE}_${text}" // Replace with the new name you want to give to the picture file
                val dailyPicturesDir = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    MainActivity.PICTURES_FOLDER
                )
                val oldFile =
                    File(dailyPicturesDir, "${oldName}${PictureActivity.PICTURE_EXTENTION}")
                val newFile =
                    File(dailyPicturesDir, "${newName}${PictureActivity.PICTURE_EXTENTION}")

                if (oldFile.exists()) {
                    if (oldFile.renameTo(newFile)) {
                        PictureActivity.NAME_CURRENT_PICTURE = newName
                        val msg = getString(R.string.picture_save_with_description)
                        Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    } else {
                        val msg = getString(R.string.change_description_failed)
                        Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val msg = getString(R.string.missing_file_description_failed)
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                }
            } else {
                val msg = getString(R.string.picture_save_no_description)
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()

            }

            finish()
        }

    }
}