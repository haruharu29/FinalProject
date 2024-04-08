package com.example.cis3515_1

import android.text.Layout.Alignment
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.cis3515_1.Navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscussionTopNavigationBar(modifier: Modifier = Modifier, navController: NavController,
                               onFilterSelected: (String) -> Unit,)
{
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    TopAppBar(colors = TopAppBarDefaults.topAppBarColors(),

            title = {
                if(currentRoute == Screen.Discussion.route) {
                    Text(
                        text = "Discussion",
                        fontWeight = FontWeight.Bold
                    )
                }
                else if(currentRoute == Screen.AddPost.route){
                    Text(
                        text = "Add Post",
                        fontWeight = FontWeight.Bold
                    )
                }
                else if(currentRoute == Screen.DiscussionSearch.route){
                    Text(
                        text = "Search",
                        fontWeight = FontWeight.Bold
                    )
                }

            },

            navigationIcon = {
                if (currentRoute == Screen.Discussion.route) {
                    IconButton(onClick = { onDiscussionNavigationIconClicked() }) {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = "Toggle Drawer")
                    }
                } else {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            },

            actions = {
                val showFilterMenu = remember { mutableStateOf(false) }
                if(currentRoute == Screen.Discussion.route) {
                    IconButton(onClick = { navController.navigate("DiscussionSearch") }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                    }
                        IconButton(onClick = { showFilterMenu.value = !showFilterMenu.value }) {
                        Icon(imageVector = Icons.Default.FilterAlt, contentDescription = "Category Filter")
                    }
                    DropdownMenu(
                        expanded = showFilterMenu.value,
                        onDismissRequest = { showFilterMenu.value = false }
                    ) {
                        DropdownMenuItem(text = { Text("All") }, onClick = {
                            onFilterSelected("All")
                            navController.navigate(Screen.Discussion.createRoute("All"))
                            showFilterMenu.value = false
                        })
                        DropdownMenuItem(text = { Text("Course Selection") }, onClick = {
                            onFilterSelected("Course Selection")
                            navController.navigate(Screen.Discussion.createRoute("Course Selection"))
                            showFilterMenu.value = false
                        })
                        DropdownMenuItem(text = { Text("Events") }, onClick = {
                            onFilterSelected("Events")
                            navController.navigate(Screen.Discussion.createRoute("Events"))
                            showFilterMenu.value = false
                        })
                        DropdownMenuItem(text = { Text("Clubs") }, onClick = {
                            onFilterSelected("Clubs")
                            navController.navigate(Screen.Discussion.createRoute("Clubs"))
                            showFilterMenu.value = false
                        })
                        DropdownMenuItem(text = { Text("Other College Related") }, onClick = {
                            onFilterSelected("Other College Related")
                            navController.navigate(Screen.Discussion.createRoute("Other College Related"))
                            showFilterMenu.value = false
                        })
                        DropdownMenuItem(text = { Text("Housing") }, onClick = {
                            onFilterSelected("Housing")
                            navController.navigate(Screen.Discussion.createRoute("Housing"))
                            showFilterMenu.value = false
                            Log.e("discussion", "Housing")
                        })
                        DropdownMenuItem(text = { Text("Living") }, onClick = {
                            onFilterSelected("Living")
                            navController.navigate(Screen.Discussion.createRoute("Living"))
                            showFilterMenu.value = false
                        })
                        DropdownMenuItem(text = { Text("Others") }, onClick = {
                            onFilterSelected("Others")
                            navController.navigate(Screen.Discussion.createRoute("Others"))
                            showFilterMenu.value = false
                        })
                    }

                    IconButton(onClick = { onAddPostClicked(navController) }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Post")
                    }




                }
            },

        )


}

fun onDiscussionNavigationIconClicked()
{

}

fun onAddPostClicked(navController: NavController)
{
    navController.navigate("AddPost")
}

