package com.clickaday

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.time.Duration
import java.time.LocalDateTime
import java.time.Period
import java.util.*

class MainActivity : AppCompatActivity() {
    companion object {
        const val PICTURES_FOLDER = "Daily"
        val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), PICTURES_FOLDER)
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions: Map<String, Boolean> ->
        val cameraPermissionGranted = permissions[Manifest.permission.CAMERA] ?: false
        val readMediaPermissionGranted = permissions[Manifest.permission.READ_MEDIA_IMAGES] ?: false

        if (cameraPermissionGranted && readMediaPermissionGranted) {
            // Call your function here that requires both permissions
            goToPictureActivity()
        } else {
            // Handle denied permissions or individual permission cases
            if (!cameraPermissionGranted || !readMediaPermissionGranted){
                Toast.makeText(
                    this,
                    "You need both Camera and Storage permission to use ClickADay",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        requestPermissions()


    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_MEDIA_IMAGES
        )
        requestPermissionLauncher.launch(permissions)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else false
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onResume() {
        super.onResume()
            displayImages()
    }

    private fun goToPictureActivity() {
        val pictureActivityButton = findViewById<Button>(R.id.PictureActivityButton)
        pictureActivityButton.setOnClickListener{

            val intent = Intent(this@MainActivity, PictureActivity::class.java)
            startActivity(intent)
            }
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun displayImages() {
        val titlePicture = findViewById<TextView>(R.id.picture_title)

        if (directory.exists() && directory.listFiles()!!.isNotEmpty()) {

            displayDayImage(titlePicture)
            displayHistoricImages()
        }
        //The file is empty
        else {
            titlePicture.text =getString(R.string.picture_moved_empty_folder)
        }
    }
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun displayDayImage(titlePicture: TextView){
        val pictureView = findViewById<ImageView>(R.id.picture_view)
        var lastFile = directory.listFiles()!![directory.listFiles()?.size?.minus(1)!!]
        var namePicture = lastFile?.name!!
        var i=1
        val listsize = directory.listFiles()!!.size
        while (namePicture.contains(".trashed") && i<listsize) {
            lastFile = directory.listFiles()?.get(directory.listFiles()?.size?.minus(1+i)!!)
            namePicture = lastFile?.name!!
            i+=1
        }
        if (namePicture.contains(".trashed")){
            titlePicture.text =getString(R.string.picture_moved_empty_folder)
            return
        }
        val tabDate = getDateNamedPicture(namePicture)
        if (isDoneToday(tabDate)) {
            val bitmap = correctOrientation(lastFile)
            pictureView.setImageBitmap(bitmap)
            val formattedHour = String.format("%02dH %02dmin" ,tabDate[3], tabDate[4])
            titlePicture.text = getString(R.string.picture_taken_hours_add) + formattedHour
        }

        else  {
            val formattedDate = String.format("%02d/%02d/%d à %02dH %02dmin", tabDate[2], tabDate[1], tabDate[0], tabDate[3], tabDate[4])
            val startTime=LocalDateTime.of(tabDate[0],tabDate[1],tabDate[2],tabDate[3],tabDate[4])
            titlePicture.text = getString(R.string.picture_not_taken_hours_add)+formattedDate+getString(R.string.picture_not_taken_since_a_while)+getTimeDifference(startTime,
                LocalDateTime.now())
        }

    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun displayHistoricImages(){
        val verifImage = Array(7) { false }.toBooleanArray()
        val pictureViewMon = findViewById<ImageView>(R.id.history_pictures_view_mon)
        val pictureViewTue = findViewById<ImageView>(R.id.history_pictures_view_tue)
        val pictureViewWed = findViewById<ImageView>(R.id.history_pictures_view_wed)
        val pictureViewThu = findViewById<ImageView>(R.id.history_pictures_view_thu)
        val pictureViewFri = findViewById<ImageView>(R.id.history_pictures_view_fri)
        val pictureViewSat = findViewById<ImageView>(R.id.history_pictures_view_sat)
        val pictureViewSun = findViewById<ImageView>(R.id.history_pictures_view_sun)

        val listsize = directory.listFiles()!!.size

        for(  i in 0 until listsize){
            var currentFile = directory.listFiles()?.get(directory.listFiles()?.size?.minus(i+1)!!)
            var namePicture = currentFile?.name!!

            if (!namePicture.contains(".trashed")){
                val tabDate = getDateNamedPicture(namePicture)
                if(isSameWeekOfYear(tabDate[0],tabDate[1],tabDate[2])){
                    val numToday=getDayOfWeek(tabDate[0],tabDate[1],tabDate[2])
                    if (!verifImage[numToday-1]){
                        verifImage[numToday-1]=true
                        val bitmap = correctOrientation(currentFile)
                        when (numToday) {
                            1 -> pictureViewMon.setImageBitmap(bitmap)
                            2 -> pictureViewTue.setImageBitmap(bitmap)
                            3 -> pictureViewWed.setImageBitmap(bitmap)
                            4 -> pictureViewThu.setImageBitmap(bitmap)
                            5 -> pictureViewFri.setImageBitmap(bitmap)
                            6 -> pictureViewSat.setImageBitmap(bitmap)
                            7 -> pictureViewSun.setImageBitmap(bitmap)
                            else -> Toast.makeText(
                                this, "Error while updating the historic table",
                                Toast.LENGTH_LONG).show()
                        }
                    }
                }
                else{
                    break
                }
            }
        }
    }
    fun correctOrientation(currentFile: File):Bitmap?{
        val bitmap = BitmapFactory.decodeFile(currentFile.path)

// Récupérer l'orientation de l'image à l'aide de ExifInterface
        val exif = ExifInterface(currentFile.path)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)

// Corriger l'orientation de l'image si nécessaire
        val rotatedBitmap = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90F)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180F)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270F)
            else -> bitmap
        }
        return rotatedBitmap
    }
    // Fonction pour faire pivoter le bitmap
    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
private fun isSameWeekOfYear(year: Int, month: Int, day: Int): Boolean {
    val givenDate = Calendar.getInstance()
    givenDate.set(year, month - 1, day) // Month is zero-based in Calendar

    val currentDate = Calendar.getInstance()

    val givenWeekOfYear = givenDate.get(Calendar.WEEK_OF_YEAR)
    val currentWeekOfYear = currentDate.get(Calendar.WEEK_OF_YEAR)

    return givenWeekOfYear == currentWeekOfYear && givenDate.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR)
}

    private fun getDayOfWeek(year: Int, month: Int, day: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, day) // Month is zero-based in Calendar

        var dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - calendar.firstDayOfWeek + 1
        if (dayOfWeek <= 0) {
            dayOfWeek += 7
        }

        return dayOfWeek
    }



    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun getTimeDifference(start: LocalDateTime, end: LocalDateTime): String {
        val duration = Duration.between(start, end)
        val period = Period.between(start.toLocalDate(), end.toLocalDate())

        val years = period.years
        val months = period.months
        val days = period.days
        var hours=0
        var minutes = 0
        try {
            hours = duration.toHoursPart()
            minutes = duration.toMinutesPart()
        }catch (e :Exception){
            val msg = "Impossible de calculer l'heure et les minutes d'écarts"
            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        }
        var returnText =""

        if (years==1)returnText+="$years an "
        else if (years>1)returnText+="$years ans "

        if (months>1)returnText+="$months mois "

        if (days==1)returnText+="$days jour "
        else if (days>1)returnText+="$days jours "

        if (hours==1)returnText+="$hours heure "
        else if (hours>1)returnText+="$hours heures "

        if (minutes>0)returnText+="$minutes min"

        return returnText
    }

    private fun getDateNamedPicture(pictureName: String): IntArray {
        val year = pictureName.substring(2, 6).toInt()
        val month = pictureName.substring(7, 9).toInt()
        val day = pictureName.substring(10, 12).toInt()
        val hour = pictureName.substring(13, 15).toInt()
        val min = pictureName.substring(16, 18).toInt()
        val sec = pictureName.substring(19, 21).toInt()

        return intArrayOf(year, month, day, hour, min, sec)
    }

    private fun isDoneToday(tabDate: IntArray): Boolean { //FALSE: Take picture/ TRUE: display the good one
        val currentDate = Date()
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        return !(calendar.get(Calendar.YEAR) > tabDate[0] || calendar.get(Calendar.MONTH) > tabDate[1] ||
                calendar.get(Calendar.DAY_OF_MONTH) > tabDate[2])
    }

}