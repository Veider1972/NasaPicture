package ru.veider.nasapicture.repository.note

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.veider.nasapicture.MainApplication

class NoteDataSource {

    private val NOTES = "NOTES"

    companion object {
        @JvmStatic
        fun getInstance() = NoteDataSource()
    }

    fun loadNotes(): ArrayList<Note> {
        MainApplication.getInstance()?.applicationContext?.apply {
            val preferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
            val str = preferences.getString(NOTES, "")
            if (str != "") {
                val type = object : TypeToken<ArrayList<Note>>() {}.type
                return Gson().fromJson(str, type)
            }
        }
        return ArrayList()
    }

    fun saveNotes(notes: ArrayList<Note>) {
        MainApplication.getInstance()?.applicationContext?.apply {
            val editor = getSharedPreferences(packageName, Context.MODE_PRIVATE).edit()
            val str = Gson().toJson(notes)
            editor.putString(NOTES, str)
            editor.apply()
        }
    }

}