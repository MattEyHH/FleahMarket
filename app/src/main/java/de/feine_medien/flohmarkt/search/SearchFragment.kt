package de.feine_medien.flohmarkt.search

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.feine_medien.flohmarkt.R


class SearchFragment : Fragment() {

    private lateinit var searchListFragment: SearchListFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadListFragment()
    }

    private fun loadListFragment() {
        searchListFragment = SearchListFragment()

        fragmentManager?.beginTransaction()?.replace(R.id.fl_search, searchListFragment)?.commit()
    }

    companion object {
        fun newInstance(): SearchFragment {
            return newInstance()
        }
    }
}
