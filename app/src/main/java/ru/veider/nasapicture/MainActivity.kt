package ru.veider.nasapicture

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import ru.veider.nasapicture.ui.PREFERENCES
import ru.veider.nasapicture.ui.THEME_GREEN
import ru.veider.nasapicture.ui.THEME_RED
import ru.veider.nasapicture.ui.main.MainFragment
import ru.veider.nasapicture.ui.note.NoteFragment

class MainActivity : AppCompatActivity() {

    lateinit var menu: Menu
    var showPicture = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadTheme()
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }

    private fun loadTheme() {
        val preferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
        var str = preferences.getString(PREFERENCES, "")
        if (str!!.isEmpty()) {
            preferences.edit().putString(PREFERENCES, THEME_GREEN).apply()
            str = THEME_GREEN
        }
        when (str) {
            THEME_GREEN -> setTheme(R.style.Nasa_Green)
            THEME_RED   -> setTheme(R.style.Nasa_Red)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (showPicture)
            menuInflater.inflate(R.menu.notes_main_menu, menu)
        else
            menuInflater.inflate(R.menu.image_main_menu, menu)
        this.menu = menu!!
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.normal_theme  -> {
                saveTheme(THEME_GREEN)
                recreate()
                return true
            }
            R.id.marsian_theme -> {
                saveTheme(THEME_RED)
                recreate()
                return true
            }
            R.id.image         -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow()
                showPicture = false
                invalidateOptionsMenu()
                return true
            }
            R.id.note          -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, NoteFragment.newInstance())
                    .commitNow()
                showPicture = true
                invalidateOptionsMenu()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        return super.onPrepareOptionsMenu(menu)
    }

    private fun saveTheme(str: String) {
        getSharedPreferences(packageName, Context.MODE_PRIVATE)
            .edit()
            .putString(PREFERENCES, if (str == THEME_GREEN) THEME_GREEN else THEME_RED)
            .apply()
    }
}