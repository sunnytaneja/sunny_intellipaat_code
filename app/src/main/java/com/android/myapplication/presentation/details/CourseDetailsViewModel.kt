package com.android.myapplication.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.myapplication.domain.model.CourseDetails
import com.android.myapplication.domain.usecase.MarkLessonCompletedUseCase
import com.android.myapplication.domain.usecase.ObserveCourseDetailsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface CourseDetailsUiState {
    data object Loading : CourseDetailsUiState
    data object NotFound : CourseDetailsUiState
    data class Success(val details: CourseDetails) : CourseDetailsUiState
}

class CourseDetailsViewModel(
    courseId: Int,
    observeCourseDetails: ObserveCourseDetailsUseCase,
    private val markLessonCompleted: MarkLessonCompletedUseCase
) : ViewModel() {

    val uiState: StateFlow<CourseDetailsUiState> = observeCourseDetails(courseId)
        .map { details ->
            if (details == null) CourseDetailsUiState.NotFound else CourseDetailsUiState.Success(details)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CourseDetailsUiState.Loading)

    fun onMarkCompleted(lessonId: Int) {
        viewModelScope.launch { markLessonCompleted(lessonId) }
    }
}
