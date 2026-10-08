package com.android.myapplication.presentation.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.myapplication.domain.model.CourseDetails
import com.android.myapplication.domain.model.Lesson

@Composable
fun CourseDetailsRoute(viewModel: CourseDetailsViewModel, onBack: () -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    CourseDetailsScreen(
        state = state,
        onBack = onBack,
        onMarkCompleted = viewModel::onMarkCompleted
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailsScreen(
    state: CourseDetailsUiState,
    onBack: () -> Unit,
    onMarkCompleted: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Course details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (state) {
                CourseDetailsUiState.Loading ->
                    CircularProgressIndicator(Modifier.align(Alignment.Center))

                CourseDetailsUiState.NotFound ->
                    Text("This course isn't available.", Modifier.align(Alignment.Center))

                is CourseDetailsUiState.Success ->
                    DetailsContent(state.details, onMarkCompleted)
            }
        }
    }
}

@Composable
private fun DetailsContent(details: CourseDetails, onMarkCompleted: (Int) -> Unit) {
    val course = details.course

    LazyColumn(Modifier.fillMaxSize()) {
        item {
            Column(Modifier.padding(16.dp)) {
                Text(course.title, style = MaterialTheme.typography.headlineSmall)
                Text(
                    course.instructor,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(16.dp))
                LinearProgressIndicator(
                    progress = { course.progress / 100f },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "${course.completedLessons} of ${course.lessonCount} lessons completed (${course.progress}%)",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            HorizontalDivider()
        }

        items(details.lessons, key = { it.id }) { lesson ->
            LessonRow(lesson = lesson, onMarkCompleted = { onMarkCompleted(lesson.id) })
            HorizontalDivider()
        }
    }
}

@Composable
private fun LessonRow(lesson: Lesson, onMarkCompleted: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(Modifier.weight(1f)) {
            Text(lesson.title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = if (lesson.isCompleted) "\u2713 Completed" else "\u25CB Pending",
                style = MaterialTheme.typography.bodySmall,
                color = if (lesson.isCompleted) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
        if (!lesson.isCompleted) {
            OutlinedButton(onClick = onMarkCompleted) { Text("Mark done") }
        }
    }
}
