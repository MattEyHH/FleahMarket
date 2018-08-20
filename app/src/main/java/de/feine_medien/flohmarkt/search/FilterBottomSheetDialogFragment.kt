package de.feine_medien.flohmarkt.search

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.support.design.widget.BottomSheetDialogFragment
import android.view.View
import android.widget.SeekBar
import de.feine_medien.flohmarkt.R
import de.feine_medien.flohmarkt.event.OnFilterClosedEvent
import de.feine_medien.flohmarkt.event.OnGeoLocationFoundEvent
import de.feine_medien.flohmarkt.webservice.Webservice
import kotlinx.android.synthetic.main.dialog_fragment_filter_bottom_sheet.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.text.SimpleDateFormat
import java.util.*

class FilterBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private val cal = Calendar.getInstance(Locale.GERMANY)
    private val params = HashMap<String, String>()
    private val webservice = Webservice()

    private var latitude: Double? = 0.0
    private var longitude: Double? = 0.0
    private var selectedDays: Int = -1
    private var year: String = ""
    private var month: String = ""
    private var day: String = ""

    private lateinit var filterView: View
    private lateinit var dateFormatter: SimpleDateFormat

    companion object {
        private const val CATEGORY_KEY = "cid"
        private const val ROOF_KEY = "options"
        private const val LAT_KEY = "latitude"
        private const val LONG_KEY = "longitude"
        private const val RADIUS_KEY = "radius"
        private const val DAYS_KEY = "t"
        private const val DATE_KEY = "d"
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog?, style: Int) {
        super.setupDialog(dialog, style)

        filterView = View.inflate(context, R.layout.dialog_fragment_filter_bottom_sheet, null)
        dialog?.setContentView(filterView)

        setupRadioButtons()
        setupRadiusSeekBar()
        setupDatePicker()
    }

    private fun setupRadiusSeekBar() {
        filterView.sb_search_radius.max = 200
        filterView.sb_search_radius.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, p2: Boolean) {
                var newProgress = progress
                if (newProgress < 20) {
                    newProgress = 20
                    seekBar?.progress = newProgress
                }
                filterView.tv_search_km.text = "${newProgress}km"
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })
    }

    private fun setupDatePicker() {
        dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY)
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            filterView.tv_date.text = dateFormatter.format(cal.time)
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

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    private fun setupRadioButtons() {
        filterView.rb_current_position.setOnClickListener {
            filterView.rb_current_position.isChecked = true
            filterView.rb_zip_area.isChecked = false
        }

        filterView.rb_zip_area.setOnClickListener {
            filterView.rb_zip_area.isChecked = true
            filterView.rb_current_position.isChecked = false
        }
    }

    private fun getCurrentCategoryState(): Int {
        var categoryValue = -1
        when (filterView.rg_market.checkedRadioButtonId) {
            R.id.rb_all_categories -> {
                categoryValue = 0
            }
            R.id.rb_antik_market -> {
                categoryValue = 1
            }
            R.id.rb_flea_market -> {
                categoryValue = 2
            }
            R.id.rb_art_work -> {
                categoryValue = 3
            }
            R.id.rb_city_party -> {
                categoryValue = 4
            }
            R.id.rb_collection -> {
                categoryValue = 5
            }
        }
        return categoryValue
    }

    private fun getCurrentRoofState(): Int {
        var roofValue = -1
        when (filterView.rg_roof.checkedRadioButtonId) {
            R.id.rb_roof_no_choose -> {
                roofValue = 0
            }
            R.id.rb_roof_no_roof -> {
                roofValue = 1
            }
            R.id.rb_roof_part_roof -> {
                roofValue = 2
            }
            R.id.rb_roof_full -> {
                roofValue = 3
            }
        }
        return roofValue
    }

    override fun onCancel(dialog: DialogInterface?) {
        super.onCancel(dialog)

        if (latitude != 0.0 || longitude != 0.0) {
            params.put(LAT_KEY, latitude.toString())
            params.put(LONG_KEY, longitude.toString())
            if (filterView.sb_search_radius.progress < 20) {
                filterView.sb_search_radius.progress = 20
            }
            params.put(RADIUS_KEY, filterView.sb_search_radius.progress.toString())
        }

        if (selectedDays >= 0) {
            params.put(DATE_KEY, "$year-$month-$day")
            params.put(DAYS_KEY, selectedDays.toString())
        }

        if (getCurrentCategoryState() >= 0) {
            params.put(CATEGORY_KEY, getCurrentCategoryState().toString())
        }

        if (getCurrentCategoryState() >= 0) {
            params.put(ROOF_KEY, getCurrentRoofState().toString())
        }

        if (params.size > 0) {
            webservice.loadEventsByDynamicCall(params)
        }

        EventBus.getDefault().post(OnFilterClosedEvent())
    }

    @Subscribe(sticky = true)
    fun onEvent(event: OnGeoLocationFoundEvent) {
        latitude = event.lat
        longitude = event.long

        EventBus.getDefault().removeStickyEvent(event)
    }
}