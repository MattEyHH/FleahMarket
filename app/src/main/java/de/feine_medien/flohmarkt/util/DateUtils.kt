package de.feine_medien.flohmarkt.util

import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class DateUtils {

    companion object {
        fun getDateFromTimestampString(timeStamp: Long?): String {
            val cal = Calendar.getInstance(Locale.GERMANY)
            cal.timeInMillis = timeStamp!! * 1000L

            return DateFormat.format("dd.MM.yyyy hh:mm", cal).toString()
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