package com.clickaday


import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
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

        ImageTools.displayImage(pictureView, ImageTools.getLastPicture(MainActivity.IMAGE_DIR_TMP))

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
            deleteTmpFolder()
            finish()
        }
    }

    /**
     * Delete the picture (saved in the folder)
     * @author Mathieu Castera
     */
    private fun deleteTmpFolder() {
        val folderToDelete = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            MainActivity.NAME_PICTURES_FOLDER_TMP
        )
        folderToDelete.deleteRecursively()
    }

    /**
     * Return back arrow to go to previous activity
     * @author Mathieu Castera
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            deleteTmpFolder()
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
                var text = textWithSpace.replace(" ", "_")
                text = USRTools.sanitizeFileName(text)

                val oldName =
                    PictureActivity.NAME_CURRENT_PICTURE // Replace with the actual old name of the picture file
                val newName =
                    "${PictureActivity.NAME_CURRENT_PICTURE}_${text}" // Replace with the new name you want to give to the picture file
                val oldFile =
                    File(MainActivity.IMAGE_DIR_TMP, "${oldName}${PictureActivity.PICTURE_EXTENTION}")


                if (oldFile.exists() && MainActivity.IMAGE_DIR.exists()) {
                    if (saveFinalPicture(
                            MainActivity.IMAGE_DIR_TMP,
                            MainActivity.IMAGE_DIR,
                            "${oldName}${PictureActivity.PICTURE_EXTENTION}",
                            "${newName}${PictureActivity.PICTURE_EXTENTION}"
                        )
                    ) {
                        PictureActivity.NAME_CURRENT_PICTURE = newName
                        deleteTmpFolder()
                        val lastfile = ImageTools.getLastPicture(MainActivity.IMAGE_DIR)
                        if (lastfile!!.name.toString() =="${newName}${PictureActivity.PICTURE_EXTENTION}"){
                        val msg = getString(R.string.picture_save_with_description)
                        Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()}
                        else{
                            val msg = "Wrong File Name"
                            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()

                        }
                    }
                } else {
                    val msg = getString(R.string.missing_file_description_failed)
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                }
            }
            finish()
        }

    }

    private fun saveFinalPicture(
        tmpFolder: File,
        finalFolder: File,
        oldFileName: String,
        newFileName: String
    ): Boolean {
        // Assurez-vous que le fichier source existe
        val sourceFile = File(tmpFolder, oldFileName)
        if (!sourceFile.exists() || !sourceFile.isFile) {
            // Gérer le cas où le fichier source n'existe pas
            return false
        }

        // Vérifier si le dossier de destination existe, sinon le créer
        if (!finalFolder.exists()) {
            finalFolder.mkdirs()
        }

        // Définir les métadonnées pour le fichier à enregistrer dans MediaStore
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, newFileName)
            put(
                MediaStore.Images.Media.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + "/${finalFolder.name}"
            )
        }

        val resolver = this.contentResolver

        // Insérer le fichier dans MediaStore et obtenir l'URI du fichier enregistré
        val imageUri: Uri? =
            resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        imageUri?.let {
            // Ouvrir un flux de sortie pour écrire les données de l'image dans le fichier enregistré
            val outputStream = resolver.openOutputStream(imageUri)
            outputStream?.use { stream ->
                // Lire les données du fichier source et les écrire dans le fichier enregistré
                sourceFile.inputStream().use { input ->
                    input.copyTo(stream)
                }
            }
            return true
        }

        return false
    }


}