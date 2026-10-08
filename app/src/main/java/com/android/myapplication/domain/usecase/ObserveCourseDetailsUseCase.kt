package com.android.myapplication.domain.usecase

import com.android.myapplication.domain.model.CourseDetails
import com.android.myapplication.domain.repository.CourseRepository
import kotlinx.coroutines.flow.Flow

class ObserveCourseDetailsUseCase(private val repository: CourseRepository) {
    operator fun invoke(courseId: Int): Flow<CourseDetails?> =
        repository.observeCourseDetails(courseId)
}
