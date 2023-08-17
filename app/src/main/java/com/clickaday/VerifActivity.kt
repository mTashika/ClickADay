package com.clickaday


import android.os.Bundle
import android.os.Environment
import android.provider.ContactsContract.CommonDataKinds.Im
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.io.File

import com.clickaday.DisplayImageTools as ImageTools

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
        val pictureView = findViewById<ImageView>(R.id.picture_view_verif)
        val directory = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            MainActivity.PICTURES_FOLDER
        )

        ImageTools.displayImage(pictureView,ImageTools.getLastPicture(directory))

        //------ Listener ------
        savePictureListener()
        returnPictureActivityListener()
    }

    /**
     * Return to the picture activity
     * @author Mathieu Castera
     */
    private fun returnPictureActivityListener() {
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
    private fun savePictureListener() {
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