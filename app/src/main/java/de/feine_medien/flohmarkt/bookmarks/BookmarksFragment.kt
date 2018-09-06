package de.feine_medien.flohmarkt.bookmarks

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.feine_medien.flohmarkt.R
import de.feine_medien.flohmarkt.event.OnAllBookmarksDeletedEvent
import de.feine_medien.flohmarkt.event.OnDeleteBookmarkEvent
import de.feine_medien.flohmarkt.model.Market
import de.feine_medien.flohmarkt.util.PreferencesHandler
import kotlinx.android.synthetic.main.fragment_bookmarks.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class BookmarksFragment : Fragment() {

    private lateinit var bookmarkAdapter: BookmarkAdapter
    private lateinit var preferences: PreferencesHandler
    private lateinit var bookmarks: MutableList<Market>

    companion object {
        fun newInstance(): BookmarksFragment {
            return newInstance()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              sasvedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bookmarks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferences = PreferencesHandler(context!!)
        bookmarks = preferences.getSavedMarkets().toMutableList()
        setupRecyclerView(bookmarks)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    private fun setupRecyclerView(markets: List<Market>) {
        if (bookmarks.size == 0) {
            tv_no_bookmarks.visibility = View.VISIBLE
        }
        bookmarkAdapter = BookmarkAdapter(activity, markets.toMutableList(), context!!)
        rv_bookmarks.layoutManager = object : LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) {
            override fun canScrollHorizontally(): Boolean {
                return false
            }
        }
        rv_bookmarks.adapter = bookmarkAdapter
        rv_bookmarks.adapter?.notifyDataSetChanged()
    }


    @Subscribe
    fun onEvent(event: OnAllBookmarksDeletedEvent) {
        bookmarks.forEach {
            preferences.putMarket(it)
        }
        setupRecyclerView(bookmarks)
    }

    @Subscribe
    fun onEvent(event: OnDeleteBookmarkEvent) {
        preferences.deleteMarkets()

        bookmarks.forEachIndexed { index, market ->
            if (market.id == event.id) {
                bookmarks.removeAt(index)
            }
        }
    }
}
