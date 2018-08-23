package de.feine_medien.flohmarkt.util

import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import de.feine_medien.flohmarkt.event.OnLocationProviderDisabledEvent
import org.greenrobot.eventbus.EventBus

class CurrentLocationListener : LocationListener {

    companion object {
        var latitude: Double? = 0.0
        var longitude: Double? = 0.0
    }

    override fun onLocationChanged(location: Location?) {
        latitude = location?.latitude
        longitude = location?.longitude
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

    }

    override fun onProviderEnabled(p0: String?) {

    }

    override fun onProviderDisabled(p0: String?) {
        EventBus.getDefault().post(OnLocationProviderDisabledEvent())
    }
}