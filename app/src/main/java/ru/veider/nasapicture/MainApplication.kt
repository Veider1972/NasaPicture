package ru.veider.nasapicture

import android.app.Application

class MainApplication:Application() {
    companion object {
        private var application: Application? = null
        fun getInstance() = application
    }

    override fun onCreate() {
        application = this
        super.onCreate()
    }
}