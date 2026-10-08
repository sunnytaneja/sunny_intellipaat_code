package com.android.myapplication.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val instructor: String,
    val lessonCount: Int
)

@Entity(
    tableName = "lessons",
    foreignKeys = [
        ForeignKey(
            entity = CourseEntity::class,
            parentColumns = ["id"],
            childColumns = ["courseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("courseId")]
)
data class LessonEntity(
    @PrimaryKey val id: Int,
    val courseId: Int,
    val title: String,
    val position: Int,
    val isCompleted: Boolean
)

/** Query result: a course plus the number of lessons completed locally. */
data class CourseWithProgress(
    val id: Int,
    val title: String,
    val instructor: String,
    val lessonCount: Int,
    val completedLessons: Int
)
