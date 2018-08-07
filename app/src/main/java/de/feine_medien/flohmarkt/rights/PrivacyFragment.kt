package de.feine_medien.flohmarkt.rights

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import de.feine_medien.flohmarkt.BuildConfig
import de.feine_medien.flohmarkt.R
import kotlinx.android.synthetic.main.fragment_privacy.*


class PrivacyFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_privacy, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val webViewClient = WebViewClient()
        wv_privacy.webViewClient = webViewClient

        val webSettings = wv_privacy.settings
        webSettings.javaScriptEnabled = true

        wv_privacy.loadUrl(BuildConfig.PRIVACY_URL)
    }

    companion object {
        fun newInstance(): PrivacyFragment {
            return newInstance()
        }
    }
}
