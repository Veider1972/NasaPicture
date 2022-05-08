package ru.veider.nasapicture.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.veider.nasapicture.repository.wiki.WikiRepository
import ru.veider.nasapicture.repository.wiki.WikiResponse
import java.io.IOException

class WikiSearchViewModel(private val wikiRepository: WikiRepository) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: Flow<Boolean> = _loading

    private val _searchedWord : MutableStateFlow<WikiResponse?> = MutableStateFlow(null)
    val searchedWord: Flow<WikiResponse?> = _searchedWord

    private val _error : MutableSharedFlow<String> = MutableSharedFlow()
    val error: Flow<String> = _error

    fun requestSearchedWord(searchedWord:String){
        viewModelScope.launch {
            _loading.emit(true)
            try {
                _searchedWord.emit(wikiRepository.wikiSearch(searchedWord))
            } catch (e: IOException){
                _error.emit(e.message.toString())
            }
            _loading.emit(false)
        }
    }
}