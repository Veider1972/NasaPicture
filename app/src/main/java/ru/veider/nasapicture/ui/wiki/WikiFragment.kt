package ru.veider.nasapicture.ui.wiki

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import ru.veider.nasapicture.R
import ru.veider.nasapicture.databinding.WikiFragmentBinding

class WikiFragment : Fragment(R.layout.wiki_fragment) {

    companion object {
        lateinit var gotoUrl: String
        fun newInstance(url: String): WikiFragment {
            gotoUrl = url
            return WikiFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        WikiFragmentBinding.bind(view).wikiView.apply {
            webViewClient = WebViewClient()
            if (gotoUrl.isNotEmpty()) {
                loadUrl(gotoUrl) }
        }
    }
}