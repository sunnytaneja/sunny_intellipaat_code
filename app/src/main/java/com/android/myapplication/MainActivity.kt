package com.android.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import com.android.myapplication.di.ViewModelFactories
import com.android.myapplication.presentation.navigation.AppNavGraph
import com.android.myapplication.ui.theme.MyApplicationTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = (application as CourseApplication).container
        setContent {
            MyApplicationTheme() {
                val factories = remember { ViewModelFactories(container) }
                AppNavGraph(factories)
            }
        }
    }
}