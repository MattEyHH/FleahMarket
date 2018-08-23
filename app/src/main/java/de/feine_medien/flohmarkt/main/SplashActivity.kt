package de.feine_medien.flohmarkt.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import de.feine_medien.flohmarkt.R
import de.feine_medien.flohmarkt.event.OnGeoLocationFoundEvent
import de.feine_medien.flohmarkt.event.OnNoGeoPermissionGivenEvent
import de.feine_medien.flohmarkt.util.CurrentLocationListener
import org.greenrobot.eventbus.EventBus

class SplashActivity : AppCompatActivity() {

    companion object {
        const val GEO_REQUEST = 789
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        geoPermissionCheck()
    }

    private fun startMainActivityWithDelay() {
        val handler = Handler()
        handler.postDelayed(Runnable {
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
            finish()
        }, 1000)
    }

    private fun startMainActivity() {
        val mainIntent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)
        finish()
    }

    private fun geoPermissionCheck() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    GEO_REQUEST)
        } else {
            getCurrentLocation()
            startMainActivityWithDelay()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locationListener = CurrentLocationListener()
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)

        val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (lastKnownLocation != null) {
            EventBus.getDefault().postSticky(OnGeoLocationFoundEvent(lastKnownLocation.latitude, lastKnownLocation.longitude))
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            GEO_REQUEST -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getCurrentLocation()
                    startMainActivity()
                } else {
                    EventBus.getDefault().postSticky(OnNoGeoPermissionGivenEvent())
                    startMainActivityWithDelay()
                }
                return
            }
        }
    }
}
