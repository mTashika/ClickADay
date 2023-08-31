package com.clickaday

import android.annotation.SuppressLint
import android.app.Activity
import android.renderscript.*
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.clickaday.USRTools
import jp.wasabeef.glide.transformations.BlurTransformation
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
        fun displayDayImage(act: Activity, dir: File,dayPictureView: ImageView) {

            val titlePicture = act.findViewById<TextView>(R.id.picture_title)
            val timePicture = act.findViewById<TextView>(R.id.textview_time_day_pic)
            val lastFile = getLastPicture(dir)
            val tabDate = Tools.getDateNamedPicture(lastFile!!.name)


            if (lastFile.name.contains(".trashed")) {
                titlePicture.text = act.getString(R.string.picture_moved_empty_folder)
                timePicture.text = ""
                return
            }

            if (Tools.isDoneToday(tabDate)) {
                val formattedHour = String.format("%02dH %02dmin", tabDate[3], tabDate[4])

                displayImage(dayPictureView, lastFile, PreferencesTools.getPrefBool(act,PreferencesTools.PREF_BLUR_IMG))

                if (PreferencesTools.getPrefBool(
                        act,
                        PreferencesTools.PREF_DESCRIPTION_IMG
                    )
                ) {//With description
                    timePicture.text = formattedHour
                    titlePicture.text =
                        USRTools.getPictureDescription(lastFile.nameWithoutExtension)
                    titlePicture.gravity = Gravity.START

                } else {//without description
                    timePicture.text = ""
                    titlePicture.text = formattedHour
                    titlePicture.gravity = Gravity.CENTER
                }


            } else {//no picture
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
                timePicture.text = ""
                titlePicture.text =
                    act.getString(R.string.picture_not_taken_hours_add) + formattedDate + act.getString(
                        R.string.picture_not_taken_since_a_while
                    ) + Tools.getTimeDifference(
                        startTime, LocalDateTime.now()
                    )
                titlePicture.gravity = Gravity.CENTER
            }
        }

        /**
         * Display images in the calendar
         * @author Mathieu Castera
         */
        fun displayHistoricImages(act: Activity,Views:List<ImageView>) {
            val verifImage = Array(7) { false }.toBooleanArray()
            val isBlur = PreferencesTools.getPrefBool(act,PreferencesTools.PREF_BLUR_IMG)

            val listsize = MainActivity.FOLDER_PICTURE.listFiles()!!.size

            for (i in 0 until listsize) {
                val currentFile = MainActivity.FOLDER_PICTURE.listFiles()
                    ?.get(MainActivity.FOLDER_PICTURE.listFiles()?.size?.minus(i + 1)!!)
                val namePicture = currentFile?.name!!

                if (!namePicture.contains(".trashed")) {
                    val tabDate = Tools.getDateNamedPicture(namePicture)
                    if (Tools.isSameWeekOfYear(tabDate[0], tabDate[1], tabDate[2])) {
                        val numToday = Tools.getDayOfWeek(tabDate[0], tabDate[1], tabDate[2])
                        if (!verifImage[numToday - 1]) {
                            verifImage[numToday - 1] = true
                            when (numToday) {
                                1 -> displayImage(Views[0], currentFile, isBlur)
                                2 -> displayImage(Views[1], currentFile, isBlur)
                                3 -> displayImage(Views[2], currentFile, isBlur)
                                4 -> displayImage(Views[3], currentFile, isBlur)
                                5 -> displayImage(Views[4], currentFile, isBlur)
                                6 -> displayImage(Views[5], currentFile, isBlur)
                                7 -> displayImage(Views[6], currentFile, isBlur)
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

        fun setEmptyImgMainAct(dayPictureView : ImageView,historicViews:List<ImageView>) {
            dayPictureView.setImageResource(0)
            for (view in historicViews){
                view.setImageResource(0)
            }

        }

        fun displayImage(pictureView: ImageView, picture: File?, isBlur: Boolean) {
            if (picture!!.exists()) {
                if (isBlur) {
                    Glide.with(pictureView)
                        .load(picture)
                        .fitCenter()
                        .apply(
                            RequestOptions.bitmapTransform(
                                BlurTransformation(
                                    25,
                                    3
                                )
                            )
                        ) // Apply blur effect here
                        .into(pictureView)

                } else {
                    Glide.with(pictureView)
                        .load(picture)
                        .fitCenter()
                        .into(pictureView)

                }
            }
        }

    }
}
