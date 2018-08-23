package de.feine_medien.flohmarkt.util

import java.text.SimpleDateFormat
import java.util.*


class DateUtils {

    companion object {

        fun getTime(s: String): String {
            var timeInt = 0
            try {
                timeInt = s.split(":")[1].replace("\"", "").toInt()
            } catch (e: Exception) { }
            timeInt /= 10000

            return timeInt.toString().padStart(2, '0') + ":00"
        }

        fun getDayAsIntegerFromTimestamp(timeStamp: Long?): Int {
            val dayString = SimpleDateFormat("dd", Locale.GERMANY).format(timeStamp?.let { Date(it) })

            return Integer.parseInt(dayString)
        }

        fun getMonthAsIntegerFromTimestamp(timeStamp: Long?): Int {
            val dayString = SimpleDateFormat("MM", Locale.GERMANY).format(timeStamp?.let { Date(it) })

            return Integer.parseInt(dayString)
        }

        fun getYearAsIntegerFromTimestamp(timeStamp: Long?): Int {
            val dayString = SimpleDateFormat("yyyy", Locale.GERMANY).format(timeStamp?.let { Date(it) })

            return Integer.parseInt(dayString)
        }
    }
}