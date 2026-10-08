package com.android.myapplication.domain.repository

import com.android.myapplication.domain.model.Course
import com.android.myapplication.domain.model.CourseDetails
import kotlinx.coroutines.flow.Flow

interface CourseRepository {
    /** Emits whatever is stored locally; updates whenever the cache changes. */
    fun observeCourses(): Flow<List<Course>>

    /** Emits null when the course is not in the local store. */
    fun observeCourseDetails(courseId: Int): Flow<CourseDetails?>

    /** Pulls fresh data from the API into the local store. */
    suspend fun refreshCourses(): Result<Unit>

    suspend fun markLessonCompleted(lessonId: Int)
}
