package de.feine_medien.flohmarkt.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import de.feine_medien.flohmarkt.R
import de.feine_medien.flohmarkt.event.*
import de.feine_medien.flohmarkt.model.Market
import kotlinx.android.synthetic.main.fragment_search_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class SearchListFragment : Fragment() {

    private var searchAdapter: SearchAdapter? = null

    private lateinit var bottomSheetDialogFragment: FilterBottomSheetDialogFragment

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

        return inflater.inflate(R.layout.fragment_search_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdMobView()
        hideOtherLayouts()

        bottomSheetDialogFragment = FilterBottomSheetDialogFragment()

        btn_change.setOnClickListener {
            if (!bottomSheetDialogFragment.isAdded) {
                bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.tag)
                showProgress(true)
            }
        }
    }

    private fun hideOtherLayouts() {
        tv_no_city_or_zip.visibility = View.GONE
        tv_no_results.visibility = View.GONE
    }

    private fun setupRecyclerView(markets: List<Market>) {
        hideOtherLayouts()
        rv_search.visibility = View.VISIBLE
        searchAdapter = SearchAdapter(activity, markets.toMutableList())
        rv_search.layoutManager = object : LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) {
            override fun canScrollHorizontally(): Boolean {
                return false
            }
        }
        showProgress(false)
        rv_search.adapter = searchAdapter
        rv_search.adapter?.notifyDataSetChanged()
    }

    private fun setupAdMobView() {
        MobileAds.initialize(activity, getString(R.string.admob_app_id))
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private fun showProgress(show: Boolean) {
        if (show) progress.visibility = View.VISIBLE else progress.visibility = View.GONE
    }

    @SuppressLint("SetTextI18n")
    @Subscribe
    fun onEvent(event: OnRadiusValueHasChangedEvent) {
        tv_search_radius.text = "${getString(R.string.search_radius)} ${event.progress}km"
    }

    @Subscribe
    fun onEvent(event: OnSearchedForMarketsByCurrentPositionEvent) {
        tv_city.text = getString(R.string.current_location)
    }

    @Subscribe
    fun onEvent(event: OnSearchedForMarketsByCityOrZipEvent) {
        tv_city.text = event.cityOrZip
    }

    @Subscribe(sticky = true)
    fun onEvent(event: OnSearchByLastSearchedCityEvent) {
        tv_city.text = event.city
    }

    @Subscribe(sticky = true)
    fun onEvent(event: OnNoResultsFoundEvent) {
        showProgress(false)
        rv_search.visibility = View.INVISIBLE
        tv_no_results.visibility = View.VISIBLE

        EventBus.getDefault().removeStickyEvent(event)
    }

    @Subscribe(sticky = true)
    fun onEvent(event: OnNoZipOrCitySelectedEvent) {
        showProgress(false)
        tv_no_city_or_zip.visibility = View.VISIBLE

        EventBus.getDefault().removeStickyEvent(event)
    }

    @Subscribe(sticky = true)
    fun onEvent(event: OnLoadAllMarketsSuccessfulEvent) {
        setupRecyclerView(event.markets)
    }

    companion object {
        fun newInstance(): SearchFragment {
            return newInstance()
        }
    }
}
