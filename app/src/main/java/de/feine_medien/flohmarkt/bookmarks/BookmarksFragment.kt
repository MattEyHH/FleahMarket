package de.feine_medien.flohmarkt.bookmarks

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.feine_medien.flohmarkt.R


class BookmarksFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bookmarks, container, false)
    }

    fun onButtonPressed(uri: Uri) {

    }

    companion object {
        fun newInstance(): BookmarksFragment {
            return newInstance()
        }
    }
}
