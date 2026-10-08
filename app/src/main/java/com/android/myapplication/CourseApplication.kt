package com.android.myapplication

import android.app.Application
import com.android.myapplication.di.AppContainer

class CourseApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
