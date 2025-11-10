package com.example.egog

import android.app.Application
import com.google.firebase.FirebaseApp

class EGOGApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}

