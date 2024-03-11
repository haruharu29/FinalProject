package com.example.cis3515_1.Screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.cis3515_1.BottomNavigationBar
import com.example.cis3515_1.TopNavigationBar

@Composable
fun InformationSession(modifier: Modifier = Modifier, navController: NavHostController)
{
    Scaffold(topBar = { TopNavigationBar() }, bottomBar = { BottomNavigationBar(navController) })
    { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        )
        {
            Text(text = "Information Session")
        }
    }
}