package com.example.movies

import android.app.Application
import com.example.movies.database.AppContainer
import com.example.movies.database.DefaultAppContainer

class MovieDBApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}