package de.feine_medien.flohmarkt.rights

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import de.feine_medien.flohmarkt.BuildConfig
import de.feine_medien.flohmarkt.R
import kotlinx.android.synthetic.main.fragment_imprint.*

class ImprintFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_imprint, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val webViewClient = WebViewClient()
        wv_imprint.webViewClient = webViewClient

        val webSettings = wv_imprint.settings
        webSettings.javaScriptEnabled = true

        wv_imprint.loadUrl(BuildConfig.IMPRINT_URL)
    }

    companion object {
        fun newInstance(): ImprintFragment {
            return newInstance()
        }
    }
}


