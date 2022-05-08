package ru.veider.nasapicture.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import ru.veider.nasapicture.databinding.WikiSearchItemBinding
import ru.veider.nasapicture.repository.wiki.WikiResponse

class WikiSearchAdapter(
    private val wikiResponse: WikiResponse,
    private val wikiSearchHolderEvents: WikiSearchHolderEvents
) : RecyclerView.Adapter<WikiSearchAdapter.WikiSearchHolder>() {

    lateinit var binder: WikiSearchItemBinding

    interface WikiSearchHolderEvents {
        fun onExplanationSelect(url: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WikiSearchHolder {
        binder = WikiSearchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WikiSearchHolder(binder.root)
    }

    override fun onBindViewHolder(holder: WikiSearchHolder, position: Int) {
        holder.bind(wikiResponse.title[position], wikiResponse.image[position], wikiResponse.url[position])
    }

    override fun getItemCount() = wikiResponse.title.size

    inner class WikiSearchHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private lateinit var url: String

        init {
            itemView.setOnClickListener {
                wikiSearchHolderEvents.onExplanationSelect(url)
            }
        }

        fun bind(title: String, image: String, url: String) {
            this.url = url
            binder.apply {
                wikiImage.apply {
                    visibility = if (image.isEmpty()) {
                        View.GONE
                    } else {
                        load(image)
                        View.VISIBLE
                    }
                }
                wikiWord.text = title
            }
        }
    }
}