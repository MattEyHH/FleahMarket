package de.feine_medien.flohmarkt.search

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import de.feine_medien.flohmarkt.R
import de.feine_medien.flohmarkt.detail.DetailActivity
import de.feine_medien.flohmarkt.event.OnLoadAllMarketsSuccessfulEvent
import de.feine_medien.flohmarkt.main.MainActivity
import de.feine_medien.flohmarkt.main.SplashActivity
import de.feine_medien.flohmarkt.model.Market
import de.feine_medien.flohmarkt.util.LocationProvider
import kotlinx.android.synthetic.main.fragment_maps.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class MapsFragment : Fragment(), OnMapReadyCallback {

    private val height = 170
    private val width = 170

    private var mapView: MapView? = null

    private lateinit var mMap: GoogleMap
    private lateinit var markets: List<Market>
    private lateinit var locationProvider: LocationProvider

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMapView(savedInstanceState)
        setupLocationProvider()
        markets = emptyList()
    }

    override fun onStart() {
        EventBus.getDefault().register(this)
        super.onStart()
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    private fun setupMapView(bundle: Bundle?) {
        mapView = view?.findViewById(R.id.map) as MapView?
        mapView?.onCreate(bundle)
        mapView?.onResume()
        mapView?.getMapAsync(this)
    }

    private fun setupLocationProvider() {
        locationProvider = LocationProvider(activity)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(activity, R.raw.style_json))

        if (locationProvider.isPermissionGiven) {
            mMap.isMyLocationEnabled = true
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(MainActivity.currentLatitude, MainActivity.currentLongitude), 11f))
        } else {
            locationProvider.askForPermission(activity, SplashActivity.GEO_REQUEST)
        }

        val bitmapdraw = resources.getDrawable(R.drawable.marker) as BitmapDrawable
        val b = bitmapdraw.bitmap
        val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)

        val markerInfoWindowAdapter = activity?.let { MapsCustomDetailView(it) }
        mMap.setInfoWindowAdapter(markerInfoWindowAdapter)

        markets.let {
            if (markets.isNotEmpty()) {
                markets.forEach { market ->
                    val marker = MarkerOptions().position(LatLng(market.event?.lat!!, market.event.lng!!)).title(market.event.title).icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                    mMap.addMarker(marker)
                }
            }
        }

        mMap.setOnInfoWindowClickListener {
            val latlng = it.position

            markets.forEach {
                if (latlng == LatLng(it.event?.lat!!, it.event.lng!!)) {
                    startActivity(DetailActivity.getIntent(activity!!, it))
                }
            }
        }
        progress.visibility = View.GONE
    }

    @Subscribe(sticky = true)
    fun onEvent(event: OnLoadAllMarketsSuccessfulEvent) {
        markets = event.markets
    }
}
