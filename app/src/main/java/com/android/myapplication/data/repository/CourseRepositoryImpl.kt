package com.android.myapplication.data.repository

import androidx.room.withTransaction
import com.android.myapplication.data.local.AppDatabase
import com.android.myapplication.data.local.CourseEntity
import com.android.myapplication.data.local.LessonEntity
import com.android.myapplication.data.remote.CourseApi
import com.android.myapplication.domain.model.Course
import com.android.myapplication.domain.model.CourseDetails
import com.android.myapplication.domain.repository.CourseRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlin.math.roundToInt

class CourseRepositoryImpl(
    private val api: CourseApi,
    private val database: AppDatabase
) : CourseRepository {

    private val dao = database.courseDao()

    override fun observeCourses(): Flow<List<Course>> =
        dao.observeCourses().map { rows -> rows.map { it.toDomain() } }

    override fun observeCourseDetails(courseId: Int): Flow<CourseDetails?> =
        combine(dao.observeCourse(courseId), dao.observeLessons(courseId)) { course, lessons ->
            course?.let { CourseDetails(it.toDomain(), lessons.map { lesson -> lesson.toDomain() }) }
        }

    override suspend fun refreshCourses(): Result<Unit> {
        return try {
            val remoteCourses = api.getCourses()
            val remoteLessons = remoteCourses.associate { it.id to api.getLessons(it.id) }

            val courses = remoteCourses.map {
                CourseEntity(it.id, it.title, it.instructor, it.lessons)
            }
            val lessons = remoteCourses.flatMap { course ->
                // The API only gives a percentage, so use it to seed completed lessons the
                // first time a course is stored. Later refreshes keep the local state.
                val seededCompleted = (course.progress * course.lessons / 100f).roundToInt()
                remoteLessons.getValue(course.id).mapIndexed { index, lesson ->
                    LessonEntity(
                        id = lesson.id,
                        courseId = course.id,
                        title = lesson.title,
                        position = index,
                        isCompleted = index < seededCompleted
                    )
                }
            }

            database.withTransaction {
                dao.upsertCourses(courses)
                dao.insertLessons(lessons)
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markLessonCompleted(lessonId: Int) {
        dao.markLessonCompleted(lessonId)
    }
}
