package com.example.cis3515_1

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.cis3515_1.Navigation.Screen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
//@Preview
@Composable
fun TopNavigationBar(modifier: Modifier = Modifier, onClick: suspend () -> Unit, navController: NavController)
{
    val scope = rememberCoroutineScope()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val currentUserId = Firebase.auth.currentUser?.email ?: ""
    NavigationBar(modifier = Modifier) {
        TopAppBar(colors = TopAppBarDefaults.topAppBarColors(titleContentColor = MaterialTheme.colorScheme.onTertiary),
            title = {
                if(isSystemInDarkTheme())
                {
                    Image(painter = painterResource(id = R.drawable.tuj_br), contentDescription = "", modifier = Modifier.size(120.dp).padding(top = 10.dp).align(Alignment.CenterVertically))
                }

                else
                {
                    Image(painter = painterResource(id = R.drawable.tuj_logo), contentDescription = "", modifier = Modifier
                        .size(120.dp)
                        .padding(top = 10.dp).align(Alignment.CenterVertically)) }
            },
            navigationIcon = {
                IconButton(onClick = {scope.launch{onClick()}})
                {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = "Toggle Drawer", Modifier.size(140.dp))
                }
            },
            actions = {
                if(currentRoute == Screen.Club.route && currentUserId.endsWith("@tuj.temple.edu")) {
                    IconButton(onClick = { onAddClubClicked(navController) }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Club", Modifier.size(140.dp))
                    }
                }
            },

        )

    }
}

fun onAddClubClicked(navController: NavController)
{
    navController.navigate("AddClub")
}