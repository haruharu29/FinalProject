package com.example.cis3515_1

import android.text.Layout.Alignment
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun TopNavigationBar(modifier: Modifier = Modifier)
{
    NavigationBar {
        TopAppBar(colors = TopAppBarDefaults.topAppBarColors(titleContentColor = Color(0xFFFFFFFF)),
            title = {
                if(isSystemInDarkTheme())
                {
                    Image(painter = painterResource(id = R.drawable.tuj_br), contentDescription = "tuj", modifier = Modifier.size(180.dp))
                }

                else
                {
                    Image(painter = painterResource(id = R.drawable.tuj_logo), contentDescription = "tuj", modifier = Modifier
                        .size(120.dp)
                        .padding(top = 10.dp)) }
            },
            navigationIcon = {
                IconButton(onClick = { onNavigationIconClicked() }) {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = "Toggle Drawer")
                }
            }

        )

    }
}

fun onNavigationIconClicked()
{

}