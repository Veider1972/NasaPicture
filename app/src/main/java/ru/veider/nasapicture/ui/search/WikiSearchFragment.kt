package ru.veider.nasapicture.ui.search

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import ru.veider.nasapicture.R
import ru.veider.nasapicture.const.WIKI_WORDS_BACKSTACK
import ru.veider.nasapicture.databinding.WikiSearchFragmentBinding
import ru.veider.nasapicture.repository.wiki.WikiRepositoryImpl
import ru.veider.nasapicture.ui.wiki.WikiFragment

class WikiSearchFragment : Fragment(R.layout.wiki_search_fragment), WikiSearchAdapter.WikiSearchHolderEvents {

    private val viewModel: WikiSearchViewModel by viewModels { WikiSearchViewModelFactory(WikiRepositoryImpl()) }

    companion object {
        lateinit var searchingWord: String
        fun newInstance(searchingWord: String): WikiSearchFragment {
            this.searchingWord = searchingWord
            return WikiSearchFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            viewModel.requestSearchedWord(searchingWord)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = WikiSearchFragmentBinding.bind(view)
        binding.wordsView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        viewLifecycleOwner.lifecycle.coroutineScope.launchWhenStarted {
            viewModel.loading.collect {
                binding.loading.root.visibility = if (it) View.VISIBLE else View.GONE
                binding.main.visibility = if (it) View.GONE else View.VISIBLE
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
                    binding.wordQuery.text = it.query
                    binding.wordsView.adapter = WikiSearchAdapter(it, this@WikiSearchFragment)
                }
            }
        }
    }

    override fun onExplanationSelect(url: String) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, WikiFragment.newInstance(url))
            .addToBackStack(WIKI_WORDS_BACKSTACK)
            .commit()
    }
}