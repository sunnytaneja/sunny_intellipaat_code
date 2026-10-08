package com.android.myapplication.data.repository

import com.android.myapplication.data.local.CourseWithProgress
import com.android.myapplication.data.local.LessonEntity
import com.android.myapplication.domain.model.Course
import com.android.myapplication.domain.model.Lesson

fun CourseWithProgress.toDomain() = Course(
    id = id,
    title = title,
    instructor = instructor,
    lessonCount = lessonCount,
    completedLessons = completedLessons
)

fun LessonEntity.toDomain() = Lesson(
    id = id,
    courseId = courseId,
    title = title,
    isCompleted = isCompleted
)
