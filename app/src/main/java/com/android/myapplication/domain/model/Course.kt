package com.android.myapplication.domain.model

import com.android.myapplication.domain.util.ProgressCalculator

data class Course(
    val id: Int,
    val title: String,
    val instructor: String,
    val lessonCount: Int,
    val completedLessons: Int
) {
    val progress: Int
        get() = ProgressCalculator.percent(completedLessons, lessonCount)
}
