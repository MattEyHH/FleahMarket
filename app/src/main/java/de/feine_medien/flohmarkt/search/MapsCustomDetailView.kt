package de.feine_medien.flohmarkt.search

import android.app.Activity
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import de.feine_medien.flohmarkt.R
import kotlinx.android.synthetic.main.view_maps_custom_detail.view.*

class MapsCustomDetailView(private var activity: Activity) : GoogleMap.InfoWindowAdapter {

    override fun getInfoWindow(marker: Marker?): View? {

        return null
    }

    override fun getInfoContents(marker: Marker?): View {
        val view = activity.layoutInflater.inflate(R.layout.view_maps_custom_detail, null)

        view.tv_title.text = marker?.title

        return view
    }
}