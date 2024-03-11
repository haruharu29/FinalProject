package com.example.cis3515_1

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cis3515_1.Navigation.Screen
import com.example.cis3515_1.Navigation.SetupNavGraph
import com.example.cis3515_1.ui.theme.Cis3515_1Theme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Cis3515_1Theme {

                SetBarColor(color = MaterialTheme.colorScheme.background)

                val navController = rememberNavController()

                // Listen to the back stack to determine the current route
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

                Scaffold(
                    topBar = {
                        // Conditionally display the TopNavigationBar based on the current route
                        if (currentRoute != Screen.Splash.route) {
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
        SideEffect { systemUiController.setSystemBarsColor(color = color)}

    }
}


