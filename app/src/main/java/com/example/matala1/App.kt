package com.example.matala1

import android.app.Application
import com.example.matala1.utilities.SharedPreferencesManager

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // האתחול הגלובלי יתבצע כאן, מיד כשהאפליקציה עולה לאוויר!
        SharedPreferencesManager.init(this)
    }
}