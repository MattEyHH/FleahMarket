package de.feine_medien.flohmarkt.detail

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.provider.CalendarContract.Events
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import de.feine_medien.flohmarkt.R
import de.feine_medien.flohmarkt.event.OnAddMarketToBookmarksEvent
import de.feine_medien.flohmarkt.event.OnOrganizerButtonClickedEvent
import de.feine_medien.flohmarkt.model.Market
import de.feine_medien.flohmarkt.util.DateUtils
import de.feine_medien.flohmarkt.util.PreferencesHandler
import kotlinx.android.synthetic.main.activity_detail.*
import org.greenrobot.eventbus.EventBus
import java.util.*


class DetailActivity : AppCompatActivity() {

    companion object {
        const val CALENDAR_REQUEST = 234
        const val EXTRA_MARKET = "extra_market"

        fun getIntent(fragmentActivity: FragmentActivity, market: Market): Intent {
            val intent = Intent(fragmentActivity, DetailActivity::class.java)
            intent.putExtra(EXTRA_MARKET, market)

            return intent
        }
    }

    private lateinit var market: Market
    private lateinit var preferences: PreferencesHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        market = intent.getParcelableExtra(EXTRA_MARKET)
        initDetailView(market)

        setupPreferences()
        setupActionBar()
        setupClickListeners()
    }

    private fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = market.event?.title
    }

    private fun setupClickListeners() {
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

        btn_add_to_bookmarks.setOnClickListener {
            val listItems = resources.getStringArray(R.array.bookmark_notice)
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.add_with_notice))
            builder.setSingleChoiceItems(listItems, -1) { dialogInterface, i ->
                when (i) {
                    0 -> market.bookmarkNotice = 0
                    1 -> market.bookmarkNotice = 1
                    2 -> market.bookmarkNotice = 2
                    3 -> market.bookmarkNotice = 3
                }

                preferences.putMarket(market)
                EventBus.getDefault().postSticky(OnAddMarketToBookmarksEvent())
                Toast.makeText(this, getString(R.string.added_to_bookmarks), Toast.LENGTH_SHORT).show()

                dialogInterface.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
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

    private fun setupPreferences() {
        preferences = PreferencesHandler(this)
    }

    private fun openMap() {
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

    private fun addEventToCalendar(market: Market) {
        val intent = Intent(Intent.ACTION_EDIT)
        val timeStamp = market.event?.time?.times(1000L)

        intent.type = "vnd.android.cursor.item/event"
        intent.putExtra(Events.TITLE, market.event?.title)
        intent.putExtra(Events.EVENT_LOCATION, "${market.addr?.street}\n${market.addr?.plz} ${market.addr?.name}")
        intent.putExtra(Events.DESCRIPTION, market.event?.text)

        val calDate = GregorianCalendar(
                DateUtils.getYearAsIntegerFromTimestamp(timeStamp),
                DateUtils.getMonthAsIntegerFromTimestamp(timeStamp),
                DateUtils.getDayAsIntegerFromTimestamp(timeStamp))

        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                calDate.timeInMillis)
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
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
