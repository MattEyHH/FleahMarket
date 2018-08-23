package de.feine_medien.flohmarkt.search

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.feine_medien.flohmarkt.R
import de.feine_medien.flohmarkt.detail.DetailActivity
import de.feine_medien.flohmarkt.event.OnMarketClickedEvent
import de.feine_medien.flohmarkt.model.Market
import de.feine_medien.flohmarkt.util.CurrentLocationListener
import kotlinx.android.synthetic.main.view_flea_market_item.view.*
import org.greenrobot.eventbus.EventBus
import java.text.DecimalFormat

class SearchAdapter internal constructor(private val fragmentActivity: FragmentActivity?, private val markets: MutableList<Market>) : RecyclerView.Adapter<SearchAdapter.SearchItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchItemViewHolder {
        return SearchItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_flea_market_item, parent, false))
    }

    override fun onBindViewHolder(holder: SearchItemViewHolder, position: Int) {
        val market = markets[position]
        holder.setMarkets(market, (markets.size - 1) == position)

        holder.item.setOnClickListener {
            EventBus.getDefault().postSticky(OnMarketClickedEvent(market))

            val detailIntent = Intent(fragmentActivity, DetailActivity::class.java)
            fragmentActivity?.startActivity(detailIntent)
        }
    }

    override fun getItemCount(): Int {
        return markets.size
    }

    class SearchItemViewHolder(val item: View) : RecyclerView.ViewHolder(item) {

        private val df = DecimalFormat("#.#")

        @SuppressLint("SetTextI18n")
        fun setMarkets(market: Market, isLastElement: Boolean) {
            market.let {
                val locationA = Location("point A")
                locationA.latitude = CurrentLocationListener.latitude!!
                locationA.longitude = CurrentLocationListener.longitude!!

                val locationB = Location("point B")
                locationB.latitude = it.addr?.lat!!
                locationB.longitude = it.addr.lng!!

                item.v_divider.visibility = if (isLastElement) View.INVISIBLE else View.VISIBLE
                item.tv_date.text = "${it.event?.date} ${it.category?.name}"
                item.tv_market_name.text = it.event?.title
                item.tv_location.text = it.addr.location
                item.tv_address.text = it.addr.street
                item.tv_zipcode.text = "${it.addr.plz} ${it.addr.name}"
                item.tv_distance.text = "${df.format(locationA.distanceTo(locationB) / 1000f)}km"
            }
        }
    }
}