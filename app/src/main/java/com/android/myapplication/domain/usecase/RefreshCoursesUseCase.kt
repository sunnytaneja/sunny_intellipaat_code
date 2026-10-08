package com.android.myapplication.domain.usecase

import com.android.myapplication.domain.repository.CourseRepository

class RefreshCoursesUseCase(private val repository: CourseRepository) {
    suspend operator fun invoke(): Result<Unit> = repository.refreshCourses()
}
