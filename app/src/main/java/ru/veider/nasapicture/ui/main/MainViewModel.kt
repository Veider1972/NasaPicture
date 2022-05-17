package ru.veider.nasapicture.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.veider.nasapicture.repository.nasa.PODResponse
import ru.veider.nasapicture.repository.nasa.NasaRepository
import java.io.IOException

class MainViewModel(private val nasaRepository: NasaRepository) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: Flow<Boolean> = _loading

    private val _image : MutableStateFlow<PODResponse?> = MutableStateFlow(null)
    val image: Flow<PODResponse?> = _image

    private val _error : MutableSharedFlow<String> = MutableSharedFlow()
    val error: Flow<String> = _error

    fun requestPOD(date : String){
        viewModelScope.launch {
            _loading.emit(true)
            try {
                _image.emit(nasaRepository.pod(date))
            } catch (e: IOException){
                _error.emit(e.message.toString())
            }
            _loading.emit(false)
        }
    }
}