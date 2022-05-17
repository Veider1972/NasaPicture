package ru.veider.nasapicture.repository.nasa

interface NasaRepository {
    suspend fun pod(date:String): PODResponse
}