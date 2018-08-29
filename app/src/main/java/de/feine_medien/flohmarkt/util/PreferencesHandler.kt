package de.feine_medien.flohmarkt.util

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.feine_medien.flohmarkt.model.Market

data class PreferencesHandler(val context: Context) {

    private val PREF_SAVED_MARKETS = "savedMarkets"
    private val PREFS_FILENAME = "de.feine_medien.flohmarkt.prefs"
    private val preferences = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)

    fun getSavedMarkets(): ArrayList<Market> {
        val savedMarkets = ArrayList<Market>()
        val market: Market
        val gson = Gson()
        val json = preferences.getString(PREF_SAVED_MARKETS, null)
        val type = object : TypeToken<Market>() {}.type

        if (json.isNullOrEmpty()) {
            return arrayListOf()
        }

        market = gson.fromJson(json, type)
        savedMarkets.add(market)

        return savedMarkets
    }

    fun putMarket(market: Market) {
        val editor = preferences.edit()
        val gson = Gson()
        val jsonString = gson.toJson(market)
        editor.putString(PREF_SAVED_MARKETS, jsonString)
        editor.apply()
    }
}