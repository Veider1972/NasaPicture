

package ru.veider.nasapicture.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.veider.nasapicture.repository.wiki.WikiRepository

@Suppress("UNCHECKED_CAST")
class WikiSearchViewModelFactory (private val wikiRepository: WikiRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = WikiSearchViewModel(wikiRepository) as T
}