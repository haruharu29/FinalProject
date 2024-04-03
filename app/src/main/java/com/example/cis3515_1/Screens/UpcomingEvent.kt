package com.example.cis3515_1.Screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import com.example.cis3515_1.BottomNavigationBar
import com.example.cis3515_1.TopNavigationBar

@Composable
fun UpcomingEvent(modifier: Modifier = Modifier, onClick: suspend () -> Unit, navController: NavHostController)
{
    Scaffold(topBar = { TopNavigationBar(onClick = onClick) }, bottomBar = { BottomNavigationBar(navController) })
    { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        )
        {}
    }

}