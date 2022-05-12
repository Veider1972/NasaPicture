package ru.veider.nasapicture.repository.wiki

import retrofit2.http.GET
import retrofit2.http.Query

interface WikiAPI {
    @GET("w/api.php?action=opensearch&format=xml")
    suspend fun getPicture(
        @Query("limit") limit: Int,
        @Query("search") search: String
    ): String
}