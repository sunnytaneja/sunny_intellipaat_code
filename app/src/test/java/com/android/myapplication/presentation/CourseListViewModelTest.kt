package com.android.myapplication.presentation

import com.android.myapplication.domain.model.Course
import com.android.myapplication.domain.model.CourseDetails
import com.android.myapplication.domain.repository.CourseRepository
import com.android.myapplication.domain.usecase.ObserveCoursesUseCase
import com.android.myapplication.domain.usecase.RefreshCoursesUseCase
import com.android.myapplication.presentation.courses.CourseListUiState
import com.android.myapplication.presentation.courses.CourseListViewModel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class CourseListViewModelTest {

    private val course =
        Course(1, "Python Programming", "John Smith", lessonCount = 20, completedLessons = 13)

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(cached: List<Course>, refreshResult: Result<Unit>): CourseListViewModel {
        val repository = FakeCourseRepository(cached, refreshResult)
        return CourseListViewModel(ObserveCoursesUseCase(repository),
            RefreshCoursesUseCase(repository)
        )
    }

    @Test
    fun `keeps showing saved courses when the refresh fails offline`(): Unit = runTest {
        val vm = viewModel(listOf(course), Result.failure(IOException("offline")))

        val state = vm.uiState.first { it is CourseListUiState.Success && !it.isRefreshing }

        state as CourseListUiState.Success
        assertEquals(listOf(course), state.courses)
        assertTrue(state.showingCachedData)
    }

    @Test
    fun `shows an error when the refresh fails and nothing is cached`() = runTest {
        val vm = viewModel(emptyList(), Result.failure(IOException("offline")))

        val state = vm.uiState.first { it !is CourseListUiState.Loading }

        assertTrue(state is CourseListUiState.Error)
    }

    @Test
    fun `shows the empty state when the server returns no courses`() = runTest {
        val vm = viewModel(emptyList(), Result.success(Unit))

        val state = vm.uiState.first { it !is CourseListUiState.Loading }

        assertEquals(CourseListUiState.Empty, state)
    }
}

private class FakeCourseRepository(
    cached: List<Course>,
    private val refreshResult: Result<Unit>
) : CourseRepository {

    private val courses = MutableStateFlow(cached)

    override fun observeCourses(): Flow<List<Course>> = courses

    override fun observeCourseDetails(courseId: Int): Flow<CourseDetails?> = flowOf(null)

    override suspend fun refreshCourses(): Result<Unit> = refreshResult

    override suspend fun markLessonCompleted(lessonId: Int) = Unit
}
