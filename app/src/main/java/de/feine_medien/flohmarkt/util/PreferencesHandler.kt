package de.feine_medien.flohmarkt.util

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.feine_medien.flohmarkt.event.OnAllBookmarksDeletedEvent
import de.feine_medien.flohmarkt.model.Market
import org.greenrobot.eventbus.EventBus

data class PreferencesHandler(val context: Context) {

    private val PREFS_FILENAME = "secret.shared.preferences"
    private val PREF_SAVED_MARKETS = "savedMarkets"
    private val PREF_LAST_SEARCHED_CITY = "lastSearchedCity"
    private val PREF_LAST_SEARCHED_LATLNG = "lastSearchedLatLng"
    private val preferences = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)

    fun getSavedMarkets(): List<Market> {
        val type = object : TypeToken<Market>() {}.type
        val gson = Gson()
        val savedMarkets = mutableListOf<Market>()
        preferences.getStringSet(PREF_SAVED_MARKETS, mutableSetOf()).forEach {
            savedMarkets.add(gson.fromJson(it, type))
        }

        return savedMarkets.distinct()
    }

    fun putMarket(market: Market) {
        val editor = preferences.edit()
        val gson = Gson()
        val jsonString = gson.toJson(market)
        val currentMarkets = preferences.getStringSet(PREF_SAVED_MARKETS, mutableSetOf()).toMutableSet()
        currentMarkets.add(jsonString)
        editor.putStringSet(PREF_SAVED_MARKETS, currentMarkets)

        editor.apply()
    }

    fun getLastSearchedLatLng(): LatLng? {
        val type = object : TypeToken<LatLng>() {}.type
        val gson = Gson()
        val lastLatLngString: String? = preferences.getString(PREF_LAST_SEARCHED_LATLNG, "")

        return gson.fromJson(lastLatLngString, type)
    }

    fun putLastSearchedLatLng(latLng: LatLng) {
        val editor = preferences.edit()
        val gson = Gson()
        val gsonString = gson.toJson(latLng)
        editor.putString(PREF_LAST_SEARCHED_LATLNG, gsonString)

        editor.apply()
    }

    fun getLastSearchedCity(): String {
        return preferences.getString(PREF_LAST_SEARCHED_CITY, "")
    }

    fun putLastSearchedCity(city: String) {
        val editor = preferences.edit()
        editor.putString(PREF_LAST_SEARCHED_CITY, city)

        editor.apply()
    }

    @SuppressLint("ApplySharedPref")
    fun deleteMarkets() {
        val editor = preferences.edit()
        editor.remove(PREF_SAVED_MARKETS).commit()
        EventBus.getDefault().post(OnAllBookmarksDeletedEvent())
    }
}