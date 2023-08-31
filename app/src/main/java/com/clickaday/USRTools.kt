package com.clickaday


import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import java.time.Duration
import java.time.LocalDateTime
import java.time.Period
import java.util.Calendar
import java.util.Date

class USRTools {
    companion object {
        /**
         * Launch a fragment in the specified layout
         */
        fun launchFragment(
            fragment: Fragment,
            idLayout: Int,
            tagFrag: String,
            fragmentManager: FragmentManager
        ) {
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(idLayout, fragment, tagFrag)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        fun buildPasswordFragment(value: Boolean): Fragment {
            val fragment = PasswordFragment()
            val args = Bundle()
            args.putBoolean(PasswordFragment.TAG_BOOL, value)
            fragment.arguments = args
            return fragment
        }

        /**
         * Close a fragment with his tag
         */
        fun closeFragmentWithTag(
            tagFrag: String,
            fragmentManager: FragmentManager
        ) {
            val fragment = fragmentManager.findFragmentByTag(tagFrag)
            if (fragment != null) {
                val transaction = fragmentManager.beginTransaction()
                transaction.remove(fragment)
                transaction.commit()
            }
        }

        fun launchPasswordCheckFrag(supportFragManager: FragmentManager) {
            val fragment = buildPasswordFragment(false)
            launchFragment(
                fragment,
                R.id.frame_layout_main_act2,
                PasswordFragment.TAG_FRAG,
                supportFragManager
            )
        }


        /**
         * Get the day of the week with year, month and day value
         * @author Internet - Mathieu Castera
         */

        fun getDayOfWeek(year: Int, month: Int, day: Int): Int {
            val calendar = Calendar.getInstance()
            calendar.set(year, month - 1, day) // Month is zero-based in Calendar

            var dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - calendar.firstDayOfWeek + 1
            if (dayOfWeek <= 0) {
                dayOfWeek += 7
            }

            return dayOfWeek
        }

        /**
         * Get the time difference between two Local date time
         * @author Internet - Mathieu Castera
         */
        fun getTimeDifference(start: LocalDateTime, end: LocalDateTime): String {
            val duration = Duration.between(start, end)
            val period = Period.between(start.toLocalDate(), end.toLocalDate())

            val years = period.years
            val months = period.months
            val days = period.days
            val hours: Int
            val minutes: Int
            try {
                hours = duration.toHours().toInt()
                minutes = (duration.toMinutes() % 60).toInt()
            } catch (e: Exception) {
                return "Impossible de calculer l'heure et les minutes d'écarts"

            }
            var returnText = ""

            if (years == 1) returnText += "$years an "
            else if (years > 1) returnText += "$years ans "

            if (months > 1) returnText += "$months mois "

            if (days == 1) returnText += "$days jour "
            else if (days > 1) returnText += "$days jours "

            if (hours == 1) returnText += "$hours heure "
            else if (hours > 1) returnText += "$hours heures "

            if (minutes > 0) returnText += "$minutes min"

            return returnText
        }

        /**
         * Get the date format name for a picture
         * @author Mathieu Castera
         */
        fun getDateNamedPicture(pictureName: String): IntArray {
            val year = pictureName.substring(2, 6).toInt()
            val month = pictureName.substring(7, 9).toInt()
            val day = pictureName.substring(10, 12).toInt()
            val hour = pictureName.substring(13, 15).toInt()
            val min = pictureName.substring(16, 18).toInt()
            val sec = pictureName.substring(19, 21).toInt()

            return intArrayOf(year, month, day, hour, min, sec)
        }

        /**
         * Function to know if a picture has been taken today
         * @author Mathieu Castera
         */
        fun isDoneToday(tabDate: IntArray): Boolean { //FALSE: Take picture/ TRUE: display the good one
            val currentDate = Date()
            val calendar = Calendar.getInstance()
            calendar.time = currentDate
            return !(calendar.get(Calendar.YEAR) > tabDate[0] || calendar.get(Calendar.MONTH) > tabDate[1] || calendar.get(
                Calendar.DAY_OF_MONTH
            ) > tabDate[2])
        }

        /**
         * Function to check the week of the year for the calendar picture
         * @author Mathieu Castera
         */
        fun isSameWeekOfYear(year: Int, month: Int, day: Int): Boolean {
            val givenDate = Calendar.getInstance()
            givenDate.set(year, month - 1, day) // Month is zero-based in Calendar

            val currentDate = Calendar.getInstance()

            val givenWeekOfYear = givenDate.get(Calendar.WEEK_OF_YEAR)
            val currentWeekOfYear = currentDate.get(Calendar.WEEK_OF_YEAR)

            return givenWeekOfYear == currentWeekOfYear && givenDate.get(Calendar.YEAR) == currentDate.get(
                Calendar.YEAR
            )
        }

        fun sanitizeFileName(fileName: String): String {
            val invalidCharsRegex = """[#%&{}\\<>*/$'":@+]""".toRegex()
            return fileName.replace(invalidCharsRegex, "_")
        }

        fun isValidDirectoryName(input: String): Boolean {
            val pattern = Regex("^[a-zA-Z0-9_-]*\$")
            return pattern.matches(input)
        }

        fun getPictureDescription(txt: String): String {
            if (txt.length <=
                PictureActivity.FILENAME_FORMAT.length + PictureActivity.PICTURE_FIRST_CHARACTERE.length + 1
            ) {
                return ""
            }
            val newTxt = txt.substring(
                PictureActivity.FILENAME_FORMAT.length + PictureActivity.PICTURE_FIRST_CHARACTERE.length + 1
            )
            return newTxt.replace("_", " ")
                .replaceFirstChar { it.uppercase() }
        }

    }

}
