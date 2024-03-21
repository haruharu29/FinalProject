package com.example.cis3515_1


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun Clubs(modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier, navController: NavHostController)
{
    Scaffold(topBar = { TopNavigationBar()}, bottomBar = { BottomNavigationBar(navController) })
    { padding ->
        Column(
            modifier = androidx.compose.ui.Modifier
                .fillMaxSize()
                .padding(padding)
        )
        {}
    }
}