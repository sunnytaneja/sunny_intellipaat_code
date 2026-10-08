package com.android.myapplication.presentation.courses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.myapplication.R
import com.android.myapplication.domain.model.Course

@Composable
fun CourseListRoute(viewModel: CourseListViewModel, onCourseClick: (Int) -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    CourseListScreen(state = state, onRetry = viewModel::refresh, onCourseClick = onCourseClick)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseListScreen(
    state: CourseListUiState,
    onRetry: () -> Unit,
    onCourseClick: (Int) -> Unit
) {
    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.my_course)) }) }) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (state) {
                CourseListUiState.Loading ->
                    CircularProgressIndicator(Modifier.align(Alignment.Center))

                CourseListUiState.Empty -> MessageState(
                    title = stringResource(R.string.no_courses_yet),
                    message = stringResource(R.string.course_enroll),
                    actionLabel = "Refresh",
                    onAction = onRetry,
                    modifier = Modifier.align(Alignment.Center)
                )

                is CourseListUiState.Error -> MessageState(
                    title = stringResource(R.string.could_not_load_courses),
                    message = state.message,
                    actionLabel = stringResource(R.string.retry),
                    onAction = onRetry,
                    modifier = Modifier.align(Alignment.Center)
                )

                is CourseListUiState.Success -> CourseList(state, onCourseClick)
            }
        }
    }
}

@Composable
private fun CourseList(state: CourseListUiState.Success, onCourseClick: (Int) -> Unit) {
    Column(Modifier.fillMaxSize()) {
        if (state.isRefreshing) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
        }
        if (state.showingCachedData) {
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    stringResource(R.string.couldn_t_refresh_showing_saved_courses),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.courses, key = { it.id }) { course ->
                CourseCard(course = course, onContinue = { onCourseClick(course.id) })
            }
        }
    }
}

@Composable
private fun CourseCard(course: Course, onContinue: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(
                course.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                course.instructor,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { course.progress / 100f },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${course.progress}% complete", style = MaterialTheme.typography.bodySmall)
                Text("${course.lessonCount} lessons", style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(12.dp))
            Button(onClick = onContinue, modifier = Modifier.align(Alignment.End)) {
                Text("Continue")
            }
        }
    }
}

@Composable
private fun MessageState(
    title: String,
    message: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Text(
            message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = onAction) { Text(actionLabel) }
    }
}
