package de.feine_medien.flohmarkt.rights

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import de.feine_medien.flohmarkt.BuildConfig
import de.feine_medien.flohmarkt.R
import kotlinx.android.synthetic.main.fragment_agb.*


class AgbFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_agb, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val webViewClient = WebViewClient()
        wv_agb.webViewClient = webViewClient

        val webSettings = wv_agb.settings
        webSettings.javaScriptEnabled = true

        wv_agb.loadUrl("file:///android_asset/tc.htm")
    }

    companion object {
        fun newInstance(): AgbFragment {
            return newInstance()
        }
    }
}
