package de.feine_medien.flohmarkt.util

import java.text.SimpleDateFormat
import java.util.*


class DateUtils {

    companion object {

        fun getTime(s: String): String {
            var time = s
            if (s.count() < 6) {
                time = StringBuffer(time).insert(0, "0").toString()
            }

            time = time.dropLast(2)
            time = StringBuffer(time).insert(2, ":").toString()

            return time
        }

        fun getDayAsIntegerFromTimestamp(timeStamp: Long?): Int {
            val dayString = SimpleDateFormat("dd", Locale.GERMANY).format(timeStamp?.let { Date(it) })

            return Integer.parseInt(dayString)
        }

        fun getMonthAsIntegerFromTimestamp(timeStamp: Long?): Int {
            val monthString = SimpleDateFormat("MM", Locale.GERMANY).format(timeStamp?.let { Date(it) })

            return Integer.parseInt(monthString) - 1
        }

        fun getYearAsIntegerFromTimestamp(timeStamp: Long?): Int {
            val yearString = SimpleDateFormat("yyyy", Locale.GERMANY).format(timeStamp?.let { Date(it) })

            return Integer.parseInt(yearString)
        }
    }
}