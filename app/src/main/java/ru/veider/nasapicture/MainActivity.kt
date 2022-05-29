package ru.veider.nasapicture

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import ru.veider.nasapicture.const.PREFERENCES
import ru.veider.nasapicture.const.THEME_MARSIAN
import ru.veider.nasapicture.const.THEME_NORMAL
import ru.veider.nasapicture.ui.main.MainFragment

class MainActivity : AppCompatActivity() {

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
            preferences.edit().putString(PREFERENCES, THEME_NORMAL).apply()
            str = THEME_NORMAL
        }
        when (str) {
            THEME_NORMAL  -> setTheme(R.style.Nasa_Normal)
            THEME_MARSIAN -> setTheme(R.style.Nasa_Marsian)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.normal_theme  -> {
                saveTheme(THEME_NORMAL)
            }
            R.id.marsian_theme -> {
                saveTheme(THEME_MARSIAN)
            }
        }
        recreate()
        return super.onOptionsItemSelected(item)
    }

    private fun saveTheme(str: String) {
        getSharedPreferences(packageName, Context.MODE_PRIVATE)
            .edit()
            .putString(PREFERENCES, if (str == THEME_NORMAL) THEME_NORMAL else THEME_MARSIAN)
            .apply()
    }
}