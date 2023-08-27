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


class MainActivity : AppCompatActivity(), Interfaces.ReturnToMainActivity {
    companion object {
        //static objects (can be use without create an instantiation of this class)
        lateinit var FOLDER_NAME_PICTURE: String
        lateinit var FOLDER_NAME_TMP_PICTURE: String
        lateinit var FOLDER_PICTURE: File
        lateinit var FOLDER_PICTURE_TMP: File
        var IS_PASSWORD: Boolean = false
        var IS_BLUR: Boolean = false
        var IS_DESCRIPTION: Boolean = false

        private val PERMISSIONS =
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES)

    }

    //view
    private lateinit var photoButton: Button

    //permission
    private var permissionsResult: Boolean = false

    /**
     * OnCreate function - MainActivity
     * @author Mathieu Castera
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //ToolBar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //View
        photoButton = findViewById(R.id.PictureActivityButton)

        //Preferences
        initializePreferences()

        //Permissions
        requestPermissions.launch(PERMISSIONS)
        permissionsResult = PreferencesTools.getPrefBool(this, PreferencesTools.PREF_PERMISSIONS)

        initialiseMainUI()
    }

    private fun initializePreferences() {
        // ------ FOLDER PATH ------
        //Get the current save folder (in preferences)
        val curFold = PreferencesTools.getPrefStr(this, PreferencesTools.PREF_FOLDER_PICT)
        if (curFold == null) {
            //ask new folder (launch the folder fragment)
            val parameterFrag: Fragment = ChangeFolderFragment()
            USRTools.launchFragment(
                parameterFrag,
                R.id.frame_layout_main_act2,
                ChangeFolderFragment.TAG_FRAG,
                supportFragmentManager
            )
        }
        updateConst()


    }

    private fun updateConst() {
        val curFold = PreferencesTools.getPrefStr(this, PreferencesTools.PREF_FOLDER_PICT)
        if (curFold != null) {
            FOLDER_NAME_PICTURE = curFold
            FOLDER_NAME_TMP_PICTURE = FOLDER_NAME_PICTURE + "_tmp"
            FOLDER_PICTURE = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                FOLDER_NAME_PICTURE
            )
            FOLDER_PICTURE_TMP = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                FOLDER_NAME_TMP_PICTURE
            )
        }

        // ------ PASSWORD ------
        IS_PASSWORD = PreferencesTools.getPrefBool(this, PreferencesTools.PREF_PASSWORD)

        // ------ BLUR ------
        IS_BLUR = PreferencesTools.getPrefBool(this, PreferencesTools.PREF_BLUR_IMG)

        // ------ DESCRIPTION ------
        IS_DESCRIPTION = PreferencesTools.getPrefBool(this, PreferencesTools.PREF_DESCRIPTION_IMG)

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
                val parameterFrag: Fragment = ParameterFragmentMenu()

                USRTools.launchFragment(
                    parameterFrag,
                    R.id.frame_layout_main_act,
                    ParameterFragmentMenu.TAG_FRAG,
                    supportFragmentManager
                )

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
        if (PreferencesTools.getPrefStr(this, PreferencesTools.PREF_FOLDER_PICT) != null) {
            displayImages()
        }

    }

    private fun initialiseMainUI() {
        photoButton.setOnClickListener {
            if (permissionsResult) {
                goToPictureActivity()
            } else {
                Toast.makeText(this, "You need permissions to take a picture", Toast.LENGTH_LONG)
                    .show()
            }
        }

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
                PreferencesTools.savePrefBool(this, true, PreferencesTools.PREF_PERMISSIONS)
            } else {
                // Handle denied permissions or individual permission cases
                if (!cameraPermissionGranted || !readMediaPermissionGranted) {
                    Toast.makeText(
                        this,
                        "You need both Camera and Storage permission to use ClickADay",
                        Toast.LENGTH_LONG
                    ).show()
                    PreferencesTools.savePrefBool(this, false, PreferencesTools.PREF_PERMISSIONS)
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

        if (!FOLDER_PICTURE.exists()) {
            // Créer le dossier s'il n'existe pas
            FOLDER_PICTURE.mkdirs()
        }

        if (FOLDER_PICTURE.exists() && FOLDER_PICTURE.listFiles()!!.isNotEmpty()) {
            ImageTools.displayDayImage(this, titlePicture, FOLDER_PICTURE)
            ImageTools.displayHistoricImages(this)
        }
        //The file is empty
        else {
            ImageTools.setEmptyImgMainAct(this)
            titlePicture.text = getString(R.string.picture_moved_empty_folder)
        }
    }


    //Interface
    override fun enablePhotoBtn() {
        photoButton.isEnabled = true
    }

    override fun launchDisplayImg() {
        updateConst()
        displayImages()
    }
}