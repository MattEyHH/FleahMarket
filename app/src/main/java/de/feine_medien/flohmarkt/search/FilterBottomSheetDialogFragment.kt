package de.feine_medien.flohmarkt.search

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.support.design.widget.BottomSheetDialogFragment
import android.view.View
import android.widget.AdapterView
import android.widget.SeekBar
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.PlaceBuffer
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import de.feine_medien.flohmarkt.R
import de.feine_medien.flohmarkt.event.OnNoGeoPermissionGivenEvent
import de.feine_medien.flohmarkt.event.OnRadiusValueHasChangedEvent
import de.feine_medien.flohmarkt.event.OnSearchedForMarketsByCityOrZipEvent
import de.feine_medien.flohmarkt.event.OnSearchedForMarketsByCurrentPositionEvent
import de.feine_medien.flohmarkt.main.MainActivity
import de.feine_medien.flohmarkt.util.PreferencesHandler
import de.feine_medien.flohmarkt.util.QueryKeys
import de.feine_medien.flohmarkt.webservice.Webservice
import kotlinx.android.synthetic.main.dialog_fragment_filter_bottom_sheet.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.text.SimpleDateFormat
import java.util.*

class FilterBottomSheetDialogFragment : BottomSheetDialogFragment(), GoogleApiClient.OnConnectionFailedListener {

    private val latLngBounds = LatLngBounds(LatLng(-40.0, -168.0), LatLng(71.0, 136.0))
    private val cal = Calendar.getInstance(Locale.GERMANY)
    private val params = HashMap<String, String>()
    private val webservice = Webservice()

    private var currentDate: String = ""
    private var currentApiDate: String = ""
    private var year: String = ""
    private var month: String = ""
    private var day: String = ""
    private var placesName: String = ""
    private var selectedDays: Int = -1
    private var placesLat = 0.0
    private var placesLng = 0.0

    private lateinit var filterView: View
    private lateinit var dateFormatter: SimpleDateFormat
    private lateinit var apiFormatter: SimpleDateFormat
    private lateinit var placeAutocompleteAdapter: PlaceAutocompleteAdapter
    private lateinit var googleApiClient: GoogleApiClient
    private lateinit var preferences: PreferencesHandler

    @SuppressLint("RestrictedApi", "SimpleDateFormat")
    override fun setupDialog(dialog: Dialog?, style: Int) {
        super.setupDialog(dialog, style)

        filterView = View.inflate(context, R.layout.dialog_fragment_filter_bottom_sheet, null)
        dialog?.setContentView(filterView)

        setupPreferences()
        setupPlaceAutoCompleteAdapter()
        setupRadioButtons()
        setupRadiusSeekBar()
        setupDatePicker()

        val df = SimpleDateFormat("dd.MM.yyyy")
        val formattedDate = df.format(cal.time)
        filterView.tv_date.text = formattedDate
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        googleApiClient.stopAutoManage(activity!!)
        googleApiClient.disconnect()
        super.onStop()
    }

    private fun setupPreferences() {
        preferences = PreferencesHandler(context!!)
    }

    private fun setupPlaceAutoCompleteAdapter() {
        googleApiClient = GoogleApiClient
                .Builder(context!!)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(activity!!, this)
                .build()

        val autocompleteFilter = AutocompleteFilter.Builder()
                .setTypeFilter(Place.TYPE_COUNTRY)
                .setCountry("DE")
                .build()

        placeAutocompleteAdapter = PlaceAutocompleteAdapter(context, googleApiClient, latLngBounds, autocompleteFilter)

        filterView.tv_auto_complete.setAdapter(placeAutocompleteAdapter)
        filterView.tv_auto_complete.onItemClickListener = autoCompleteClickListener

    }

    private val autoCompleteClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
        val item = placeAutocompleteAdapter.getItem(i)
        val placeId = item.placeId

        val placesResult: PendingResult<PlaceBuffer> = Places.GeoDataApi.getPlaceById(googleApiClient, placeId)
        placesResult.setResultCallback(mUpdatePlaceDetailsCallback)
    }

    private val mUpdatePlaceDetailsCallback = ResultCallback<PlaceBuffer> { places ->
        if (places.status.isSuccess) {
            val myPlace = places.get(0)
            placesLat = myPlace.latLng.latitude
            placesLng = myPlace.latLng.longitude
            placesName = myPlace.name.toString()

            preferences.putLastSearchedLatLng(LatLng(placesLat, placesLng))
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Toast.makeText(context, "Service nicht erreichbar", Toast.LENGTH_SHORT).show()
    }

    private fun setupRadiusSeekBar() {
        filterView.sb_search_radius.max = 200
        filterView.sb_search_radius.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, p2: Boolean) {
                var newProgress = progress
                if (newProgress < 20) {
                    newProgress = 20
                    seekBar?.progress = newProgress
                }
                filterView.tv_search_km.text = "${newProgress}km"
                EventBus.getDefault().post(OnRadiusValueHasChangedEvent(newProgress))
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                //noOp
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                //noOp
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun setupDatePicker() {
        dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY)
        apiFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY)
        currentDate = dateFormatter.format(cal.time)
        currentApiDate = apiFormatter.format(cal.time)

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val month = monthOfYear + 1

            currentDate = "$dayOfMonth.$month.$year"
            currentApiDate = "$year-$month-$dayOfMonth"
            filterView.tv_date.text = currentDate
        }

        filterView.tv_date.setOnClickListener {
            DatePickerDialog(context, dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()

            year = cal.get(Calendar.YEAR).toString()
            month = cal.get(Calendar.MONTH).toString()
            day = cal.get(Calendar.DAY_OF_MONTH).toString()
        }

        filterView.tv_days.setOnClickListener {
            val listItems = resources.getStringArray(R.array.following_days_items)
            val builder = AlertDialog.Builder(context)
            builder.setTitle(getString(R.string.show_me_in_following_days))
            builder.setSingleChoiceItems(listItems, -1) { dialogInterface, i ->
                when (i) {
                    0 -> selectedDays = 7
                    1 -> selectedDays = 14
                    2 -> selectedDays = 30
                }

                filterView.tv_days.text = listItems?.get(i)
                dialogInterface.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun setupRadioButtons() {
        filterView.rb_current_position.setOnClickListener {
            filterView.rb_current_position.isChecked = true
            filterView.rb_zip_area.isChecked = false
            filterView.tv_auto_complete.visibility = View.GONE
        }

        filterView.rb_zip_area.setOnClickListener {
            filterView.rb_zip_area.isChecked = true
            filterView.rb_current_position.isChecked = false
            filterView.tv_auto_complete.visibility = View.VISIBLE
        }
    }

    private fun getCurrentCategoryState(): Int {
        var categoryValue = -1
        when (filterView.rg_market.checkedRadioButtonId) {
            R.id.rb_all_categories -> { categoryValue = 0 }
            R.id.rb_antik_market -> { categoryValue = 1 }
            R.id.rb_flea_market -> { categoryValue = 2 }
            R.id.rb_art_work -> { categoryValue = 3 }
            R.id.rb_city_party -> { categoryValue = 4 }
            R.id.rb_collection -> { categoryValue = 5 }
        }
        return categoryValue
    }

    private fun getCurrentRoofState(): Int {
        var roofValue = -1
        when (filterView.rg_roof.checkedRadioButtonId) {
            R.id.rb_roof_no_choose -> { roofValue = 0 }
            R.id.rb_roof_no_roof -> { roofValue = 1 }
            R.id.rb_roof_part_roof -> { roofValue = 2 }
            R.id.rb_roof_full -> { roofValue = 3 }
        }
        return roofValue
    }

    override fun onCancel(dialog: DialogInterface?) {
        super.onCancel(dialog)

        if (filterView.rb_current_position.isChecked) {
            if (MainActivity.currentLatitude == 0.0 || MainActivity.currentLongitude == 0.0) {
                Toast.makeText(context, getString(R.string.no_location_available), Toast.LENGTH_SHORT).show()
            } else {
                params.put(QueryKeys.LAT_KEY, MainActivity.currentLatitude.toString())
                params.put(QueryKeys.LONG_KEY, MainActivity.currentLongitude.toString())
            }

            EventBus.getDefault().post(OnSearchedForMarketsByCurrentPositionEvent())
        }

        if (filterView.rb_zip_area.isChecked) {
            params.put(QueryKeys.LAT_KEY, placesLat.toString())
            params.put(QueryKeys.LONG_KEY, placesLng.toString())
            preferences.putLastSearchedCity(placesName)

            EventBus.getDefault().post(OnSearchedForMarketsByCityOrZipEvent(placesName))
        }

        if (filterView.sb_search_radius.progress < 20) {
            filterView.sb_search_radius.progress = 20
        }

        params.put(QueryKeys.RADIUS_KEY, filterView.sb_search_radius.progress.toString())

        params.put(QueryKeys.DATE_KEY, currentApiDate)

        if (selectedDays >= 0) {
            params.put(QueryKeys.DAYS_KEY, selectedDays.toString())
        } else {
            params.put(QueryKeys.DAYS_KEY, "7")
        }

        if (getCurrentCategoryState() >= 0) {
            params.put(QueryKeys.CATEGORY_KEY, getCurrentCategoryState().toString())
        }

        if (getCurrentCategoryState() >= 0) {
            params.put(QueryKeys.ROOF_KEY, getCurrentRoofState().toString())
        }

        if (params.size > 0) {
            webservice.loadEventsByDynamicCall(params)
        }
    }

    @Subscribe(sticky = true)
    fun onEvent(event: OnNoGeoPermissionGivenEvent) {
        filterView.rb_current_position.isChecked = false
        filterView.rb_current_position.isEnabled = false
        filterView.rb_current_position.setOnClickListener {
            Toast.makeText(context, getString(R.string.no_geo_permission_given), Toast.LENGTH_SHORT).show()
        }
        filterView.rb_zip_area.isChecked = true
        filterView.tv_auto_complete.visibility = View.VISIBLE
    }
}