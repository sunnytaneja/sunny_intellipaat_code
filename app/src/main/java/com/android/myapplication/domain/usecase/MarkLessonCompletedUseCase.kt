package com.android.myapplication.domain.usecase

import com.android.myapplication.domain.repository.CourseRepository

class MarkLessonCompletedUseCase(private val repository: CourseRepository) {
    suspend operator fun invoke(lessonId: Int) = repository.markLessonCompleted(lessonId)
}
