package de.feine_medien.flohmarkt.search

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.feine_medien.flohmarkt.R
import de.feine_medien.flohmarkt.event.OnLoadAllMarketsSuccessfulEvent
import de.feine_medien.flohmarkt.model.Market
import kotlinx.android.synthetic.main.fragment_search_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class SearchListFragment : Fragment() {

    private var searchAdapter: SearchAdapter? = null

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_list, container, false)
    }

    private fun setupRecyclerView(markets: List<Market>) {
        searchAdapter = SearchAdapter(activity, markets)
        rv_search.layoutManager = object : LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) {
            override fun canScrollHorizontally(): Boolean {
                return false
            }
        }
        rv_search.adapter = searchAdapter
    }

    @Subscribe(sticky = true)
    fun onEvent(event: OnLoadAllMarketsSuccessfulEvent) {
        setupRecyclerView(event.markets)

        EventBus.getDefault().removeStickyEvent(event)
    }

    companion object {
        fun newInstance(): SearchFragment {
            return newInstance()
        }
    }
}
