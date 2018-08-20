package de.feine_medien.flohmarkt.util

import android.content.Context

data class PreferencesHandler(val context: Context) {

    private val PREF_FIRST_APP_START = "firstAppStart"
    private val PREFS_FILENAME = "de.feine_medien.flohmarkt.prefs"
    private val preferences = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)

    fun isFirstAppStart() : Boolean {
        return preferences.getBoolean(PREF_FIRST_APP_START, true)
    }

    fun putFirstAppStart(isFirstAppStart: Boolean) {
        val editor = preferences.edit()
        editor.putBoolean(PREF_FIRST_APP_START, isFirstAppStart)
        editor.apply()
    }
}