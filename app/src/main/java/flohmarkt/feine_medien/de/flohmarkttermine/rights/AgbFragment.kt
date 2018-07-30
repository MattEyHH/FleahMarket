package flohmarkt.feine_medien.de.flohmarkttermine.rights

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import flohmarkt.feine_medien.de.flohmarkttermine.BuildConfig
import flohmarkt.feine_medien.de.flohmarkttermine.R
import kotlinx.android.synthetic.main.fragment_agb.*


class AgbFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_agb, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val webViewClient = WebViewClient()
        wv_agb.setWebViewClient(webViewClient)

        val webSettings = wv_agb.getSettings()
        webSettings.setJavaScriptEnabled(true)

        wv_agb.loadUrl(BuildConfig.AGB_URL)
    }

    companion object {
        fun newInstance(): AgbFragment {
            return newInstance()
        }
    }
}
