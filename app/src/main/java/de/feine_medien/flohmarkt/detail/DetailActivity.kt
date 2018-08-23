package de.feine_medien.flohmarkt.detail

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.provider.CalendarContract.Events
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import de.feine_medien.flohmarkt.R
import de.feine_medien.flohmarkt.event.OnMarketClickedEvent
import de.feine_medien.flohmarkt.event.OnOrganizerButtonClickedEvent
import de.feine_medien.flohmarkt.model.Market
import de.feine_medien.flohmarkt.util.DateUtils
import kotlinx.android.synthetic.main.activity_detail.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*


class DetailActivity : AppCompatActivity() {

    lateinit var market: Market

    companion object {
        const val CALENDAR_REQUEST = 234
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btn_organizer.setOnClickListener {
            EventBus.getDefault().postSticky(OnOrganizerButtonClickedEvent(market))

            val organizerIntent = Intent(this, OrganizerActivity::class.java)
            startActivity(organizerIntent)
        }

        btn_more_description.setOnClickListener {
            tv_description.text = market.event?.fulltext

            btn_more_description.visibility = View.GONE
            btn_less_description.visibility = View.VISIBLE
        }

        btn_less_description.setOnClickListener {
            tv_description.text = market.event?.text

            btn_more_description.visibility = View.VISIBLE
            btn_less_description.visibility = View.GONE
        }

        btn_add_to_calender.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_CALENDAR),
                        CALENDAR_REQUEST)
            } else {
                addEventToCalendar(market)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            CALENDAR_REQUEST -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    addEventToCalendar(market)
                } else {
                    showNotInstalledToast()
                }
                return
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()

            R.id.action_route -> openMap()
        }

        return true
    }

    fun openMap() {
        var geoString = "geo:"

        market.let {
            geoString += String.format(Locale.getDefault(), "%s,%s?q=%s,%s %s,%s",
                    market.addr?.lat, market.addr?.lng,
                    market.addr?.name, market.addr?.street, market.addr?.plz,
                    market.province?.name)
        }

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(geoString))

        try {
            this.startActivity(intent)

        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, getString(R.string.no_maps_app_installed), Toast.LENGTH_LONG).show()
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

    @SuppressLint("SetTextI18n")
    fun initDetailView(market: Market) {
        this.market = market

        tv_title.text = market.event?.title
        tv_location.text = market.addr?.location
        tv_date.text = "${market.event?.date} ${DateUtils.getTime(market.event?.tstart.toString())}-${DateUtils.getTime(market.event?.tend.toString())}"
        tv_address.text = market.addr?.street
        tv_zipcode.text = "${market.addr?.plz} ${market.addr?.name}"
        tv_city.text = market.province?.name
        tv_description.text = market.event?.text
    }

    @Subscribe(sticky = true)
    fun onEvent(event: OnMarketClickedEvent) {
        supportActionBar?.title = event.market.event?.title
        initDetailView(event.market)

        EventBus.getDefault().removeStickyEvent(event)
    }

    private fun addEventToCalendar(market: Market) {
        val intent = Intent(Intent.ACTION_EDIT)
        val timeStamp = market.event?.time

        intent.type = "vnd.android.cursor.item/event"
        intent.putExtra(Events.TITLE, market.event?.title)
        intent.putExtra(Events.EVENT_LOCATION, market.addr?.street + "\n" + market.addr?.plz + " " + market.addr?.name)
        intent.putExtra(Events.DESCRIPTION, market.event?.text)

        val calDate = GregorianCalendar(
                DateUtils.getYearAsIntegerFromTimestamp(timeStamp),
                DateUtils.getMonthAsIntegerFromTimestamp(timeStamp),
                DateUtils.getDayAsIntegerFromTimestamp(timeStamp))

        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                calDate.timeInMillis)
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                calDate.timeInMillis)
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            showNotInstalledToast()
        }
    }

    private fun showNotInstalledToast() {
        Toast.makeText(this, getString(R.string.no_calendar_app_installed), Toast.LENGTH_SHORT).show()
    }
}
