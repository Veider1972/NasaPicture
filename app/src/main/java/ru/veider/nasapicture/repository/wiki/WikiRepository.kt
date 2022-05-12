package ru.veider.nasapicture.repository.wiki

interface WikiRepository {
    suspend fun wikiSearch(word : String): WikiResponse
}