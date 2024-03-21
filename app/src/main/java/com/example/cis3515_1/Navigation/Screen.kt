package com.example.cis3515_1.Navigation

sealed class Screen(val route: String)
{
    object Splash : Screen("splash_screen")
    object Home : Screen("home_screen")
    object Club: Screen("club_screen")
    object UpcomingEvent: Screen("UpcomingEvent")
    object LogIn: Screen("LogIn")
    object InformationSession:  Screen("Information Session")
    object Discussion: Screen("Discussion/{filter}")
    {
        fun createRoute(filter: String = "All") = "Discussion/$filter"
    }

    object Account: Screen("Account")
    object Notifications: Screen("Notifications")
    object AddPost: Screen("AddPost")

    object PostDetail : Screen("postDetail/{postId}")
    {
        fun createRoute(postId: String) = "postDetail/$postId"
    }

    object DiscussionSearch: Screen("DiscussionSearch")
    object RegisterScreen: Screen("RegisterScreen")
}