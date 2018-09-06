package de.feine_medien.flohmarkt.bookmarks

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import de.feine_medien.flohmarkt.R
import de.feine_medien.flohmarkt.detail.DetailActivity
import de.feine_medien.flohmarkt.event.OnBookmarksCountChangedEvent
import de.feine_medien.flohmarkt.event.OnDeleteBookmarkEvent
import de.feine_medien.flohmarkt.model.Market
import kotlinx.android.synthetic.main.view_flea_market_item.view.*
import org.greenrobot.eventbus.EventBus


class BookmarkAdapter internal constructor(private val fragmentActivity: FragmentActivity?, private val markets: MutableList<Market>, val context: Context) : RecyclerView.Adapter<BookmarkAdapter.SearchItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchItemViewHolder {
        return SearchItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_flea_market_item, parent, false), context)
    }

    override fun onBindViewHolder(holder: SearchItemViewHolder, position: Int) {
        val market = markets[position]
        holder.setMarkets(market, (markets.size - 1) == position)

        holder.item.setOnClickListener {
            fragmentActivity?.startActivity(DetailActivity.getIntent(fragmentActivity, market))
        }
    }

    override fun getItemCount(): Int {
        return markets.size
    }

    class SearchItemViewHolder(val item: View, val context: Context) : RecyclerView.ViewHolder(item) {

        @SuppressLint("SetTextI18n")
        fun setMarkets(market: Market, isLastElement: Boolean) {
            market.let { it ->
                when (market.bookmarkNotice) {
                    0 -> item.tv_bookmark_going.visibility = View.VISIBLE
                    1 -> item.tv_bookmark_not_sure.visibility = View.VISIBLE
                    2 -> item.tv_bookmark_want_visit.visibility = View.VISIBLE
                    3 -> item.tv_bookmark_no_place.visibility = View.VISIBLE
                }

                item.v_divider.visibility = if (isLastElement) View.INVISIBLE else View.VISIBLE
                item.tv_date.text = "${it.event?.date} ${it.category?.name}"
                item.tv_market_name.text = it.event?.title
                item.tv_location.text = it.addr?.location
                item.tv_address.text = it.addr?.street
                item.tv_zipcode.text = "${it.addr?.plz} ${it.addr?.name}"
                item.iv_delete.visibility = View.VISIBLE
                item.iv_delete.setOnClickListener {

                    val dialogClickListener = DialogInterface.OnClickListener { _, which ->
                        when (which) {
                            DialogInterface.BUTTON_POSITIVE -> {
                                EventBus.getDefault().post(market.id?.let { item -> OnDeleteBookmarkEvent(item) })
                                EventBus.getDefault().post(OnBookmarksCountChangedEvent())
                                Toast.makeText(context, context.getString(R.string.bookmark_deleted_successful), Toast.LENGTH_SHORT).show()
                            }

                            DialogInterface.BUTTON_NEGATIVE -> {
                                //noOp
                            }
                        }
                    }

                    val builder = AlertDialog.Builder(context)
                    builder.setMessage(context.getString(R.string.really_want_to_delete_event)).setPositiveButton("Ja", dialogClickListener)
                            .setNegativeButton("Nein", dialogClickListener).show()
                }
            }
        }
    }
}