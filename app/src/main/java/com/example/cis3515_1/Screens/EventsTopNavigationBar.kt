package com.example.cis3515_1

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.cis3515_1.Navigation.Screen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsTopNavigationBar(navController: NavController,  onClick:  suspend () -> Unit)
{
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    Log.d("Navigation", "Current Route: $currentRoute")
    val scope = rememberCoroutineScope()
    TopAppBar(colors = TopAppBarDefaults.topAppBarColors(),

        title = {
            if(currentRoute == Screen.UpcomingEvent.route) {
                Text(
                    text = "Upcoming Events",
                    fontWeight = FontWeight.Bold
                )
            }
            else if(currentRoute == Screen.addEvent.route){
                Text(
                    text = "Add Event",
                    fontWeight = FontWeight.Bold
                )
            }
            else if(currentRoute == Screen.eventsSearch.route){
                Text(
                    text = "Search",
                    fontWeight = FontWeight.Bold
                )
            }

        },

        navigationIcon = {
            if (currentRoute == Screen.UpcomingEvent.route) {
                IconButton(onClick = { scope.launch { onClick() } }) {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = "Toggle Drawer")
                }

            } else {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        },

        actions = {
            if(currentRoute == Screen.UpcomingEvent.route) {
                IconButton(onClick = { navController.navigate("eventsSearch") }) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                }


                val userEmail = Firebase.auth.currentUser?.email ?: ""

                if (userEmail.endsWith("@tuj.temple.edu"))
                {
                    IconButton(onClick = { onAddEventClicked(navController) }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Events")
                    }

                }




            }
        },

        )


}



fun onAddEventClicked(navController: NavController)
{
    navController.navigate("AddEvent")
}