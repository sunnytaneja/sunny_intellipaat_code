package com.android.myapplication.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {

    @Query(
        """
        SELECT c.id AS id, c.title AS title, c.instructor AS instructor, c.lessonCount AS lessonCount,
               (SELECT COUNT(*) FROM lessons l WHERE l.courseId = c.id AND l.isCompleted = 1) AS completedLessons
        FROM courses c
        ORDER BY c.id
        """
    )
    fun observeCourses(): Flow<List<CourseWithProgress>>

    @Query(
        """
        SELECT c.id AS id, c.title AS title, c.instructor AS instructor, c.lessonCount AS lessonCount,
               (SELECT COUNT(*) FROM lessons l WHERE l.courseId = c.id AND l.isCompleted = 1) AS completedLessons
        FROM courses c
        WHERE c.id = :courseId
        """
    )
    fun observeCourse(courseId: Int): Flow<CourseWithProgress?>

    @Query("SELECT * FROM lessons WHERE courseId = :courseId ORDER BY position")
    fun observeLessons(courseId: Int): Flow<List<LessonEntity>>

    // Upsert (not REPLACE) so the foreign key cascade doesn't wipe lessons on refresh.
    @Upsert
    suspend fun upsertCourses(courses: List<CourseEntity>)

    // IGNORE keeps lessons the user already completed offline.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLessons(lessons: List<LessonEntity>)

    @Query("UPDATE lessons SET isCompleted = 1 WHERE id = :lessonId")
    suspend fun markLessonCompleted(lessonId: Int)
}
