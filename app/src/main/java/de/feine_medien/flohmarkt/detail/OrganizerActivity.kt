package de.feine_medien.flohmarkt.detail

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import de.feine_medien.flohmarkt.R
import de.feine_medien.flohmarkt.event.OnOrganizerButtonClickedEvent
import de.feine_medien.flohmarkt.model.Market
import kotlinx.android.synthetic.main.activity_organizer.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class OrganizerActivity : AppCompatActivity() {

    companion object {
        const val CALL_REQUEST = 567
    }

    private lateinit var market: Market

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_organizer)

        setupActionBar()
        setupClickListeners()
    }

    private fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupClickListeners() {
        btn_make_call.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.CALL_PHONE),
                        CALL_REQUEST)
            } else {
                makeCall()
            }
        }

        btn_add_to_contactbook.setOnClickListener {
            val addContactIntent = Intent(ContactsContract.Intents.Insert.ACTION)

            addContactIntent.type = ContactsContract.RawContacts.CONTENT_TYPE
            addContactIntent.putExtra(ContactsContract.Intents.Insert.NAME, "${market.organizer?.firstname} ${market.organizer?.lastname}")
            addContactIntent.putExtra(ContactsContract.Intents.Insert.PHONE, market.organizer?.phone)

            startActivity(addContactIntent)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CALL_REQUEST -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    makeCall()
                } else {
                    btn_make_call.visibility = View.INVISIBLE
                }
                return
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun makeCall() {
        val makeCallIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:${market.organizer?.phone}"))
        startActivity(makeCallIntent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }

        return true
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

        tv_company.text = market.organizer?.company
        tv_name.text = "${market.organizer?.firstname} ${market.organizer?.lastname}"
        tv_street.text = market.organizer?.street
        tv_zipcode.text = "${market.organizer?.plz} ${market.organizer?.city}"
        market.organizer?.phone?.let {
            if (it.isNotEmpty()) {
                tv_phone.text = "Tel.: ${market.organizer.phone}"

            } else {
                tv_phone.visibility = View.INVISIBLE
            }
        }
    }

    @Subscribe(sticky = true)
    fun onEvent(event: OnOrganizerButtonClickedEvent) {
        initDetailView(event.market)

        EventBus.getDefault().removeStickyEvent(event)
    }
}
