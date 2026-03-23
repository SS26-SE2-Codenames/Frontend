package com.codenames.codenames_frontend.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable

@Composable
fun NavGraph(){
    val navController = rememberNavController();

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ){
        composable(Screen.Home.route) {
            //call matching Composable, e.g.
            //HomeScreen(navController)
        }
    }
}