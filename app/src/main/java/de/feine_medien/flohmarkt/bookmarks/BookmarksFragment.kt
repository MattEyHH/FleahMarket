package de.feine_medien.flohmarkt.bookmarks

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.feine_medien.flohmarkt.R
import de.feine_medien.flohmarkt.event.OnLoadSavedMarketsFromPreferencesEvent
import de.feine_medien.flohmarkt.model.Market
import de.feine_medien.flohmarkt.search.SearchAdapter
import kotlinx.android.synthetic.main.fragment_bookmarks.*
import kotlinx.android.synthetic.main.fragment_search_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class BookmarksFragment : Fragment() {

    private lateinit var bookmarkAdapter: BookmarkAdapter

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
        bookmarkAdapter = BookmarkAdapter(activity, markets.toMutableList())
        rv_search.layoutManager = object : LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) {
            override fun canScrollHorizontally(): Boolean {
                return false
            }
        }
        rv_bookmarks.adapter = bookmarkAdapter
        rv_bookmarks.adapter?.notifyDataSetChanged()
    }

    @Subscribe(sticky = true)
    fun onEvent(event: OnLoadSavedMarketsFromPreferencesEvent) {
        setupRecyclerView(event.markets)
    }
}
