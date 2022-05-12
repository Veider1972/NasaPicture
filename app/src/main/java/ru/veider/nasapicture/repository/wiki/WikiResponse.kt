package ru.veider.nasapicture.repository.wiki

data class WikiResponse (
    var query: String = "",
    var title: ArrayList<String> = ArrayList(),
    var url: ArrayList<String> = ArrayList(),
    var image: ArrayList<String> = ArrayList()
)