package com.android.myapplication.domain.usecase

import com.android.myapplication.domain.model.Course
import com.android.myapplication.domain.repository.CourseRepository
import kotlinx.coroutines.flow.Flow

class ObserveCoursesUseCase(private val repository: CourseRepository) {
    operator fun invoke(): Flow<List<Course>> = repository.observeCourses()
}
