package com.android.myapplication.presentation.courses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.myapplication.domain.model.Course
import com.android.myapplication.domain.usecase.ObserveCoursesUseCase
import com.android.myapplication.domain.usecase.RefreshCoursesUseCase
import com.android.myapplication.presentation.common.toUserMessage
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface CourseListUiState {
    data object Loading : CourseListUiState
    data object Empty : CourseListUiState
    data class Error(val message: String) : CourseListUiState
    data class Success(
        val courses: List<Course>,
        val isRefreshing: Boolean,
        /** True when the last refresh failed and we're showing the saved copy. */
        val showingCachedData: Boolean
    ) : CourseListUiState
}

private sealed interface RefreshState {
    data object Loading : RefreshState
    data object Idle : RefreshState
    data class Failed(val message: String) : RefreshState
}

class CourseListViewModel(
    observeCourses: ObserveCoursesUseCase,
    private val refreshCourses: RefreshCoursesUseCase
) : ViewModel() {

    private val refreshState = MutableStateFlow<RefreshState>(RefreshState.Loading)
    private var refreshJob: Job? = null

    val uiState: StateFlow<CourseListUiState> =
        combine(observeCourses(), refreshState) { courses, refresh -> toUiState(courses, refresh) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CourseListUiState.Loading)

    init {
        refresh()
    }

    fun refresh() {
        if (refreshJob?.isActive == true) return
        refreshJob = viewModelScope.launch {
            refreshState.value = RefreshState.Loading
            refreshState.value = refreshCourses().fold(
                onSuccess = { RefreshState.Idle },
                onFailure = { RefreshState.Failed(it.toUserMessage()) }
            )
        }
    }
}

private fun toUiState(courses: List<Course>, refresh: RefreshState): CourseListUiState = when {
    courses.isNotEmpty() -> CourseListUiState.Success(
        courses = courses,
        isRefreshing = refresh is RefreshState.Loading,
        showingCachedData = refresh is RefreshState.Failed
    )
    refresh is RefreshState.Loading -> CourseListUiState.Loading
    refresh is RefreshState.Failed -> CourseListUiState.Error(refresh.message)
    else -> CourseListUiState.Empty
}
