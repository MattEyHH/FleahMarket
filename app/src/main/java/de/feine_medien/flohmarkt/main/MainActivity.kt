package de.feine_medien.flohmarkt.main

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import de.feine_medien.flohmarkt.R
import de.feine_medien.flohmarkt.bookmarks.BookmarksFragment
import de.feine_medien.flohmarkt.rights.AgbFragment
import de.feine_medien.flohmarkt.rights.ImprintFragment
import de.feine_medien.flohmarkt.rights.PrivacyFragment
import de.feine_medien.flohmarkt.search.SearchFragment
import de.feine_medien.flohmarkt.webservice.Webservice
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var webservice = Webservice()

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        var fragment: Fragment? = null
        var fragmentClass: Class<*>? = null
        fragmentClass = SearchFragment::class.java
        try {
            fragment = fragmentClass.newInstance() as Fragment
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val fragmentManager = supportFragmentManager
        fragment?.let { fragmentManager.beginTransaction().replace(R.id.fl_content, it).commit() }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        webservice.loadAllEvents()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> return true

            R.id.action_map -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var fragment: Fragment? = null
        var fragmentClass: Class<*>? = null

        when (item.itemId) {
            R.id.nav_search -> {
                fragmentClass = SearchFragment::class.java
                setToolbarTitle("Suchen")
            }
            R.id.nav_bookmark -> {
                fragmentClass = BookmarksFragment::class.java
                setToolbarTitle("Merkzettel")
            }
            R.id.nav_imprint -> {
                fragmentClass = ImprintFragment::class.java
                setToolbarTitle("Impressum")
            }
            R.id.nav_privacy -> {
                fragmentClass = PrivacyFragment::class.java
                setToolbarTitle("Datenschutz")
            }
            R.id.nav_agb -> {
                fragmentClass = AgbFragment::class.java
                setToolbarTitle("AGB")
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

        val fragmentManager = supportFragmentManager
        fragment?.let { fragmentManager.beginTransaction().replace(R.id.fl_content, it).commit() }

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
        builder.append(getString(R.string.share_message) + " " + getString(R.string.share_playstore_link))

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
}
