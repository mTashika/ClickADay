package com.clickaday

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.BlurTransformation
import java.io.File
import java.util.Calendar

class BigPictureActivity : AppCompatActivity() {

    private lateinit var imageViewPager: ViewPager
    private lateinit var imageFiles: List<File>
    private lateinit var closeBtn: ImageButton
    private lateinit var topTxt: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.big_picture_activity_layout)
        closeBtn = findViewById(R.id.image_btn_close_big_activity)
        topTxt = findViewById(R.id.top_textview_big_activity)


        val dayInt = intent.getIntExtra("dayInt", -1)
        val weekint = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)
        val listFile = findFiles(dayInt, weekint)
        val isDescription =
            PreferencesTools.getPrefBool(this, PreferencesTools.PREF_DESCRIPTION_IMG)
        val isBlur = PreferencesTools.getPrefBool(this, PreferencesTools.PREF_BLUR_IMG)

        imageFiles = listFile.toList()
        if (imageFiles.isEmpty()) {
            finish()
        }
        if(dayInt == -1){
            topTxt.text=""

        }
        else{
            if (dayInt==Calendar.MONDAY){
                topTxt.text="MONDAY"
            }
            else if (dayInt==Calendar.TUESDAY){
                topTxt.text="TUESDAY"
            }
            else if (dayInt==Calendar.WEDNESDAY){
                topTxt.text="WEDNESDAY"
            }
            else if (dayInt==Calendar.THURSDAY){
                topTxt.text="THURSDAY"
            }
            else if (dayInt==Calendar.FRIDAY){
                topTxt.text="FRIDAY"
            }
            else if (dayInt==Calendar.SATURDAY){
                topTxt.text="SATURDAY"
            }
            else if (dayInt==Calendar.SUNDAY){
                topTxt.text="SUNDAY"
            }

        }

        imageViewPager = findViewById(R.id.imageViewPager)
        val pagerAdapter = EnlargedImagePagerAdapter(
            imageFiles,
            getDescription(imageFiles),
            getTimes(imageFiles),
            isDescription, isBlur
        )
        imageViewPager.adapter = pagerAdapter
        setLinstener()
    }

    private fun setLinstener() {
        closeBtn.setOnClickListener {
            finish()
        }
    }

    private fun findFiles(dayInt: Int, weekInt: Int): List<File> {
        val folder = MainActivity.FOLDER_PICTURE // Folder with all the pictures
        val filesWithMatchingDate = mutableListOf<File>()
        val files = folder.listFiles() ?: emptyArray()

        if (dayInt == -1) {
            for (i in files.size - 1 downTo 0) {
                val file = files[i]
                if (file.name.contains(".trashed")) {
                    continue
                }
                filesWithMatchingDate.add(file)
            }
        } else {

            val calendar = Calendar.getInstance()

            calendar.set(Calendar.WEEK_OF_YEAR, weekInt)
            calendar.set(Calendar.DAY_OF_WEEK, dayInt)
            val aimDayOfYear = calendar.get(Calendar.DAY_OF_YEAR)


            // Iterate through the files in the folder

            for (i in files.size - 1 downTo 0) {
                val file = files[i]
                if (file.name.contains(".trashed")) {
                    continue
                }
                val fileName = file.nameWithoutExtension.substring(
                    PictureActivity.PICTURE_FIRST_CHARACTERE.length,
                    PictureActivity.PICTURE_FIRST_CHARACTERE.length + PictureActivity.FILENAME_FORMAT.length
                )

                val parts = fileName.split("_")
                if (parts.size == 6) {
                    val fileMonth = parts[1].toIntOrNull()
                    val fileDay = parts[2].toIntOrNull()
                    val fileYear = parts[0].toIntOrNull()
                    if (fileMonth != null && fileDay != null && fileYear != null) {
                        calendar.set(Calendar.YEAR, fileYear)
                        calendar.set(Calendar.MONTH, fileMonth - 1)
                        calendar.set(Calendar.DAY_OF_MONTH, fileDay)
                        val pictDayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
                        val pictWeekOfYear = calendar.get(Calendar.WEEK_OF_YEAR)

                        if (pictWeekOfYear != weekInt) {//not the same week
                            break
                        }
                        if (pictDayOfYear == aimDayOfYear) {//same day
                            //Add the file to a list and return the list at the end
                            filesWithMatchingDate.add(file)
                        }


                    }
                }
            }
        }

        return filesWithMatchingDate
    }

    private fun getDescription(files: List<File>): List<String> {
        val returnList = mutableListOf<String>()
        for (file in files) {
            returnList.add(USRTools.getPictureDescription(file.nameWithoutExtension))
        }
        return returnList
    }

    private fun getTimes(files: List<File>): List<String> {
        val returnList = mutableListOf<String>()
        var tabDate: IntArray
        var formattedHour: String

        for (file in files) {
            tabDate = USRTools.getDateNamedPicture(file.name)
            formattedHour = String.format(
                "%02d/%02d/%03d (%02dH:%02d)",
                tabDate[2],
                tabDate[1],
                tabDate[0],
                tabDate[3],
                tabDate[4]
            )
            returnList.add(formattedHour)
        }
        return returnList

    }
}

class EnlargedImagePagerAdapter(
    private val imageFiles: List<File>,
    private val descriptions: List<String>,
    private val times: List<String>,
    private val isDescription: Boolean,
    private val isBlur: Boolean
) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(container.context)
        val itemView = inflater.inflate(R.layout.item_big_image, container, false)

        val imageView = itemView.findViewById<ImageView>(R.id.pagerImageView)
        val descriptionTextView =
            itemView.findViewById<TextView>(R.id.textview_description_big_activity)
        val timeTextView = itemView.findViewById<TextView>(R.id.textview_time_big_activity)
        val imageFile = imageFiles[position]
        if (!isBlur) {
            // Load image using Glide library
            Glide.with(imageView)
                .load(imageFile)
                .fitCenter()
                .into(imageView)

        } else {//with blur
            Glide.with(imageView)
                .load(imageFile)
                .fitCenter()
                .apply(
                    RequestOptions.bitmapTransform(
                        BlurTransformation(
                            25,
                            3
                        )
                    )
                ) // Apply blur effect here
                .into(imageView)
        }


        container.addView(itemView)
        if (isDescription) {
            //With description
            timeTextView.text = times[position]
            descriptionTextView.text = descriptions[position]
            descriptionTextView.gravity = Gravity.START

        } else {//without description
            timeTextView.text = ""
            descriptionTextView.text = times[position]
            descriptionTextView.gravity = Gravity.CENTER
        }
        return itemView
    }


    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }

    override fun getCount(): Int {
        return imageFiles.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }


}