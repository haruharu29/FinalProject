package com.example.cis3515_1

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
    NavigationBar(modifier = Modifier) {
        TopAppBar(colors = TopAppBarDefaults.topAppBarColors(titleContentColor = Color(0xFFFFFFFF)),
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
                IconButton(onClick = {})
                {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = "Toggle Drawer", Modifier.size(140.dp))
                }
            }

        )

    }
}
