package com.example.cis3515_1

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cis3515_1.Navigation.Screen
import com.example.cis3515_1.Navigation.SetupNavGraph
import com.example.cis3515_1.ui.theme.Cis3515_1Theme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.Firebase
import com.google.firebase.initialize

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Firebase.initialize(this)
        setContent {
            Cis3515_1Theme {

                SetBarColor(color = MaterialTheme.colorScheme.background)

                val navController = rememberNavController()
                var selectedFilter by rememberSaveable { mutableStateOf("All") }

                // Listen to the back stack to determine the current route
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

                Scaffold(
                    topBar = {
                        // Conditionally display the TopNavigationBar based on the current route
                        if (currentRoute == Screen.Discussion.route || currentRoute == Screen.AddPost.route || currentRoute == Screen.PostDetail.route || currentRoute == Screen.DiscussionSearch.route) {
                            DiscussionTopNavigationBar(
                                onFilterSelected = { filter -> selectedFilter = filter },
                                navController = navController)
                        }
                        else if (currentRoute != Screen.Splash.route) {
                            TopNavigationBar()
                        }
                    },
                    bottomBar = {
                        // Conditionally display the BottomNavigationBar based on the current route
                        if (currentRoute != Screen.Splash.route) {
                            BottomNavigationBar(navController)
                        }
                    }
                ) {
                    SetupNavGraph(navController = navController)
                }
            }
        }
    }

    @Composable
    private fun SetBarColor(color: Color)
    {
        val systemUiController = rememberSystemUiController()
        SideEffect { systemUiController.setSystemBarsColor(color = color) }
    }
}


