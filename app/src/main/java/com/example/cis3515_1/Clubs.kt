package com.example.cis3515_1


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import java.lang.reflect.Modifier

@Composable
fun Clubs()
{
    Scaffold(topBar = { TopNavigationBar()}, bottomBar = { BottomNavigationBar() })
    { padding ->
        Column(
            modifier = androidx.compose.ui.Modifier
                .fillMaxSize()
                .padding(padding)
        )
        {}
    }
}