package ru.veider.nasapicture.repository.nasa

interface NasaRepository {
    suspend fun pod(): PODResponse
}