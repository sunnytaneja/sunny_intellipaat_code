package com.android.myapplication.di

import android.content.Context
import com.android.myapplication.data.auth.MockAuthRepository
import com.android.myapplication.data.local.AppDatabase
import com.android.myapplication.data.remote.CourseApi
import com.android.myapplication.data.remote.MockCourseApi
import com.android.myapplication.data.remote.NetworkMonitor
import com.android.myapplication.data.repository.CourseRepositoryImpl
import com.android.myapplication.domain.repository.AuthRepository
import com.android.myapplication.domain.repository.CourseRepository
import com.android.myapplication.domain.usecase.LoginUseCase
import com.android.myapplication.domain.usecase.MarkLessonCompletedUseCase
import com.android.myapplication.domain.usecase.ObserveCourseDetailsUseCase
import com.android.myapplication.domain.usecase.ObserveCoursesUseCase
import com.android.myapplication.domain.usecase.RefreshCoursesUseCase

/**
 * Hand-rolled dependency container. Small enough that a DI framework would be
 * overkill here; swapping this for Hilt later only touches this package.
 */
class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    private val database: AppDatabase = AppDatabase.create(appContext)
    private val courseApi: CourseApi = MockCourseApi(appContext, NetworkMonitor(appContext))

    private val authRepository: AuthRepository = MockAuthRepository()
    private val courseRepository: CourseRepository = CourseRepositoryImpl(courseApi, database)

    val loginUseCase = LoginUseCase(authRepository)
    val observeCoursesUseCase = ObserveCoursesUseCase(courseRepository)
    val refreshCoursesUseCase = RefreshCoursesUseCase(courseRepository)
    val observeCourseDetailsUseCase = ObserveCourseDetailsUseCase(courseRepository)
    val markLessonCompletedUseCase = MarkLessonCompletedUseCase(courseRepository)
}
