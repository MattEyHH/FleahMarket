package de.feine_medien.flohmarkt.main

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import de.feine_medien.flohmarkt.R
import de.feine_medien.flohmarkt.bookmarks.BookmarksFragment
import de.feine_medien.flohmarkt.event.OnGeoLocationFoundEvent
import de.feine_medien.flohmarkt.event.OnLoadAllMarketsSuccessfulEvent
import de.feine_medien.flohmarkt.event.OnLoadSavedMarketsFromPreferencesEvent
import de.feine_medien.flohmarkt.event.OnNoGeoPermissionGivenEvent
import de.feine_medien.flohmarkt.rights.AgbFragment
import de.feine_medien.flohmarkt.rights.ImprintFragment
import de.feine_medien.flohmarkt.rights.PrivacyFragment
import de.feine_medien.flohmarkt.search.MapsFragment
import de.feine_medien.flohmarkt.search.SearchFragment
import de.feine_medien.flohmarkt.search.SearchListFragment
import de.feine_medien.flohmarkt.util.LocationProvider
import de.feine_medien.flohmarkt.util.PreferencesHandler
import de.feine_medien.flohmarkt.util.QueryKeys
import de.feine_medien.flohmarkt.webservice.Webservice
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        var currentLatitude = 0.0
        var currentLongitude = 0.0
    }

    private var webservice = Webservice()
    private var showMapIcon = true
    private var fragment: Fragment? = null
    private var fragmentClass: Class<*>? = null

    private lateinit var fragmentManager: FragmentManager
    private lateinit var currentDate: String
    private lateinit var preferences: PreferencesHandler
    private lateinit var locationProvider: LocationProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        EventBus.getDefault().register(this)
        setSupportActionBar(toolbar)

        preferences = PreferencesHandler(this)
        getSavedMarketsFromPreferences()

        invalidateOptionsMenu()

        setupLocationProvider()
        setupPreferences()
        setupCalendar()
        setupNavigationDrawerLayout()
        handleFragmentStart()

        if (currentLatitude != 0.0 && currentLongitude != 0.0) {
            makeGeoOnboardingCall()
        } else {
            makeOnboardingCall()
        }
    }

    override fun onDestroy() {
        locationProvider.stopLocationUpdates()
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    private fun getSavedMarketsFromPreferences() {
        val savedMarkets = preferences.getSavedMarkets()

        EventBus.getDefault().postSticky(OnLoadSavedMarketsFromPreferencesEvent(savedMarkets))
    }

    @SuppressLint("SimpleDateFormat")
    private fun setupCalendar() {
        val calendar = Calendar.getInstance()
        val mdformat = SimpleDateFormat("yyyy-MM-dd")
        currentDate = mdformat.format(calendar.time)
    }

    private fun setupPreferences() {
        preferences = PreferencesHandler(this)
    }

    private fun setupLocationProvider() {
        locationProvider = LocationProvider(this)
        locationProvider.startLocationUpdates()
    }

    private fun handleFragmentStart() {
        fragmentClass = SearchFragment::class.java
        try {
            fragment = (fragmentClass as Class<*>).newInstance() as Fragment
        } catch (e: Exception) {
            e.printStackTrace()
        }

        fragmentManager = supportFragmentManager
        fragment?.let { fragmentManager.beginTransaction().replace(R.id.fl_content, it).commit() }
    }

    private fun setupNavigationDrawerLayout() {
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                showMapIcon = true

                val searchListFragment = SearchListFragment()
                replaceFragmentOnMenuItemClicked(searchListFragment)

                invalidateOptionsMenu()
                true
            }

            R.id.action_map -> {
                showMapIcon = false

                val mapsFragment = MapsFragment()
                replaceFragmentOnMenuItemClicked(mapsFragment)

                invalidateOptionsMenu()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun replaceFragmentOnMenuItemClicked(fragment: Fragment) {
        fragmentManager.beginTransaction().replace(R.id.fl_search, fragment).commit()
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)

        val itemSearch = menu?.findItem(R.id.action_search)
        val itemMap = menu?.findItem(R.id.action_map)

        if (showMapIcon) {
            itemSearch?.isVisible = false
            itemMap?.isVisible = true
        } else if (!showMapIcon) {
            itemSearch?.isVisible = true
            itemMap?.isVisible = false
        }

        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_search -> {
                fragmentClass = SearchFragment::class.java
                setToolbarTitle(getString(R.string.search))
            }
            R.id.nav_bookmark -> {
                fragmentClass = BookmarksFragment::class.java
                setToolbarTitle(getString(R.string.bookmark))
            }
            R.id.nav_imprint -> {
                fragmentClass = ImprintFragment::class.java
                setToolbarTitle(getString(R.string.imprint))
            }
            R.id.nav_privacy -> {
                fragmentClass = PrivacyFragment::class.java
                setToolbarTitle(getString(R.string.privacy))
            }
            R.id.nav_agb -> {
                fragmentClass = AgbFragment::class.java
                setToolbarTitle(getString(R.string.agb))
            }
            R.id.nav_share -> {
                shareContent()
            }
            R.id.nav_send -> {
                giveFeedback()
            }
        }

        try {
            fragment = fragmentClass?.newInstance() as Fragment
        } catch (e: Exception) {
            e.printStackTrace()
        }

        fragmentManager = supportFragmentManager
        fragment?.let { replaceFragmentOnMenuItemClicked(it) }

        val drawer = drawer_layout as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)

        return true
    }

    private fun setToolbarTitle(title: String) {
        toolbar.title = title
    }

    private fun shareContent() {
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"

        val builder = StringBuilder()
        builder.append(getString(R.string.share_message_headline))
        builder.append("\n\n")
        builder.append("${getString(R.string.share_message)} ${getString(R.string.share_playstore_link)}")

        share.putExtra(Intent.EXTRA_TEXT, builder.toString())

        startActivity(Intent.createChooser(share, getString(R.string.share_app_title)))
    }

    private fun giveFeedback() {
        val subject = getString(R.string.give_feedback_subject)
        val intent = Intent(Intent.ACTION_VIEW)
        val data = Uri.parse("mailto:info@flohmarkt-termine.net?subject=$subject")
        intent.data = data

        startActivity(intent)
    }

    private fun makeOnboardingCall() {
        val map = HashMap<String, String>()

        map.put(QueryKeys.CITY_KEY, "hamburg")
        map.put(QueryKeys.DAYS_KEY, "7")
        map.put(QueryKeys.DATE_KEY, currentDate)

        webservice.loadEventsByDynamicCall(map)
    }

    private fun makeGeoOnboardingCall() {
        val map = HashMap<String, String>()
        map.put(QueryKeys.LAT_KEY, currentLatitude.toString())
        map.put(QueryKeys.LONG_KEY, currentLongitude.toString())
        map.put(QueryKeys.RADIUS_KEY, "20")
        map.put(QueryKeys.DAYS_KEY, "7")
        map.put(QueryKeys.DATE_KEY, currentDate)

        webservice.loadEventsByDynamicCall(map)
    }

    @Subscribe(sticky = true)
    fun onEvent(event: OnNoGeoPermissionGivenEvent) {
        makeOnboardingCall()
        Toast.makeText(this, getString(R.string.no_location_available), Toast.LENGTH_SHORT).show()
    }

    @Subscribe
    fun onEvent(event: OnLoadAllMarketsSuccessfulEvent) {
        progress.visibility = View.GONE
    }

    @Subscribe(sticky = true)
    fun onEvent(event: OnGeoLocationFoundEvent) {
        currentLatitude = event.location.latitude
        currentLongitude = event.location.longitude

        EventBus.getDefault().removeStickyEvent(event)
    }
}
