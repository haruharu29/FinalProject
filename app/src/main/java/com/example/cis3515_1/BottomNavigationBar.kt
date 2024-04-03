package com.example.cis3515_1

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.cis3515_1.Navigation.Screen
import com.example.cis3515_1.data.BottomNavigation

val items = listOf(
    BottomNavigation(
        title = "Home",
        icon = Icons.Rounded.Home,
        route = Screen.Home.route
    ),

    BottomNavigation(
        title = "Notifications",
        icon = Icons.Rounded.Notifications,
        route = Screen.Notifications.route
    ),

    BottomNavigation(
        title = "Log Out",
        icon = Icons.Rounded.AccountCircle,
        route = Screen.LoggedIn.route
    )
)

@Composable
fun BottomNavigationBar(navController: NavController)
{
    NavigationBar {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        Row (modifier = Modifier.background(MaterialTheme.colorScheme.inverseOnSurface)){
            items.forEach { item ->
                val isSelected = item.route == currentRoute
                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        if (!isSelected) {
                            navController.navigate(item.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = "item.title",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    },

                    label =
                    {
                        Text(text = item.title,
                            color = MaterialTheme.colorScheme.onBackground)
                    }
                )
            }
        }
    }
}