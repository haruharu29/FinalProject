package com.example.cis3515_1.Screens

import androidx.annotation.DimenRes
import androidx.annotation.Dimension
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialogDefaults.shape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontLoadingStrategy.Companion.Async
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.wear.compose.material.ContentAlpha
import com.example.cis3515_1.BottomNavigationBar
import com.example.cis3515_1.TopNavigationBar
import com.example.cis3515_1.data.ClubsDataSource
import com.example.cis3515_1.data.ClubsList
import com.example.cis3515_1.ui.theme.Grey
import com.example.cis3515_1.ui.theme.Red01
import com.skydoves.orbital.Orbital
import com.skydoves.orbital.animateSharedElementTransition
import com.skydoves.orbital.rememberContentWithOrbitalScope
import org.jetbrains.annotations.Async


@Composable
fun Clubs(modifier: Modifier = Modifier, navController: NavHostController)
{
    Scaffold(topBar = {TopNavigationBar()}, bottomBar = {BottomNavigationBar(navController)})
    { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        )
        {
            ClubsCards()
        }
    }
}

@Composable
fun ClubsCards() {
    ListClubs(
        clubList = ClubsDataSource().loadClubs(),
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListClubs(clubList: List<ClubsList>, modifier: Modifier = Modifier)
{
    LazyColumn(modifier = modifier)
    {
        items(clubList)
        { clubs ->
            ClubsCard(
                clubs = clubs,
                modifier = Modifier.padding(8.dp) ,
                clubList = clubList
            )
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubsCard(clubs: ClubsList, modifier: Modifier = Modifier,clubList: List<ClubsList>) {
    var expandedState by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) 180f else 0f
    )

    Card(modifier = Modifier
        .fillMaxWidth()
        .animateContentSize(
            animationSpec = tween(
                durationMillis = 300,
                easing = LinearOutSlowInEasing
            )
        ),
        shape = CardDefaults.elevatedShape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent, disabledContainerColor = Grey, contentColor = Red01),
        onClick = { expandedState = !expandedState }
        )
    {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(30.dp))
        {
            Row(verticalAlignment = Alignment.CenterVertically)
            {
                ClubIcon(clubs.imageResourceId)

                Text(modifier = Modifier.weight(6f),
                    text = LocalContext.current.getString(clubs.stringResourceId),
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis)

                IconButton(modifier = Modifier
                    .weight(1f)
                    .alpha(ContentAlpha.medium)
                    .rotate(rotationState),
                        onClick = { expandedState = !expandedState })
                {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Drop-Down Arrow"
                    )
                }
            }

            if (expandedState)
            {

                Image(painter = painterResource(clubs.imageResourceId),
                    contentDescription = stringResource(clubs.stringResourceId),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop)

                Text(
                    text = LocalContext.current.getString(clubs.stringResourceId1),
                    maxLines = 30,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier.size(20.dp))

                Text(
                    text ="Leaders: ",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.W900,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier.size(20.dp))

                Text(
                    text = LocalContext.current.getString(clubs.stringResourceId2),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.W900,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }

    Spacer(modifier = Modifier.size(5.dp))
}

@Composable
fun ClubIcon(
    @DrawableRes clubIcon: Int,
    modifier: Modifier = Modifier
) {
    Image(
        modifier = modifier
            .size(70.dp)
            .padding(end = 10.dp)
            .clip(MaterialTheme.shapes.small),
        contentScale = ContentScale.Crop,
        painter = painterResource(clubIcon),

        contentDescription = null
    )
}