package com.example.cis3515_1


import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.cis3515_1.Navigation.Screen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscussionTopNavigationBar(
    modifier: Modifier = Modifier, navController: NavController,
    onFilterSelected: (String) -> Unit,
    onClick:  suspend () -> Unit)
{
    val scope = rememberCoroutineScope()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(),
        // top bar title
        title = {
            if (currentRoute == Screen.Discussion.route) {
                Text(
                    text = "Discussion",
                    fontWeight = FontWeight.Bold
                )
            }

            else if (currentRoute == Screen.AddPost.route || currentRoute == Screen.AddPostLostAndFound.route || currentRoute == Screen.AddPostStudentResources.route) {
                Text(
                    text = "Add Post",
                    fontWeight = FontWeight.Bold
                )
            }

            else if (currentRoute == Screen.DiscussionSearch.route || currentRoute == Screen.StudentResourcesSearch.route) {
                Text(
                    text = "Search",
                    fontWeight = FontWeight.Bold
                )
            }

            else if (currentRoute == Screen.LostAndFound.route) {
                Text(
                    text = "Lost And Found",
                    fontWeight = FontWeight.Bold
                )
            }

            else if (currentRoute ==Screen.StudentResources.route)
            {
                Text(
                    text = "Student Resources",
                    fontWeight = FontWeight.Bold,
                    fontSize = 21.sp
                )
            }
        },
        // icon on top left
        navigationIcon = {
            if (currentRoute == Screen.Discussion.route || currentRoute == Screen.LostAndFound.route || currentRoute == Screen.StudentResources.route)
            {
                IconButton(onClick = { scope.launch { onClick() } }) {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = "Toggle Drawer")
                }
            }

            else
            {
                IconButton(onClick = { navController.popBackStack() })
                {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        // icons on top right
        actions = {
            val showFilterMenu = remember { mutableStateOf(false) }

            if (currentRoute == Screen.Discussion.route)
            {
                IconButton(onClick = { navController.navigate("DiscussionSearch") })
                {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                }
                IconButton(onClick = { showFilterMenu.value = !showFilterMenu.value })
                {
                    Icon(
                        imageVector = Icons.Default.FilterAlt,
                        contentDescription = "Category Filter"
                    )
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

                IconButton(onClick = { onAddPostClicked_Discussion(navController) }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Post")
                }
            }

            else if (currentRoute == Screen.StudentResources.route)
            {
                IconButton(onClick = { navController.navigate("StudentResourcesSearch") })
                {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                }

                IconButton(onClick = { showFilterMenu.value = !showFilterMenu.value })
                {
                    Icon(
                        imageVector = Icons.Default.FilterAlt,
                        contentDescription = "Category_filter"
                    )
                }

                DropdownMenu(
                    expanded = showFilterMenu.value,
                    onDismissRequest = { showFilterMenu.value = false }
                ) {
                    DropdownMenuItem(text = { Text("All") }, onClick = {
                        onFilterSelected("All")
                        navController.navigate(Screen.StudentResources.createRoute("All"))
                        showFilterMenu.value = false
                    })
                    DropdownMenuItem(text = { Text("Discounts") }, onClick = {
                        onFilterSelected("Discounts")
                        navController.navigate(Screen.StudentResources.createRoute("Discounts"))
                        showFilterMenu.value = false
                    })
                    DropdownMenuItem(text = { Text("Events") }, onClick = {
                        onFilterSelected("Events")
                        navController.navigate(Screen.StudentResources.createRoute("Events"))
                        showFilterMenu.value = false
                    })
                    DropdownMenuItem(text = { Text("Clubs") }, onClick = {
                        onFilterSelected("Clubs")
                        navController.navigate(Screen.StudentResources.createRoute("Clubs"))
                        showFilterMenu.value = false
                    })
                    DropdownMenuItem(text = { Text("Housing") }, onClick = {
                        onFilterSelected("Housing")
                        navController.navigate(Screen.StudentResources.createRoute("Housing"))
                        showFilterMenu.value = false
                        //Log.e("studentResources", "Housing")
                    })
                    DropdownMenuItem(text = { Text("Living") }, onClick = {
                        onFilterSelected("Living")
                        navController.navigate(Screen.StudentResources.createRoute("Living"))
                        showFilterMenu.value = false
                    })
                    DropdownMenuItem(text = { Text("Others") }, onClick = {
                        onFilterSelected("Others")
                        navController.navigate(Screen.StudentResources.createRoute("Others"))
                        showFilterMenu.value = false
                    })
                }

                val userEmail = Firebase.auth.currentUser?.email ?: ""

                if (userEmail.endsWith("@tuj.temple.edu"))
                {
                    IconButton(onClick = { onAddPostClicked_StudentResources(navController) })
                    {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Post")
                    }
                }
            }

            else if (currentRoute == Screen.LostAndFound.route)
            {

                val userEmail = Firebase.auth.currentUser?.email ?: ""

                if (userEmail.endsWith("@tuj.temple.edu"))
                {
                    IconButton(onClick = { onAddPostClicked_LostAndFound(navController) })
                    {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Post")
                    }
                }

                else if (userEmail.endsWith("@temple.edu"))
                {

                }
            }
        },

        )


}

fun onAddPostClicked_Discussion(navController: NavController)
{
    navController.navigate("AddPost")
}

fun onAddPostClicked_LostAndFound(navController: NavController)
{
    navController.navigate("AddPost_Lost")
}

fun onAddPostClicked_StudentResources(navController: NavController)
{
    navController.navigate("AddPost_StudentResources")
}