package com.kaeonx.moneymanager.activities

import android.app.Application

// Courtesy of https://stackoverflow.com/a/58627769/7254995
class App : Application() {

    companion object {
        internal lateinit var context: App
            private set

        internal var setPersistenceEnabledCalled = false
    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }
}