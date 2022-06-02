package ru.veider.nasapicture.ui.main

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
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
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.main_fragment.*
import ru.veider.nasapicture.R
import ru.veider.nasapicture.const.TAG
import ru.veider.nasapicture.databinding.MainFragmentBinding
import ru.veider.nasapicture.repository.nasa.NasaRepositoryImpl
import ru.veider.nasapicture.ui.ANIMATION_DURATION
import ru.veider.nasapicture.ui.WIKI_SEARCH_BACKSTACK
import ru.veider.nasapicture.ui.note.NoteFragment
import ru.veider.nasapicture.ui.search.WikiSearchFragment
import java.text.SimpleDateFormat
import java.util.*

class MainFragment : Fragment(R.layout.main_fragment) {

    private lateinit var binding: MainFragmentBinding
    private var isImageExpanded = false

    private val viewModel: MainViewModel by viewModels { MainViewModelFactory(NasaRepositoryImpl()) }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }

    private fun getDay(day: DAY): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
        when (day) {
            DAY.TODAY        -> {}
            DAY.YESTERDAY    -> calendar.add(Calendar.DATE, -1)
            DAY.EREYESTERDAY -> calendar.add(Calendar.DATE, -2)
        }
        return dateFormat.format(calendar.time)
    }

    private fun animateImageOn() {
        Log.d(TAG, "animateImageOn")
        binding.imageLayout.apply {
            ObjectAnimator.ofFloat(this, "translationX", resources.getDimension(R.dimen.translation_image), 0f).apply {
                duration = ANIMATION_DURATION
                start()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        binding = MainFragmentBinding.bind(view)
        if (savedInstanceState == null) {
            viewModel.requestPOD(getDay(DAY.TODAY))
            animateImageOn()
        }

        binding.pictureTabLayout.apply {
            this.getTabAt(2)?.select()
            if (savedInstanceState == null)
                tabLayoutIn(this)
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    animateImageOn()
                    when (tab?.position) {
                        DAY.EREYESTERDAY.ordinal -> viewModel.requestPOD(getDay(DAY.EREYESTERDAY))
                        DAY.YESTERDAY.ordinal    -> viewModel.requestPOD(getDay(DAY.YESTERDAY))
                        DAY.TODAY.ordinal        -> viewModel.requestPOD(getDay(DAY.TODAY))
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabReselected(tab: TabLayout.Tab?) {}

            })
        }

        binding.image.setOnClickListener {
            isImageExpanded = !isImageExpanded
            TransitionManager.beginDelayedTransition(
                binding.main, TransitionSet()
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
        Log.d(TAG, "wikiEffect")
        if (binding.wikiSearchText.visibility == View.VISIBLE) {
            ObjectAnimator.ofFloat(binding.wikiShow, "rotation", 0f, -180f).start()
        } else {
            ObjectAnimator.ofFloat(binding.wikiShow, "rotation", -180f, 0f).start()
        }
    }

    private fun tabLayoutIn(tabLayout: TabLayout) {
        Log.d(TAG, "tabLayoutIn")
        tabLayout.apply {
            translationY = resources.getDimension(R.dimen.translation_toolbar)
            visibility = View.VISIBLE
            ValueAnimator.ofFloat(resources.getDimension(R.dimen.translation_toolbar), 0f).apply {
                duration = ANIMATION_DURATION
                addUpdateListener {
                    translationY = this.animatedValue as Float
                }
            }.start()
        }
    }
}
