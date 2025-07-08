package com.example.testassessment.technicalassignment.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.testassessment.technicalassignment.domain.model.Airline
import com.example.testassessment.technicalassignment.presentation.ui.AirlineDetailScreen
import com.example.testassessment.technicalassignment.presentation.ui.AirlineListScreen
import com.example.testassessment.technicalassignment.presentation.viewmodel.AirlineViewModel

/**
 * Sets up the app's navigation graph with list and detail screens.
 */

@Composable
fun NavGraph(navController: NavHostController) {
    val viewModel: AirlineViewModel = hiltViewModel()
    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            AirlineListScreen(viewModel) { airline ->
                navController.currentBackStackEntry?.savedStateHandle?.set("airline", airline)
                navController.navigate("detail")
            }
        }
        composable("detail") {
            val airline =
                navController.previousBackStackEntry?.savedStateHandle?.get<Airline>("airline")
            airline?.let { AirlineDetailScreen(it) }
        }
    }
}