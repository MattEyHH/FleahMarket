package de.feine_medien.flohmarkt.search

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.feine_medien.flohmarkt.R
import de.feine_medien.flohmarkt.event.OnLoadAllMarketsSuccessfulEvent
import de.feine_medien.flohmarkt.event.OnNoGeoPermissionGivenEvent
import de.feine_medien.flohmarkt.model.Market
import kotlinx.android.synthetic.main.fragment_search.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class SearchFragment : Fragment() {

    private lateinit var events: List<Market>
    private lateinit var searchListFragment: SearchListFragment

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
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    @Subscribe(sticky = true)
    fun onEvent(event: OnLoadAllMarketsSuccessfulEvent) {
        events = event.markets
        loadListFragment()

        progress.visibility = View.GONE
    }

    @Subscribe(sticky = true)
    fun onEvent(event: OnNoGeoPermissionGivenEvent) {
        loadListFragment()

        progress.visibility = View.GONE
    }

    private fun loadListFragment() {
        searchListFragment = SearchListFragment()

        fragmentManager?.beginTransaction()?.add(R.id.fl_search, searchListFragment)?.commit()
    }

    companion object {
        fun newInstance(): SearchFragment {
            return newInstance()
        }
    }
}
