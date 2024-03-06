package com.example.cis3515_1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.example.cis3515_1.Navigation.SetupNavGraph
import com.example.cis3515_1.ui.theme.Cis3515_1Theme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Cis3515_1Theme {

                SetBarColor(color = MaterialTheme.colorScheme.background)

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    //for splash screen
                    val navController = rememberNavController()
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


