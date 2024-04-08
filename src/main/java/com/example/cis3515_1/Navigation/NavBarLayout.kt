package com.example.cis3515_1.Navigation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.cis3515_1.R

@Composable
fun NavBarHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        /*Image(
            painter = painterResource(id = R.drawable.temple_owls_logo_svg_vector), contentDescription = "logo",
            modifier = Modifier
                .size(150.dp)
                .padding(top = 40.dp)
        )*/

        Spacer(modifier = Modifier.size(30.dp))
        ProfileImage()
    }
}

@Composable
fun ProfileImage()
{
    val imageUri = rememberSaveable { mutableStateOf("") }
    
    val painter = rememberImagePainter(
        if (imageUri.value.isEmpty())
        {
            R.drawable.temple_owls_logo_svg_vector
        }
        else
        {
            imageUri.value
        })
    
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent())
    {
        uri: Uri? ->
        uri?.let{imageUri.value = it.toString()}
    }

    Card(shape = CircleShape)
    {
        Image(painter = painter, contentDescription = null, modifier = Modifier
            .wrapContentSize()
            .clickable { launcher.launch("image/*") }
            .size(120.dp),
            contentScale = ContentScale.Crop)
    }
}

@Composable
fun NavBarBody(
    items: List<NavigationItem>,
    currentRoute: String?,
    onClick: (NavigationItem) -> Unit,
) {
    items.forEachIndexed { index, navigationItem ->
        NavigationDrawerItem(
            colors = NavigationDrawerItemDefaults.colors(),
            label = { Text(text = navigationItem.title, fontSize = 20.sp, fontWeight = FontWeight.Bold)},
            selected = currentRoute == navigationItem.route,
            onClick = {onClick(navigationItem)},
            icon = {
                Icon(
                    imageVector = if (currentRoute == navigationItem.route)
                    {
                        navigationItem.selectedIcon
                    }
                    else
                    {
                        navigationItem.unselectedIcon
                    },
                    contentDescription = navigationItem.title,
                    Modifier.size(35.dp)
                )
            },
            badge = {
                navigationItem.badgeCount?.let {
                    Text(text = it.toString())
                }
            },
            modifier = Modifier.padding(
                PaddingValues(horizontal = 12.dp,
                    vertical = 8.dp)
            ))
    }
}