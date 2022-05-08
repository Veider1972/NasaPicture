package ru.veider.nasapicture.repository.nasa

import retrofit2.http.GET
import retrofit2.http.Query

interface NasaAPI {
    @GET("/planetary/apod")
    suspend fun getPicture(
        @Query("api_key") apiKey: String
    ): PODResponse
}