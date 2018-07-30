package flohmarkt.feine_medien.de.flohmarkttermine.search

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import flohmarkt.feine_medien.de.flohmarkttermine.Event.OnLoadAllEventsSuccessfulEvent
import flohmarkt.feine_medien.de.flohmarkttermine.R
import flohmarkt.feine_medien.de.flohmarkttermine.model.Market
import kotlinx.android.synthetic.main.fragment_search.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class SearchFragment : Fragment() {

    private var searchAdapter: SearchAdapter? = null
    private lateinit var events: Any

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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

    private fun setupRecyclerView(markets: List<Market>) {
        searchAdapter = SearchAdapter(markets)
        rv_search.layoutManager = object : LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) {
            override fun canScrollHorizontally(): Boolean {
                return false
            }
        }
        rv_search.adapter = searchAdapter
    }

    @Subscribe(sticky = true)
    fun onEvent(event: OnLoadAllEventsSuccessfulEvent) {
        events = event.events

        EventBus.getDefault().removeStickyEvent(event)
    }

    companion object {
        fun newInstance(): SearchFragment {
            return newInstance()
        }
    }
}
