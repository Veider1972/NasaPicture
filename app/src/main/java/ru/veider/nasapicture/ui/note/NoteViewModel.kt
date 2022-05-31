package ru.veider.nasapicture.ui.note

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.veider.nasapicture.repository.note.Note
import ru.veider.nasapicture.repository.note.NoteDataSource
import kotlin.collections.ArrayList


class NoteViewModel : ViewModel() {

    private val notes: MutableLiveData<ArrayList<Note>> = MutableLiveData()
    private val repo = NoteDataSource.getInstance()

    fun getNotes() = notes

    init {
        viewModelScope.launch {
            notes.postValue(repo.loadNotes())
        }
    }

    fun putNotes(notes:ArrayList<Note>) {
        viewModelScope.apply {
            repo.saveNotes(notes)
        }
    }
}