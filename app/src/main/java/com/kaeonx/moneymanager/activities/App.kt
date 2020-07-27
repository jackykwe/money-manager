package com.kaeonx.moneymanager.activities

import android.app.Application

// Courtesy of https://stackoverflow.com/a/58627769/7254995
class App : Application() {

    companion object {
        lateinit var context: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }
}