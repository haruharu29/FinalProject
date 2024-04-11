package com.example.cis3515_1

import Model.NavigationItem
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.outlined.Accessibility
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cis3515_1.Navigation.NavBarBody
import com.example.cis3515_1.Navigation.NavBarHeader
import com.example.cis3515_1.Navigation.Screen
import com.example.cis3515_1.Navigation.SetupNavGraph
import com.example.cis3515_1.ui.theme.Cis3515_1Theme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Cis3515_1Theme {

                SetBarColor(color = MaterialTheme.colorScheme.background)

                val navController = rememberNavController()
                var selectedFilter by rememberSaveable { mutableStateOf("All") }

                // Listen to the back stack to determine the current route
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

                //for navigation pane

                val items: List<NavigationItem> = listOf(
                    NavigationItem(
                        title = "Canvas",
                        route = Screen.Canvas.route,
                        selectedIcon = ImageVector.vectorResource(id = R.drawable.canvas_logo),
                        unselectedIcon = ImageVector.vectorResource(id = R.drawable.canvas_logo),
                    ),

                    NavigationItem(
                        title = "Outlook",
                        route = Screen.Outlook.route,
                        selectedIcon = ImageVector.vectorResource(id = R.drawable.outlook_filled),
                        unselectedIcon = ImageVector.vectorResource(id = R.drawable.outlook_logo)
                    ),
                    NavigationItem(
                        title = "Academic Calendar",
                        route = Screen.AcademicCalendar.route,
                        selectedIcon = Icons.Filled.CalendarMonth,
                        unselectedIcon =  Icons.Outlined.CalendarMonth
                    ),
                    NavigationItem(
                        title = "Floor Guide",
                        route = Screen.FloorGuide.route,
                        selectedIcon = Icons.Filled.Business,
                        unselectedIcon = Icons.Outlined.Business
                    ),
                    NavigationItem(
                        title = "Student Resources",
                        route = Screen.StudentResources.createRoute("All"),
                        selectedIcon = Icons.Filled.Accessibility,
                        unselectedIcon = Icons.Outlined.Accessibility
                    ),
                    NavigationItem(
                        title = "Lost and Found",
                        route = Screen.LostAndFound.route,
                        selectedIcon = ImageVector.vectorResource(id = R.drawable.lost_found),
                        unselectedIcon = ImageVector.vectorResource(id = R.drawable.lost_found)
                    ),
                    NavigationItem(
                        title = "Contact",
                        route = Screen.Contact.route,
                        selectedIcon = Icons.Filled.Contacts,
                        unselectedIcon = Icons.Outlined.Contacts
                    )

                )
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                val context = LocalContext.current

                ModalNavigationDrawer(
                    gesturesEnabled = drawerState.isOpen, drawerContent = {
                        ModalDrawerSheet()
                        {
                            NavBarHeader()
                            Spacer(modifier = Modifier.height(8.dp))
                            NavBarBody(
                                items = items,
                                currentRoute = currentRoute
                            ) { currentNavigationItem ->
                                if (currentNavigationItem.route == "share") {
                                    Toast.makeText(context, "Share Clicked", Toast.LENGTH_LONG)
                                        .show()
                                } else {
                                    navController.navigate(currentNavigationItem.route) {

                                        navController.graph.startDestinationRoute?.let { startDestinationRoute ->

                                            popUpTo(startDestinationRoute) {
                                                saveState = true
                                            }
                                        }

                                        // Configure navigation to avoid multiple instances of the same destination
                                        launchSingleTop = true

                                        // Restore state when re-selecting a previously selected item
                                        restoreState = true
                                    }
                                }

                                scope.launch {
                                    drawerState.close()
                                }
                            }
                        }
                    }, drawerState = drawerState
                ) {
                    Scaffold(
                        topBar = {
                            // Conditionally display the TopNavigationBar based on the current route

                            if (currentRoute == Screen.Discussion.route || currentRoute == Screen.DiscussionSearch.route || currentRoute == Screen.AddPost.route || currentRoute == Screen.PostDetail.route  || currentRoute == Screen.LostAndFound.route  || currentRoute == Screen.PostDetail_LostAndFound.route  || currentRoute == Screen.AddPostLostAndFound.route || currentRoute == Screen.AddPostStudentResources.route || currentRoute == Screen.StudentResources.route || currentRoute == Screen.PostDetail_StudentResources.route)
                            {
                                DiscussionTopNavigationBar(
                                    onFilterSelected = { filter -> selectedFilter = filter },
                                    navController = navController, onClick = {drawerState.open()})
                            }

                            else if (currentRoute != Screen.Splash.route && currentRoute != Screen.Account.route && currentRoute != Screen.Discussion.route && currentRoute != Screen.RegisterScreen.route && currentRoute != Screen.AddPostStudentResources.route && currentRoute != Screen.StudentResources.route && currentRoute != Screen.PostDetail_StudentResources.route && currentRoute != Screen.StudentResourcesSearch.route && currentRoute != Screen.UpcomingEvent.route && currentRoute != Screen.eventsSearch.route && currentRoute != Screen.addEvent.route)
                            {
                                TopNavigationBar (onClick = { drawerState.open()}, navController = navController)
                            }

                            else if (currentRoute == Screen.UpcomingEvent.route || currentRoute == Screen.eventsSearch.route || currentRoute == Screen.addEvent.route)
                            {
                                EventsTopNavigationBar(navController = navController, onClick = { drawerState.open()})
                            }
                                 },
                        bottomBar = {
                            // Conditionally display the BottomNavigationBar based on the current route
                            if (currentRoute != Screen.Splash.route && currentRoute != Screen.Account.route && currentRoute != Screen.RegisterScreen.route) {
                                BottomNavigationBar(navController)
                            }

                        }
                    )
                    {

                        SetupNavGraph(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }, navController = navController)

                    }
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


