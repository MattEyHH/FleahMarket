package flohmarkt.feine_medien.de.flohmarkttermine.search

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import flohmarkt.feine_medien.de.flohmarkttermine.Event.OnMarketClickedEvent
import flohmarkt.feine_medien.de.flohmarkttermine.R
import flohmarkt.feine_medien.de.flohmarkttermine.model.Market
import kotlinx.android.synthetic.main.view_flea_market_item.view.*
import org.greenrobot.eventbus.EventBus

class SearchAdapter internal constructor(private val markets: List<Market>) : RecyclerView.Adapter<SearchAdapter.SearchItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchItemViewHolder {
        return SearchItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_flea_market_item, parent, false))
    }

    override fun onBindViewHolder(holder: SearchItemViewHolder, position: Int) {
        val market = markets[position]
        holder.setMarkets(market, (markets.size - 1) == position)

        holder.itemView.setOnClickListener { EventBus.getDefault().post(OnMarketClickedEvent(market)) }
    }

    override fun getItemCount(): Int {
        return markets.size
    }

    class SearchItemViewHolder(val item: View) : RecyclerView.ViewHolder(item) {

        fun setMarkets(market: Market, isLastElement: Boolean) {
            item.v_divider.visibility = if (isLastElement) View.GONE else View.VISIBLE
            item.tv_date.text = market.startDate.toString() + " Flohmarkt"
            item.tv_market_name.text = market.title
            item.tv_location.text = market.location
            item.tv_address.text = market.street
            item.tv_zipcode.text = market.zipCode
            item.tv_distance.text = market.distance.toString()
        }
    }
}