package ru.veider.nasapicture

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import ru.veider.nasapicture.databinding.SplashActivityBinding
import ru.veider.nasapicture.ui.PREFERENCES
import ru.veider.nasapicture.ui.THEME_GREEN
import ru.veider.nasapicture.ui.THEME_RED


@SuppressLint("CustomSplashScreen") class SplashActivity : AppCompatActivity() {

    private val SPLASH_DISPLAY_LENGTH = 3000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadTheme()

        val binder = SplashActivityBinding.inflate(layoutInflater)

        setContentView(binder.root)
        binder.nasa.animate()
            .rotation(360f)
            .setDuration(SPLASH_DISPLAY_LENGTH)
            .start()

        Handler(Looper.getMainLooper()).postDelayed({
                                                        val mainIntent = Intent(this, MainActivity::class.java)
                                                        this.startActivity(mainIntent)
                                                        this.finish()
                                                    }, SPLASH_DISPLAY_LENGTH)
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
}