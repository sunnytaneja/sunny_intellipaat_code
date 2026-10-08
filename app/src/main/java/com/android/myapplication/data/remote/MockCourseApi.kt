package com.android.myapplication.data.remote

import android.content.Context
import com.android.myapplication.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

/**
 * Stand-in for a real backend. Reads JSON from assets, adds some latency and
 * fails with an IOException when the device has no connection, so the offline
 * path behaves like it would with a real network client.
 */
class MockCourseApi(
    private val context: Context,
    private val networkMonitor: NetworkMonitor,
    private val latencyMs: Long = 900
) : CourseApi {

    override suspend fun getCourses(): List<CourseDto> {
        checkConnection()
        delay(latencyMs)
        val json = JSONArray(readAsset("courses.json"))
        return List(json.length()) { index ->
            val item = json.getJSONObject(index)
            CourseDto(
                id = item.getInt(context.getString(R.string.id)),
                title = item.getString(context.getString(R.string.title)),
                instructor = item.getString(context.getString(R.string.instructor)),
                progress = item.getInt(context.getString(R.string.progress)),
                lessons = item.getInt(context.getString(R.string.lessons))
            )
        }
    }

    override suspend fun getLessons(courseId: Int): List<LessonDto> {
        checkConnection()
        val json = JSONObject(readAsset("lessons.json")).optJSONArray(courseId.toString())
            ?: return emptyList()
        return List(json.length()) { index ->
            val item = json.getJSONObject(index)
            LessonDto(id = item.getInt(context.getString(R.string.id)), title = item.getString(context.getString(R.string.title)))
        }
    }

    private fun checkConnection() {
        if (!networkMonitor.isOnline()) throw IOException(context.getString(R.string.error_no_connection))
    }

    private suspend fun readAsset(name: String): String = withContext(Dispatchers.IO) {
        context.assets.open(name).bufferedReader().use { it.readText() }
    }
}
