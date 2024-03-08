package com.example.cis3515_1.Navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cis3515_1.AccountScreen
import com.example.cis3515_1.AnimatedSplashScreen
import com.example.cis3515_1.Clubs
import com.example.cis3515_1.Discussion
import com.example.cis3515_1.HomeScreen
import com.example.cis3515_1.UpcomingEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(route = Screen.Splash.route) {
            AnimatedSplashScreen(navController = navController)
        }
        composable(route = Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(route = Screen.Club.route)
        {
            Clubs(navController = navController)
        }

        composable(route = Screen.UpcomingEvent.route)
        {
            UpcomingEvent()
        }

        composable(route = Screen.Account.route)
        {
            AccountScreen(navController = navController)
        }

        composable(route = Screen.Discussion.route)
        {
            Discussion(navController = navController)
        }


    }
}