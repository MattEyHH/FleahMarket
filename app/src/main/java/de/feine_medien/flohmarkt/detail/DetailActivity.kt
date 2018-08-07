package de.feine_medien.flohmarkt.detail

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import de.feine_medien.flohmarkt.R
import de.feine_medien.flohmarkt.event.OnMarketClickedEvent
import de.feine_medien.flohmarkt.model.Market
import kotlinx.android.synthetic.main.activity_detail.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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

    fun initDetailView(market: Market) {
        tv_title.text = market.event?.title
        tv_location.text = market.addr?.location
        tv_date.text = "${market.event?.date} ${market.event?.tstart}-${market.event?.tend}"
        tv_address.text = market.addr?.street
        tv_zipcode.text = "${market.addr?.plz} ${market.addr?.name}"
        tv_city.text = market.province?.name
    }

    @Subscribe(sticky = true)
    fun onEvent(event: OnMarketClickedEvent) {

        supportActionBar?.title = event.market.event?.title

        initDetailView(event.market)
        EventBus.getDefault().removeStickyEvent(event)
    }
}
