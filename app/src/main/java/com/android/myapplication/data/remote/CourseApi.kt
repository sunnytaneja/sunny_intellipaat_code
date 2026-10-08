package com.android.myapplication.data.remote

data class CourseDto(
    val id: Int,
    val title: String,
    val instructor: String,
    val progress: Int,
    val lessons: Int
)

data class LessonDto(
    val id: Int,
    val title: String
)

interface CourseApi {
    suspend fun getCourses(): List<CourseDto>
    suspend fun getLessons(courseId: Int): List<LessonDto>
}
