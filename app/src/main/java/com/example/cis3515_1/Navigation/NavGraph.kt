package com.example.cis3515_1.Navigation

import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.cis3515_1.AccountScreen
import com.example.cis3515_1.Screens.AddPostScreen
import com.example.cis3515_1.BottomNavigationBar
import com.example.cis3515_1.Screens.AnimatedSplashScreen
import com.example.cis3515_1.Screens.Clubs
import com.example.cis3515_1.Screens.Contact
import com.example.cis3515_1.Screens.CourseSchedule
import com.example.cis3515_1.Screens.Discussion
import com.example.cis3515_1.Screens.DiscussionSearch
import com.example.cis3515_1.Screens.FloorGuide
import com.example.cis3515_1.Screens.HomeScreen
import com.example.cis3515_1.Screens.LogIn
import com.example.cis3515_1.Screens.LostAndFoundStaff
import com.example.cis3515_1.Screens.PostDetailScreen
import com.example.cis3515_1.Screens.UpcomingEvent
import com.example.cis3515_1.TopNavigationBar
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupNavGraph(onClick: suspend () -> Unit, navController: NavHostController)
{
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(route = Screen.Splash.route) {
            AnimatedSplashScreen(navController = navController)
        }

        composable(route = Screen.Account.route)
        {
            AccountScreen(navController = navController)
        }

        composable(route = Screen.LoggedIn.route)
        {
            LogIn(userEmail = Firebase.auth.currentUser, onLogout =
            {
                Firebase.auth.signOut()
                navController.navigate(Screen.Account.route)
            }, navController = navController)
        }

        composable(route = Screen.Home.route) {
            HomeScreen(onClick = onClick, navController = navController)
        }

        composable(route = Screen.Club.route)
        {
            Clubs(onClick = onClick, navController = navController)
        }

        composable(route = Screen.UpcomingEvent.route)
        {
            UpcomingEvent(onClick = onClick, navController = navController)
        }

        composable(route = Screen.CourseSchedule.route)
        {
            CourseSchedule(onClick = onClick, navController = navController)
        }

        composable(route = Screen.Contact.route)
        {
            Contact(onClick = onClick, navController = navController)
        }

        composable(route = Screen.FloorGuide.route)
        {
            FloorGuide(onClick = onClick, navController = navController)
        }

        composable(route = Screen.Canvas.route)
        {
            WebViewScreen("https://templeu.instructure.com", onClick = onClick, navController = navController)
        }
        
        composable(route = Screen.Outlook.route)
        {
            WebViewScreen(url = "https://outlook.office.com/mail/", onClick = onClick, navController = navController)
           /* val localUriHandler = LocalUriHandler.current
            localUriHandler.openUri("https://outlook.office.com/mail/")*/
        }
        
        composable(route = Screen.CourseSchedule.route)
        {
            WebViewScreen(url = "https://www.tuj.ac.jp/ug/academics/semester-info/schedule", onClick = onClick, navController = navController)
        }

        composable(
            route = Screen.Discussion.route,
            arguments = listOf(navArgument("filter") { defaultValue = "All"; type = NavType.StringType })
        ) { backStackEntry ->
            val filter = backStackEntry.arguments?.getString("filter") ?: "All"
            Discussion(navController = navController, selectedFilter = filter, onClick = onClick)
        }


        composable(route = Screen.AddPost.route)
        {
            AddPostScreen(navController = navController, onClick = onClick)
        }

        composable(route = Screen.PostDetail.route, arguments = listOf(navArgument("postId") { type = NavType.StringType }))
        { backStackEntry ->
            PostDetailScreen(postId = backStackEntry.arguments?.getString("postId") ?: "", navController = navController, onClick = onClick)
        }

        composable(route = Screen.DiscussionSearch.route)
        {
            DiscussionSearch(navController = navController, onClick = onClick)
        }

        composable(
            route = Screen.LostAndFound_Staff.route,
            arguments = listOf(navArgument("filter") { defaultValue = "All"; type = NavType.StringType })
        ) { backStackEntry ->
            val filter = backStackEntry.arguments?.getString("filter") ?: "All"
            LostAndFoundStaff(navController = navController, selectedFilter = filter, onClick = onClick)
        }

    }
}

@Composable
fun WebViewScreen(url: String, modifier: Modifier = Modifier, onClick: suspend () -> Unit, navController: NavHostController)
{
    Scaffold(topBar = { TopNavigationBar(onClick = onClick) }, bottomBar = { BottomNavigationBar(navController) })
    { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        )
        {
            var backEnabled by remember { mutableStateOf(false) }
            var webView: WebView? = null

            AndroidView(factory = {
                WebView(it).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    //webViewClient = WebViewClient()
                    webChromeClient = WebChromeClient()
                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                            backEnabled = view.canGoBack()
                        }
                    }
                    loadUrl(url)
                    settings.javaScriptEnabled = true
                }

            }, update = {
                it.loadUrl(url)
            })

            BackHandler(enabled = backEnabled) {
                webView?.goBack()
            }
        }
    }

}