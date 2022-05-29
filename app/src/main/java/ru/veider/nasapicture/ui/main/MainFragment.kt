package ru.veider.nasapicture.ui.main

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import coil.api.load
import ru.veider.nasapicture.R
import ru.veider.nasapicture.const.ANIMATION_DURATION
import ru.veider.nasapicture.const.DAY
import ru.veider.nasapicture.const.WIKI_SEARCH_BACKSTACK
import ru.veider.nasapicture.databinding.MainFragmentBinding
import ru.veider.nasapicture.repository.nasa.NasaRepositoryImpl
import ru.veider.nasapicture.ui.search.WikiSearchFragment
import java.text.SimpleDateFormat
import java.util.*

class MainFragment : Fragment(R.layout.main_fragment) {

    private lateinit var binding: MainFragmentBinding

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel: MainViewModel by viewModels { MainViewModelFactory(NasaRepositoryImpl()) }

    private fun getDay(day: DAY): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
        when (day) {
            DAY.TODAY        -> {}
            DAY.YESTERDAY    -> {
                calendar.add(Calendar.DATE, -1)
            }
            DAY.EREYESTERDAY -> {
                calendar.add(Calendar.DATE, -2)
            }
        }
        return dateFormat.format(calendar.time)
    }

    private fun animateImageOn(){
        binding.imageLayout.apply {
            ObjectAnimator.ofFloat(this,"translationX", resources.getDimension(R.dimen.translation_image),0f).apply {
                duration = ANIMATION_DURATION
                start()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = MainFragmentBinding.bind(view)
        if (savedInstanceState == null) {
            viewModel.requestPOD(getDay(DAY.TODAY))
            animateImageOn()
        }

        binding.wikiShow.setOnClickListener {
            binding.wikiSearchText.visibility = if (binding.wikiSearchText.visibility==View.VISIBLE) View.GONE else View.VISIBLE
        }

        with(binding.bottomNavigationView) {
            selectedItemId = R.id.today
            setOnItemSelectedListener { item ->
                animateImageOn()
                binding.wikiSearchText.visibility = View.GONE
                when (item.itemId) {
                    R.id.ereyesterday -> viewModel.requestPOD(getDay(DAY.EREYESTERDAY))
                    R.id.yesterday    -> viewModel.requestPOD(getDay(DAY.YESTERDAY))
                    R.id.today        -> viewModel.requestPOD(getDay(DAY.TODAY))
                }
                true
            }
            ValueAnimator.ofFloat(resources.getDimension(R.dimen.translation_toolbar), 0f).apply {
                duration = ANIMATION_DURATION
                addUpdateListener {
                    binding.bottomNavigationView.translationY = this.animatedValue as Float
                }
            }.start()
        }

        binding.wikiSearchText.apply {
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
                    main.visibility = (if (it) View.GONE else View.VISIBLE).also {
                        bottomSheet.bottomSheetLayout.visibility = it
                        wikiShow.visibility = it
                    }
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