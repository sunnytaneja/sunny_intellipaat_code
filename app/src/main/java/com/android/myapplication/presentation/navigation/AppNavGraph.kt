package com.android.myapplication.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.android.myapplication.di.ViewModelFactories
import com.android.myapplication.presentation.courses.CourseListRoute
import com.android.myapplication.presentation.courses.CourseListViewModel
import com.android.myapplication.presentation.details.CourseDetailsRoute
import com.android.myapplication.presentation.details.CourseDetailsViewModel
import com.android.myapplication.presentation.login.LoginRoute
import com.android.myapplication.presentation.login.LoginViewModel

private object Routes {
    const val LOGIN = "login"
    const val COURSES = "courses"
    const val COURSE_ID_ARG = "courseId"
    const val DETAILS = "courses/{$COURSE_ID_ARG}"

    fun details(courseId: Int) = "courses/$courseId"
}

@Composable
fun AppNavGraph(
    factories: ViewModelFactories,
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = Routes.LOGIN) {

        composable(Routes.LOGIN) {
            LoginRoute(
                viewModel = viewModel<LoginViewModel>(factory = factories.login()),
                onLoggedIn = {
                    navController.navigate(Routes.COURSES) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.COURSES) {
            CourseListRoute(
                viewModel = viewModel<CourseListViewModel>(factory = factories.courseList()),
                onCourseClick = { courseId -> navController.navigate(Routes.details(courseId)) }
            )
        }

        composable(
            route = Routes.DETAILS,
            arguments = listOf(navArgument(Routes.COURSE_ID_ARG) { type = NavType.IntType })
        ) { entry ->
            val courseId = entry.arguments?.getInt(Routes.COURSE_ID_ARG) ?: return@composable
            CourseDetailsRoute(
                viewModel = viewModel<CourseDetailsViewModel>(factory = factories.courseDetails(courseId)),
                onBack = { navController.popBackStack() }
            )
        }
    }
}
