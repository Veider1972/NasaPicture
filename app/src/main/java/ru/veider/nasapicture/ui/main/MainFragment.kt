package ru.veider.nasapicture.ui.main

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.transition.*
import coil.api.load
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.main_fragment.*
import ru.veider.nasapicture.R
import ru.veider.nasapicture.databinding.MainFragmentBinding
import ru.veider.nasapicture.repository.nasa.NasaRepositoryImpl
import ru.veider.nasapicture.ui.ANIMATION_DURATION
import ru.veider.nasapicture.ui.DAY
import ru.veider.nasapicture.ui.WIKI_SEARCH_BACKSTACK
import ru.veider.nasapicture.ui.search.WikiSearchFragment
import java.text.SimpleDateFormat
import java.util.*

class MainFragment : Fragment(R.layout.main_fragment) {

    private lateinit var binding: MainFragmentBinding
    private var isImageExpanded = false


    companion object {
        var instance: MainFragment? = null
        fun newInstance() : MainFragment {
            if (instance == null) instance = MainFragment()
            return instance!!
        }
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

    private fun animateImageOn() {
        binding.imageLayout.apply {
            ObjectAnimator.ofFloat(this, "translationX", resources.getDimension(R.dimen.translation_image), 0f).apply {
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

        binding.image.setOnClickListener {
            isImageExpanded = !isImageExpanded
            TransitionManager.beginDelayedTransition(
                main_layout, TransitionSet()
                    .addTransition(ChangeBounds())
                    .addTransition(ChangeImageTransform())
            )
            val params: ViewGroup.LayoutParams = binding.image.layoutParams
            params.height = if (isImageExpanded) binding.main.height else ViewGroup.LayoutParams.WRAP_CONTENT
            binding.image.apply {
                layoutParams = params
                scaleType = if (isImageExpanded) ImageView.ScaleType.CENTER_CROP else ImageView.ScaleType.FIT_CENTER
            }
        }

        binding.wikiShow.setOnClickListener {
            TransitionManager.beginDelayedTransition(binding.main, Slide(Gravity.START))

            binding.wikiSearchText.visibility = if (binding.wikiSearchText.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            wikiEffect()
        }

        with(binding.bottomNavigationView) {
            selectedItemId = R.id.today
            setOnItemSelectedListener { item ->
                animateImageOn()
                when (item.itemId) {
                    R.id.ereyesterday -> viewModel.requestPOD(getDay(DAY.EREYESTERDAY))
                    R.id.yesterday    -> viewModel.requestPOD(getDay(DAY.YESTERDAY))
                    R.id.today        -> viewModel.requestPOD(getDay(DAY.TODAY))
                }
                true
            }
            if (savedInstanceState == null)
                bottomNavigationIn(this)
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
                        animateImageOn()
                        title.text = String.format("%s:", it.title)
                        bottomSheet.description.text = it.explanation
                    }
                }
            }
        }
    }

    private fun wikiEffect() {
        if (binding.wikiSearchText.visibility == View.VISIBLE) {
            ObjectAnimator.ofFloat(binding.wikiShow, "rotation", 0f, -180f).start()
        } else {
            ObjectAnimator.ofFloat(binding.wikiShow, "rotation", -180f, 0f).start()
        }
    }

    private fun bottomNavigationIn(bottomNavigationView: BottomNavigationView) {
        bottomNavigationView.translationY = resources.getInteger(R.integer.fab_rotation).toFloat()
        ValueAnimator.ofFloat(resources.getDimension(R.dimen.translation_toolbar), 0f).apply {
            duration = ANIMATION_DURATION
            addUpdateListener {
                bottomNavigationView.translationY = this.animatedValue as Float
            }
        }.start()
    }
}
