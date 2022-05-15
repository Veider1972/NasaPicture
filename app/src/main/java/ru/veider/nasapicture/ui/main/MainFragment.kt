package ru.veider.nasapicture.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import coil.api.load
import ru.veider.nasapicture.R
import ru.veider.nasapicture.const.WIKI_SEARCH_BACKSTACK
import ru.veider.nasapicture.databinding.MainFragmentBinding
import ru.veider.nasapicture.repository.nasa.NasaRepositoryImpl
import ru.veider.nasapicture.ui.search.WikiSearchFragment

class MainFragment : Fragment(R.layout.main_fragment) {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel: MainViewModel by viewModels { MainViewModelFactory(NasaRepositoryImpl()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            viewModel.requestPOD()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = MainFragmentBinding.bind(view)

        binding.bottomSheet.wikiSearchText.apply {
            setEndIconOnClickListener {
                this.editText?.apply {
                    if (text.toString().trim().isEmpty())
                        Toast.makeText(requireContext(), context.getString(R.string.wiki_search_word_empty), Toast.LENGTH_LONG).show()
                    else
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.container, WikiSearchFragment.newInstance(text.toString()))
                            .addToBackStack(WIKI_SEARCH_BACKSTACK)
                            .commit()
                }
            }
        }

        viewLifecycleOwner.lifecycle.coroutineScope.launchWhenStarted {
            viewModel.loading.collect {
                binding.apply {
                    loading.root.visibility = if (it) View.VISIBLE else View.GONE
                    main.visibility = if (it) View.GONE else View.VISIBLE
                    bottomSheet.bottomSheetLayout.visibility = if (it) View.GONE else View.VISIBLE
                }
            }
        }

        viewLifecycleOwner.lifecycle.coroutineScope.launchWhenStarted {
            viewModel.error.collect {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }

        viewLifecycleOwner.lifecycle.coroutineScope.launchWhenStarted {
            viewModel.image.collect {
                it?.let {
                    binding.apply {
                        image.load(it.hdUrl)
                        title.text = String.format("%s:", it.title)
                        bottomSheet.description.text = it.explanation
                    }
                }
            }
        }
    }
}