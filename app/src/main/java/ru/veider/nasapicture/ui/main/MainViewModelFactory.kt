package ru.veider.nasapicture.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.veider.nasapicture.repository.nasa.NasaRepository

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(private val nasaRepository: NasaRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = MainViewModel(nasaRepository) as T
}