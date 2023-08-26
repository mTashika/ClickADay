package com.clickaday

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import java.io.File
import com.clickaday.DisplayImageTools as ImageTools


class MainActivity : AppCompatActivity(),Interfaces.EnableButtonListener {
    companion object {
        //static objects (can be use without create an instantiation of this class)
        private const val PICTURES_FOLDER = "Daily"
        const val NAME_PICTURES_FOLDER_TMP = "Tmp_Daily"
        val IMAGE_DIR = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            PICTURES_FOLDER
        )
        val IMAGE_DIR_TMP = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            NAME_PICTURES_FOLDER_TMP
        )
        private val PERMISSIONS =
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES)

    }

    private lateinit var photoButton: Button
    private var permissionsResult: Boolean = false

    /**
     * OnCreate function - MainActivity
     * @author Mathieu Castera
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        photoButton = findViewById(R.id.PictureActivityButton)


        requestPermissions.launch(PERMISSIONS)
        permissionsResult = PreferencesTools.getPermissionsResult(this)

        setPictureButtonListener()

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.parameter_menu, menu)
        return true
    }


    /**
     * Return back arrow function
     * @author Mathieu Castera
     * @param item Menu item selected by the user
     * @return true if the item was manage
     *
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }

            R.id.settings_item -> {
                photoButton.isEnabled = false
                val parameterFrag:Fragment = ParameterFragmentMenu()
                USRTools.launchFragment(parameterFrag,R.id.frame_layout_main_act, ParameterFragmentMenu.TAG_FRAG ,supportFragmentManager)

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    /**
     * On activity resume
     * @author Mathieu Castera
     */
    override fun onResume() {
        super.onResume()
        displayImages()
    }

    private fun setPictureButtonListener() {
        photoButton.setOnClickListener {
            if (permissionsResult) {
                goToPictureActivity()
            } else {
                Toast.makeText(this, "You need permissions to take a picture", Toast.LENGTH_LONG)
                    .show()
            }
        }

    }

    override fun enablePhotoBtn() {
        photoButton.isEnabled = true
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
                PreferencesTools.savePermissionsResult(this, true)
            } else {
                // Handle denied permissions or individual permission cases
                if (!cameraPermissionGranted || !readMediaPermissionGranted) {
                    Toast.makeText(
                        this,
                        "You need both Camera and Storage permission to use ClickADay",
                        Toast.LENGTH_LONG
                    ).show()
                    PreferencesTools.savePermissionsResult(this, false)
                    finish()
                }
            }
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

        if (!IMAGE_DIR.exists()) {
            // Créer le dossier s'il n'existe pas
            IMAGE_DIR.mkdirs()
        }

        if (IMAGE_DIR.exists() && IMAGE_DIR.listFiles()!!.isNotEmpty()) {
            ImageTools.displayDayImage(this, titlePicture, IMAGE_DIR)
            ImageTools.displayHistoricImages(this)
        }
        //The file is empty
        else {
            titlePicture.text = getString(R.string.picture_moved_empty_folder)
        }
    }
}