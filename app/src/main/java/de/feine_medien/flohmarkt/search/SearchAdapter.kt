package de.feine_medien.flohmarkt.search

import android.content.Intent
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.feine_medien.flohmarkt.R
import de.feine_medien.flohmarkt.detail.DetailActivity
import de.feine_medien.flohmarkt.event.OnMarketClickedEvent
import de.feine_medien.flohmarkt.model.Market
import kotlinx.android.synthetic.main.view_flea_market_item.view.*
import org.greenrobot.eventbus.EventBus

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

    fun clear() {
        markets.clear()
        notifyDataSetChanged()
    }

    class SearchItemViewHolder(val item: View) : RecyclerView.ViewHolder(item) {

        fun setMarkets(market: Market, isLastElement: Boolean) {
            item.v_divider.visibility = if (isLastElement) View.GONE else View.VISIBLE
            item.tv_date.text = "${market.event?.date} ${market.category?.name}"
            item.tv_market_name.text = market.event?.title
            item.tv_location.text = market.addr?.location
            item.tv_address.text = market.addr?.street
            item.tv_zipcode.text = "${market.addr?.plz} ${market.addr?.name}"
            //item.tv_distance.text = market.distance.toString()
        }
    }
}