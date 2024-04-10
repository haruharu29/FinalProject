package com.example.cis3515_1.Screens


import Model.upcomingEventsVars
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.cis3515_1.BottomNavigationBar
import com.example.cis3515_1.Navigation.Screen
import com.example.cis3515_1.R
import com.example.cis3515_1.TopNavigationBar
import com.example.cis3515_1.fetchEventsFromFirestore
import com.example.cis3515_1.ui.theme.Red01
import com.example.cis3515_1.ui.theme.Red05
import com.example.cis3515_1.ui.theme.getTodaysDate
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalPagerApi::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier, onClick: suspend () -> Unit, navController: NavHostController)
{
    Column(horizontalAlignment = Alignment.CenterHorizontally)
    {
        Scaffold(
            topBar = { TopNavigationBar(onClick = onClick, navController = navController) },
            bottomBar = { BottomNavigationBar(navController) })
        { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
            )
            {
                LazyColumn(modifier = Modifier)
                {
                    item {
                        Spacer(modifier = Modifier.size(10.dp))
                    }

                    val gradient = Brush.verticalGradient(colors = listOf(Red05, Red01))

                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp)
                        )
                        {
                            Box(
                                Modifier
                                    .background(color = MaterialTheme.colorScheme.background)
                                    .height(200.dp)
                                    .fillMaxWidth()
                            )
                            {
                                /*val upcomingEvents = remember { mutableStateOf<List<upcomingEventsVars>>(emptyList()) }
                                val todayDate = getTodaysDate()
                                LaunchedEffect(todayDate) {

                                    upcomingEvents.value = fetchEventsFromFirestore(todayDate)
                                }

                                if (upcomingEvents.value.size.equals(0))
                                {
                                    Image(painter = painterResource(id = R.drawable.welcome),
                                        contentDescription = "icon",
                                        modifier = Modifier.alpha(alpha = 0.9F).fillMaxWidth()
                                    )
                                }
                                val state = rememberPagerState(pageCount = {
                                    upcomingEvents.value.size
                                })
                                //EventList(upcomingEvents = upcomingEvents.value, navController)
                                HorizontalPager(
                                    state = state
                                ) { page ->

                                    val event = upcomingEvents.value.getOrNull(page)
                                    event?.let {
                                        Row {
                                            Column(modifier = modifier.weight(1F).fillMaxWidth()) {
                                                if ( it.imageUrls.firstOrNull().equals(null))
                                                {

                                                    Image(painter = painterResource(id = R.drawable.welcome),
                                                        contentDescription = "icon",
                                                        modifier = Modifier.alpha(alpha = 0.9F)
                                                    )

                                                    Image(painter = painterResource(id = R.drawable.no_event),
                                                        contentDescription = "icon",
                                                        modifier = Modifier.alpha(alpha = 0.9F)
                                                    )
                                                }

                                                Image(
                                                    painter = rememberImagePainter(
                                                        data = it.imageUrls.firstOrNull(), // Display first image if available,
                                                        builder = {
                                                            crossfade(true)
                                                        }
                                                    ),
                                                    modifier = Modifier.fillMaxSize(1f),
                                                    contentDescription = null
                                                )
                                            }


                                            //Spacer(modifier = modifier.padding(10.dp))
                                            /*Column(modifier = modifier.weight(1F).fillMaxHeight().padding(10.dp)) {
                                                Text(text = "Name Of Event: ${it.nameOfEvent}")
                                                Text(text = "Location: ${it.location}")
                                                Text(text = "Description: ${it.description}")
                                                Text(text = "Date: ${it.date}")
                                            }*/
                                        }



                                        // Delete event if necessary
                                        // deleteEvent(it.id, navController)

                                    }
                                }*/

                                todayEvent()
                            }

                        }
                    }

                    item {
                        Spacer(modifier = Modifier.size(10.dp))
                    }

                    item {
                        Row(Modifier.padding(start = 10.dp))
                        {
                            Card(
                                modifier = Modifier
                                    .height(180.dp)
                                    .width(180.dp)
                                    .padding(
                                        top = 10.dp,
                                        end = 10.dp,
                                        bottom = 10.dp,
                                        start = 5.dp
                                    ),
                                elevation = CardDefaults.cardElevation(10.dp),
                                onClick = { navController.navigate(Screen.UpcomingEvent.route) }
                            )
                            {
                                Box(
                                    Modifier
                                        .background(brush = gradient)
                                        .height(180.dp)
                                        .width(180.dp)
                                )
                                {
                                    Text(
                                        "UPCOMING EVENTS",
                                        Modifier
                                            .padding(20.dp)
                                            .align(Alignment.Center),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            Card(
                                modifier = Modifier
                                    .height(180.dp)
                                    .width(180.dp)
                                    .padding(start = 10.dp, bottom = 10.dp, top = 10.dp),
                                elevation = CardDefaults.cardElevation(10.dp),
                                onClick = { navController.navigate(Screen.Club.route) }
                            )
                            {
                                Box(
                                    Modifier
                                        .background(brush = gradient)
                                        .height(180.dp)
                                        .width(180.dp)
                                )
                                {
                                    Text(
                                        "CLUBS & ORGANIZATIONS",
                                        Modifier
                                            .padding(10.dp)
                                            .align(Alignment.Center),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                        }
                    }

                    item{
                        Row(Modifier.padding(start = 10.dp))

                        {
                            Card(
                                modifier = Modifier
                                    .height(180.dp)
                                    .width(180.dp)
                                    .padding(top = 10.dp, end = 10.dp, bottom = 10.dp, start = 5.dp),
                                elevation = CardDefaults.cardElevation(10.dp),
                                onClick = { navController.navigate(Screen.CourseSchedule.route) }
                            )
                            {
                                Box(
                                    Modifier
                                        .background(brush = gradient)
                                        .height(180.dp)
                                        .width(180.dp)
                                )
                                {
                                    Text(
                                        "COURSE SCHEDULES",
                                        Modifier
                                            .padding(20.dp)
                                            .align(Alignment.Center),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        letterSpacing = 0.sp,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            Card(
                                modifier = Modifier
                                    .height(180.dp)
                                    .width(180.dp)
                                    .padding(top = 10.dp, bottom = 10.dp, start = 10.dp),
                                elevation = CardDefaults.cardElevation(10.dp),
                                onClick = {  navController.navigate(Screen.Discussion.createRoute("All"))}
                            )
                            {
                                Box(
                                    Modifier
                                        .background(brush = gradient)
                                        .height(180.dp)
                                        .width(180.dp)
                                )
                                {
                                    Text(
                                        "DISCUSSIONS",
                                        Modifier
                                            .padding(10.dp)
                                            .align(Alignment.Center),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun todayEvent(modifier: Modifier = Modifier) {

    val upcomingEvents = remember { mutableStateOf<List<upcomingEventsVars>>(emptyList()) }
    val todayDate = getTodaysDate()

    LaunchedEffect(todayDate) {

        upcomingEvents.value = fetchEventsFromFirestore(todayDate)
    }

    //var images:  List<Int>

    /*if (upcomingEvents.value.size.equals(0))
    {
        images= listOf(
            R.drawable.welcome,
            R.drawable.no_event,
        )
    }

    else
    {
        images = listOf()
    }*/


    val images = listOf(
        R.drawable.welcome,
        R.drawable.no_event,
    )
    val pagerState = rememberPagerState(pageCount = {images.size})
    LaunchedEffect(Unit) {
        while (true) {
            delay(4000)
            val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
            pagerState.scrollToPage(nextPage)
        }
    }
    val scope = rememberCoroutineScope()

    Column(
        modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = modifier.wrapContentSize().align(Alignment.CenterHorizontally)) {
            HorizontalPager(
                state = pagerState,
                modifier.wrapContentSize()

            ) { currentPage ->

                Card(
                    modifier
                        .wrapContentSize(),
                        //.padding(26.dp)
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = images[currentPage]),
                        contentDescription = "",
                        alignment = Alignment.Center
                    )
                }
            }
            IconButton(
                onClick = {
                    val nextPage = pagerState.currentPage + 1
                    if (nextPage < images.size) {
                        scope.launch {
                            pagerState.scrollToPage(nextPage)
                        }
                    }
                },
                modifier
                    .padding(30.dp)
                    .size(48.dp)
                    .align(Alignment.CenterEnd)
                    .clip(CircleShape),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color(0x52373737)
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowRight, contentDescription = "",
                    modifier.fillMaxSize(),
                    tint = Color.LightGray
                )
            }
            IconButton(
                onClick = {
                    val prevPage = pagerState.currentPage -1
                    if (prevPage >= 0) {
                        scope.launch {
                            pagerState.scrollToPage(prevPage)
                        }
                    }
                },
                modifier
                    .padding(30.dp)
                    .size(48.dp)
                    .align(Alignment.CenterStart)
                    .clip(CircleShape),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color(0x52373737)
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowLeft, contentDescription = "",
                    modifier.fillMaxSize(),
                    tint = Color.LightGray
                )
            }
        }

        PageIndicator(
            pageCount = images.size,
            currentPage = pagerState.currentPage,
            modifier = modifier
        )

    }
}

@Composable
fun PageIndicator(pageCount: Int, currentPage: Int, modifier: Modifier) {

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        repeat(pageCount){
            IndicatorDots(isSelected = it == currentPage, modifier= modifier)
        }
    }
}

@Composable
fun IndicatorDots(isSelected: Boolean, modifier: Modifier) {
    val size = animateDpAsState(targetValue = if (isSelected) 12.dp else 10.dp, label = "")
    Box(modifier = modifier
        .padding(2.dp)
        .size(size.value)
        .clip(CircleShape)
        .background(if (isSelected) Color(0xff373737) else Color(0xA8373737))
    )
}

