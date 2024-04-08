package com.example.cis3515_1.Navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer

import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.cis3515_1.AccountScreen
import com.example.cis3515_1.AddPostScreen
import com.example.cis3515_1.AnimatedSplashScreen
import com.example.cis3515_1.Clubs
import com.example.cis3515_1.Discussion
import com.example.cis3515_1.DiscussionSearch
import com.example.cis3515_1.HomeScreen
import com.example.cis3515_1.Notifications
import com.example.cis3515_1.PostDetailScreen
import com.example.cis3515_1.RegisterScreen
import com.example.cis3515_1.UpcomingEvent
import com.example.cis3515_1.addEventScreen
import com.example.cis3515_1.eventsDetailsScreen
import com.example.cis3515_1.eventsSearch

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

        composable(route = Screen.Notifications.route)
        {
            Notifications(navController = navController)
        }

        composable(route = Screen.Account.route)
        {
            AccountScreen(navController = navController)
        }


        composable(
            route = Screen.Discussion.route,
            arguments = listOf(navArgument("filter") { defaultValue = "All"; type = NavType.StringType })
        ) { backStackEntry ->
            val filter = backStackEntry.arguments?.getString("filter") ?: "All"
            Discussion(navController = navController, selectedFilter = filter)
        }


        composable(route = Screen.AddPost.route)
        {
            AddPostScreen(navController = navController)
        }

        composable(route = Screen.PostDetail.route, arguments = listOf(navArgument("postId") { type = NavType.StringType }))
        { backStackEntry ->
            PostDetailScreen(postId = backStackEntry.arguments?.getString("postId") ?: "", navController = navController)
        }

        composable(route = Screen.DiscussionSearch.route)
        {
            DiscussionSearch(navController = navController)
        }

        composable(route = Screen.RegisterScreen.route)
        {
            RegisterScreen(navController = navController)
        }

        composable(route = Screen.UpcomingEvent.route)
        {
            UpcomingEvent(navController = navController)
        }

        composable(route = Screen.addEvent.route)
        {
            addEventScreen( navController = navController)
        }

        composable(route = Screen.eventsSearch.route)
        {
            eventsSearch(navController = navController)
        }

        composable(route = Screen.eventsDetailsScreen.route, arguments = listOf(navArgument("eventId") { type = NavType.StringType }))
        { backStackEntry ->
            eventsDetailsScreen(eventId = backStackEntry.arguments?.getString("eventId") ?: "", navController = navController)
        }

    }
}