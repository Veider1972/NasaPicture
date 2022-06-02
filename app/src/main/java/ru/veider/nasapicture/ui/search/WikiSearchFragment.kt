package ru.veider.nasapicture.ui.search

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import ru.veider.nasapicture.R
import ru.veider.nasapicture.databinding.WikiSearchFragmentBinding
import ru.veider.nasapicture.repository.wiki.WikiRepositoryImpl
import ru.veider.nasapicture.repository.wiki.WikiResponse
import ru.veider.nasapicture.ui.wiki.WikiFragment

class WikiSearchFragment : Fragment(R.layout.wiki_search_fragment) {

    private val viewModel: WikiSearchViewModel by viewModels { WikiSearchViewModelFactory(WikiRepositoryImpl()) }

    companion object {
        const val PARAM: String = "PARAM"
        fun newInstance(searchingWord: String): WikiSearchFragment {
            return WikiSearchFragment().apply {
                arguments = Bundle().apply {
                    putString(PARAM, searchingWord)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            arguments?.getString(PARAM, "")?.apply {
                viewModel.requestSearchedWord(this)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = WikiSearchFragmentBinding.bind(view)

        viewLifecycleOwner.lifecycle.coroutineScope.launchWhenStarted {
            viewModel.loading.collect {
                with(binding) {
                    loading.root.visibility = if (it) View.VISIBLE else View.GONE
                    main.visibility = if (it) View.GONE else View.VISIBLE
                }
            }
        }

        viewLifecycleOwner.lifecycle.coroutineScope.launchWhenStarted {
            viewModel.error.collect {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }

        viewLifecycleOwner.lifecycle.coroutineScope.launchWhenStarted {
            viewModel.searchedWord.collect {
                it?.let {
                    with(binding) {
                        val viewPage = wordsView
                        viewPage.adapter = PagerAdapter(this@WikiSearchFragment, it)
                        TabLayoutMediator(wordsTab, viewPage) { tab, position ->
                            tab.text = it.title[position]
                        }.attach()
                    }
                }
            }
        }
    }

    inner class PagerAdapter(fragment: Fragment, private val wikiResponse: WikiResponse) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = wikiResponse.url.size

        override fun createFragment(position: Int): Fragment = WikiFragment.newInstance(wikiResponse.url[position])
    }
}