package ru.veider.nasapicture

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.veider.nasapicture.databinding.MainActivityBinding
import ru.veider.nasapicture.ui.PREFERENCES
import ru.veider.nasapicture.ui.THEME_GREEN
import ru.veider.nasapicture.ui.THEME_RED
import ru.veider.nasapicture.ui.main.MainFragment
import ru.veider.nasapicture.ui.note.NoteFragment

class MainActivity : AppCompatActivity() {

    private var showPicture = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadTheme()

        val binding = MainActivityBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.toolbar.menu. apply {
            findItem(R.id.green_theme).setOnMenuItemClickListener {
                saveTheme(THEME_GREEN)
                recreate()
                true
            }
            findItem(R.id.red_theme).setOnMenuItemClickListener {
                saveTheme(THEME_RED)
                recreate()
                true
            }
            findItem(R.id.note).setOnMenuItemClickListener {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, NoteFragment.newInstance())
                    .commitNow()
                showPicture = true
                findItem(R.id.note).isVisible = false
                findItem(R.id.image).isVisible = true
                binding.toolbar.title = resources.getString(R.string.notes)
                true
            }
            findItem(R.id.image).setOnMenuItemClickListener {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commit()
                showPicture = false
                findItem(R.id.image).isVisible = false
                findItem(R.id.note).isVisible = true
                binding.toolbar.title = resources.getString(R.string.app_name)
                true
            }
        }
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

    private fun saveTheme(str: String) {
        getSharedPreferences(packageName, Context.MODE_PRIVATE)
            .edit()
            .putString(PREFERENCES, if (str == THEME_GREEN) THEME_GREEN else THEME_RED)
            .apply()
    }
}