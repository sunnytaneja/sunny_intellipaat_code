package com.android.myapplication.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.android.myapplication.presentation.courses.CourseListViewModel
import com.android.myapplication.presentation.details.CourseDetailsViewModel
import com.android.myapplication.presentation.login.LoginViewModel

class ViewModelFactories(private val container: AppContainer) {

    fun login(): ViewModelProvider.Factory = viewModelFactory {
        initializer { LoginViewModel(container.loginUseCase) }
    }

    fun courseList(): ViewModelProvider.Factory = viewModelFactory {
        initializer {
            CourseListViewModel(container.observeCoursesUseCase, container.refreshCoursesUseCase)
        }
    }

    fun courseDetails(courseId: Int): ViewModelProvider.Factory = viewModelFactory {
        initializer {
            CourseDetailsViewModel(
                courseId,
                container.observeCourseDetailsUseCase,
                container.markLessonCompletedUseCase
            )
        }
    }
}
