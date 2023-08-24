package com.clickaday

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.clickaday.R.string.picture_taken_hours_add
import java.io.File
import java.time.LocalDateTime

import com.clickaday.USRTools as Tools

class DisplayImageTools {
    companion object {

        fun getLastPicture(dirPicture: File): File? {
            if (dirPicture.exists() && !dirPicture.listFiles()?.isEmpty()!!) {
                val listSize = dirPicture.listFiles()!!.size
                var lastFile = dirPicture.listFiles()?.get(dirPicture.listFiles()?.size?.minus(1)!!)
                var namePicture = lastFile?.name!!

                var i = 1
                while (namePicture.contains(".trashed") && i < listSize) {
                    lastFile =
                        dirPicture.listFiles()?.get(dirPicture.listFiles()?.size?.minus(1 + i)!!)
                    namePicture = lastFile?.name!!
                    i += 1
                }
                return if (i == listSize && namePicture.contains(".trashed"))
                    null
                else
                    lastFile
            } else return null
        }

        /**
         * Display last image of the day (big one)
         * @author Mathieu Castera
         */
        @SuppressLint("SetTextI18n") // supress error for static text implementation
        fun displayDayImage(act: Activity, titlePicture: TextView, dir: File) {

            val pictureView = act.findViewById<ImageView>(R.id.picture_view)
            val lastFile = getLastPicture(dir)


            if (lastFile!!.name.contains(".trashed")) {
                titlePicture.text = act.getString(R.string.picture_moved_empty_folder)
                return
            }
            val tabDate = Tools.getDateNamedPicture(lastFile.name)
            if (Tools.isDoneToday(tabDate)) {
                displayImage(pictureView, lastFile)
                val formattedHour = String.format("%02dH %02dmin", tabDate[3], tabDate[4])
                titlePicture.text = act.getString(picture_taken_hours_add) + formattedHour

            } else {
                val formattedDate = String.format(
                    "%02d/%02d/%d à %02dH %02dmin",
                    tabDate[2],
                    tabDate[1],
                    tabDate[0],
                    tabDate[3],
                    tabDate[4]
                )
                val startTime =
                    LocalDateTime.of(tabDate[0], tabDate[1], tabDate[2], tabDate[3], tabDate[4])
                titlePicture.text =
                    act.getString(R.string.picture_not_taken_hours_add) + formattedDate + act.getString(
                        R.string.picture_not_taken_since_a_while
                    ) + Tools.getTimeDifference(
                        startTime, LocalDateTime.now()
                    )
            }
        }

        /**
         * Display images in the calendar
         * @author Mathieu Castera
         */
        fun displayHistoricImages(act: Activity) {
            val verifImage = Array(7) { false }.toBooleanArray()
            val pictureViewMon = act.findViewById<ImageView>(R.id.history_pictures_view_mon)
            val pictureViewTue = act.findViewById<ImageView>(R.id.history_pictures_view_tue)
            val pictureViewWed = act.findViewById<ImageView>(R.id.history_pictures_view_wed)
            val pictureViewThu = act.findViewById<ImageView>(R.id.history_pictures_view_thu)
            val pictureViewFri = act.findViewById<ImageView>(R.id.history_pictures_view_fri)
            val pictureViewSat = act.findViewById<ImageView>(R.id.history_pictures_view_sat)
            val pictureViewSun = act.findViewById<ImageView>(R.id.history_pictures_view_sun)

            val listsize = MainActivity.IMAGE_DIR.listFiles()!!.size

            for (i in 0 until listsize) {
                val currentFile = MainActivity.IMAGE_DIR.listFiles()
                    ?.get(MainActivity.IMAGE_DIR.listFiles()?.size?.minus(i + 1)!!)
                val namePicture = currentFile?.name!!

                if (!namePicture.contains(".trashed")) {
                    val tabDate = Tools.getDateNamedPicture(namePicture)
                    if (Tools.isSameWeekOfYear(tabDate[0], tabDate[1], tabDate[2])) {
                        val numToday = Tools.getDayOfWeek(tabDate[0], tabDate[1], tabDate[2])
                        if (!verifImage[numToday - 1]) {
                            verifImage[numToday - 1] = true
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
                                    act,
                                    "Error while updating the historic table",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    } else {
                        break
                    }
                }
            }
        }

        /**
         * Correct the orientation of a picture (based on the stored picture)
         * @author Mathieu Castera
         */
        private fun correctOrientation(currentFile: File): Bitmap? {
            val bitmap = BitmapFactory.decodeFile(currentFile.path)

// Récupérer l'orientation de l'image à l'aide de ExifInterface
            val exif = ExifInterface(currentFile.path)
            val orientation =
                exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED
                )

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
         * Function to rotate the bitmap format pictures
         * @author Mathieu Castera
         */
        private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(degrees)
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }

        fun displayImage(pictureView: ImageView, picture: File?) {
            if (picture != null) {
                if (picture.exists()) {
                    val bitmap = correctOrientation(picture)
                    pictureView.setImageBitmap(bitmap)
                }
            } else {
                //pictureView.setImageBitmap()
            }
        }
    }}
