package com.clickaday

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File

import com.clickaday.DisplayImageTools as ImageTools

class MainActivity : AppCompatActivity() {
    companion object {
        //static objects (can be use without create an instantiation of this class)
        const val PICTURES_FOLDER = "Daily"
        val dir_pic = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            PICTURES_FOLDER
        )
    }

    /**
     * OnCreate function - MainActivity
     * @author Mathieu Castera
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val photoButton = findViewById<Button>(R.id.PictureActivityButton)

        requestPermissionLauncher()
        val permissionsResult = getPermissionsResult()


        photoButton.setOnClickListener {
            if (permissionsResult) {
                goToPictureActivity()
            }
            else {
                Toast.makeText(this,"You need permissions to take a picture",Toast.LENGTH_LONG).show()
            }
        }

    }

    /**
     * Request camera and read media image permission
     * @author Mathieu Castera
     */
    private fun requestPermissionLauncher() {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_MEDIA_IMAGES
        )
        requestPermissions.launch(permissions)
    }

    /**
     * Request Camera and read Media permissions.
     * @author Mathieu Castera
     * @return granted permissionsResult = true or finish the activity
     */
    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
        { permissions: Map<String, Boolean> ->
            val cameraPermissionGranted = permissions[Manifest.permission.CAMERA] ?: false
            val readMediaPermissionGranted =
                permissions[Manifest.permission.READ_MEDIA_IMAGES] ?: false

            if (cameraPermissionGranted && readMediaPermissionGranted) {
                // Call your function here that requires both permissions
                savePermissionsResult(true)
            } else {
                // Handle denied permissions or individual permission cases
                if (!cameraPermissionGranted || !readMediaPermissionGranted) {
                    Toast.makeText(
                        this,
                        "You need both Camera and Storage permission to use ClickADay",
                        Toast.LENGTH_LONG
                    ).show()
                    savePermissionsResult(false)
                    finish()
                }
            }
        }


    /**
     * Return back arrow function
     * @author Mathieu Castera
     * @param item Menu item selected by the user
     * @return true if the item was manage
     *
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else false
    }

    /**
     * On activity resume
     * @author Mathieu Castera
     */
    override fun onResume() {
        super.onResume()
        displayImages()
    }

    /**
     * goTo PictureActivity
     * @author Mathieu Castera
     */
    private fun goToPictureActivity() {
        val intent = Intent(this, PictureActivity::class.java)
        startActivity(intent)
    }

    /**
     * Display images on the main activity (big and small ones)
     * @author Mathieu Castera
     */
    private fun displayImages() {
        val titlePicture = findViewById<TextView>(R.id.picture_title)

        if (dir_pic.exists() && dir_pic.listFiles()!!.isNotEmpty()) {
            ImageTools.displayDayImage(this, titlePicture, dir_pic)
            ImageTools.displayHistoricImages(this)
        }
        //The file is empty
        else {
            titlePicture.text = getString(R.string.picture_moved_empty_folder)
        }
    }

    //------ Preferences ------
    // Fonction pour sauvegarder la valeur de permissionsResult dans les SharedPreferences
    private fun savePermissionsResult(permissionsResult: Boolean) {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("permissionsResult", permissionsResult)
        editor.apply()
    }

    // Fonction pour récupérer la valeur de permissionsResult depuis les SharedPreferences
    private fun getPermissionsResult(): Boolean {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        return sharedPreferences.getBoolean("permissionsResult", false)
    }


}