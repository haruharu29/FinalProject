package com.example.cis3515_1.Navigation

sealed class Screen(val route: String)
{
    object Splash : Screen("splash_screen")
    object LoggedIn: Screen("Logged In")
    object Home : Screen("home_screen")
    object Club: Screen("Club")
    object AddClub: Screen("AddClub")
    object UpcomingEvent: Screen("UpcomingEvent")
    object LogIn: Screen("LogIn")
    object RegisterScreen: Screen("RegisterScreen")
    object CourseSchedule:  Screen("Course Schedule")
    object AcademicCalendar: Screen("Academic Calendar")
    object Discussion: Screen("Discussion/{filter}")
    {
        fun createRoute(filter: String = "All") = "Discussion/$filter"
    }

    object Notifications: Screen("Notifications")
    object AddPost: Screen("AddPost")

    object PostDetail: Screen("postDetail/{postId}")
    {
        fun createRoute(postId: String) = "postDetail/$postId"
    }

    object StudentResourcesSearch: Screen("StudentResourcesSearch")
    object DiscussionSearch: Screen("DiscussionSearch")
    object Account: Screen("Account")
    object Contact: Screen("Contact")
    object FloorGuide: Screen("Floor Guide")
    object Canvas: Screen("Canvas")
    object Outlook: Screen("Outlook")
    object LostAndFound: Screen("LostAndFound")
    object StudentResources: Screen("StudentResources/{filter}")
    {
        fun createRoute(filter: String = "All") = "StudentResources/$filter"
    }
    object AddPostLostAndFound: Screen("AddPost_Lost")
    object AddPostStudentResources: Screen("AddPost_StudentResources")

    object PostDetail_LostAndFound: Screen("postDetail_lost/{postId}")
    {
        fun createRoute(postId: String) = "postDetail_lost/$postId"
    }

    object PostDetail_StudentResources: Screen("postDetail_studentResources/{postId}")
    {
        fun createRoute(postId: String) = "postDetail_studentResources/$postId"
    }
}