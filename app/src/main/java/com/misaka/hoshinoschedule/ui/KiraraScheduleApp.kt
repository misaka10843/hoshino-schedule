package com.misaka.hoshinoschedule.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose AnimatedNavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.misaka.hoshinoschedule.ui.courselist.CourseListRoute
import com.misaka.hoshinoschedule.ui.editor.CourseEditorRoute
import com.misaka.hoshinoschedule.ui.navigation.kiraraBackEnter
import com.misaka.hoshinoschedule.ui.navigation.kiraraBackExit
import com.misaka.hoshinoschedule.ui.navigation.kiraraForwardEnter
import com.misaka.hoshinoschedule.ui.navigation.kiraraForwardExit
import com.misaka.hoshinoschedule.ui.schedule.ScheduleRoute
import com.misaka.hoshinoschedule.ui.settings.SettingsRoute

object Routes {
    const val SCHEDULE = "schedule"
    const val COURSE_EDITOR = "courseEditor"
    const val SETTINGS = "settings"
    const val COURSE_LIST = "courses"
    const val COURSE_ID = "courseId"
}

@Composable
fun KiraraScheduleApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    
    AnimatedNavHost(navController = navController, startDestination = Routes.SCHEDULE) {
        composable(
            route = Routes.SCHEDULE,
            enterTransition = { kiraraBackEnter(context) },
            exitTransition = { kiraraForwardExit(context) },
            popEnterTransition = { kiraraBackEnter(context) },
            popExitTransition = { kiraraForwardExit(context) }
        ) {
            val viewModel: com.misaka.hoshinoschedule.ui.schedule.ScheduleViewModel =
                viewModel(factory = AppViewModelProvider.scheduleFactory)
            ScheduleRoute(
                viewModel = viewModel,
                onAddCourse = { navController.navigate(Routes.COURSE_EDITOR) },
                onEditCourse = { courseId ->
                    navController.navigate("${Routes.COURSE_EDITOR}?${Routes.COURSE_ID}=$courseId")
                },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                onOpenCourseList = { navController.navigate(Routes.COURSE_LIST) }
            )
        }
        composable(
            route = "${Routes.COURSE_EDITOR}?${Routes.COURSE_ID}={${Routes.COURSE_ID}}",
            arguments = listOf(
                navArgument(Routes.COURSE_ID) {
                    type = NavType.LongType
                    defaultValue = -1
                }
            ),
            enterTransition = { kiraraForwardEnter(context) },
            exitTransition = { kiraraBackExit(context) },
            popEnterTransition = { kiraraForwardEnter(context) },
            popExitTransition = { kiraraBackExit(context) }
        ) {
            val viewModel: com.misaka.hoshinoschedule.ui.editor.CourseEditorViewModel =
                viewModel(factory = AppViewModelProvider.courseEditorFactory)
            CourseEditorRoute(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Routes.COURSE_LIST,
            enterTransition = { kiraraForwardEnter(context) },
            exitTransition = { kiraraBackExit(context) },
            popEnterTransition = { kiraraForwardEnter(context) },
            popExitTransition = { kiraraBackExit(context) }
        ) {
            val viewModel: com.misaka.hoshinoschedule.ui.courselist.CourseListViewModel =
                viewModel(factory = AppViewModelProvider.courseListFactory)
            CourseListRoute(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onAddCourse = { navController.navigate(Routes.COURSE_EDITOR) },
                onEditCourse = { courseId ->
                    navController.navigate("${Routes.COURSE_EDITOR}?${Routes.COURSE_ID}=$courseId")
                }
            )
        }
        composable(
            route = Routes.SETTINGS,
            enterTransition = { kiraraForwardEnter(context) },
            exitTransition = { kiraraBackExit(context) },
            popEnterTransition = { kiraraForwardEnter(context) },
            popExitTransition = { kiraraBackExit(context) }
        ) {
            val viewModel: com.misaka.hoshinoschedule.ui.settings.SettingsViewModel =
                viewModel(factory = AppViewModelProvider.settingsFactory)
            SettingsRoute(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
