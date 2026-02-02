package com.vaibhavpawade.jobtrack.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.vaibhavpawade.jobtrack.ui.features.addedit.AddEditJobScreen
import com.vaibhavpawade.jobtrack.ui.features.jobdetail.JobDetailScreen
import com.vaibhavpawade.jobtrack.ui.features.joblist.JobListScreen

object AppDestinations {
    const val JOB_LIST_ROUTE = "job_list"
    const val JOB_DETAIL_ROUTE = "job_detail"
    const val ADD_EDIT_JOB_ROUTE = "add_edit_job"
    const val JOB_ID_ARG = "jobId"
}

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = AppDestinations.JOB_LIST_ROUTE
    ) {
        composable(AppDestinations.JOB_LIST_ROUTE) {
            JobListScreen(
                onAddJobClick = { navController.navigate(AppDestinations.ADD_EDIT_JOB_ROUTE) },
                onJobClick = { jobId ->
                    navController.navigate("${AppDestinations.JOB_DETAIL_ROUTE}/$jobId")
                }
            )
        }
        composable(
            route = "${AppDestinations.JOB_DETAIL_ROUTE}/{${AppDestinations.JOB_ID_ARG}}",
            arguments = listOf(navArgument(AppDestinations.JOB_ID_ARG) { type = NavType.LongType })
        ) {
            JobDetailScreen(
                onNavigateBack = { navController.navigateUp() },
                onEditJob = { jobId: Long ->
                    navController.navigate("${AppDestinations.ADD_EDIT_JOB_ROUTE}?${AppDestinations.JOB_ID_ARG}=$jobId")
                }
            )
        }
        composable(
            route = "${AppDestinations.ADD_EDIT_JOB_ROUTE}?${AppDestinations.JOB_ID_ARG}={${AppDestinations.JOB_ID_ARG}}",
            arguments = listOf(
                navArgument(AppDestinations.JOB_ID_ARG) {
                    type = NavType.LongType
                    defaultValue = -1L // Indicates new job
                }
            )
        ) {
            AddEditJobScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NavGraphPreview() {
    NavGraph()
}
