package com.example.cis3515_1.Screens

import Model.upcomingEventsVars
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
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
                    val gradient = Brush.verticalGradient(colors = listOf(Red05, Red01))

                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp)
                        )
                        {
                            Box(
                                Modifier
                                    .background(brush = gradient)
                                    .height(200.dp)
                                    .fillMaxWidth()
                            ) {
                                val upcomingEvents =
                                    remember { mutableStateOf<List<upcomingEventsVars>>(emptyList()) }
                                val todayDate = getTodaysDate()
                                LaunchedEffect(todayDate) {
                                    upcomingEvents.value = fetchEventsFromFirestore(todayDate)
                                }

                                val pageCount = if (upcomingEvents.value.isEmpty()) 2 else upcomingEvents.value.size + 1
                                val pagerState = rememberPagerState(initialPage = 0, pageCount = {pageCount})
                                val coroutineScope = rememberCoroutineScope()

                                LaunchedEffect(key1 = true) {
                                    coroutineScope.launch {
                                        while (true) {
                                            delay(3000)
                                            val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
                                            pagerState.animateScrollToPage(nextPage)
                                        }
                                    }
                                }

                                HorizontalPager(
                                    state = pagerState
                                ) { page ->
                                    when {
                                        page == 0 -> {
                                            Image(
                                                painter = painterResource(id = R.drawable.welcome),
                                                contentDescription = "Event Image",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        }

                                        page == 1 && upcomingEvents.value.isEmpty() -> {
                                            Image(
                                                painter = painterResource(id = R.drawable.no_event),
                                                contentDescription = "No Event",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        }

                                        else -> {
                                            val eventIndex = page - 1
                                            val event = upcomingEvents.value.getOrNull(eventIndex)
                                            event?.let {
                                                Image(
                                                    painter = rememberAsyncImagePainter(
                                                        ImageRequest.Builder(
                                                            LocalContext.current
                                                        ).data(data = it.imageUrls.firstOrNull())
                                                            .apply(block = fun ImageRequest.Builder.() {
                                                                crossfade(true)
                                                            }).build()
                                                    ),
                                                    contentDescription = "Event Image",
                                                    modifier = Modifier.fillMaxWidth(),
                                                    contentScale = ContentScale.FillWidth
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    }


                    item {
                        Row(Modifier.padding(start = 10.dp))
                        {
                            Card(
                                modifier = Modifier
                                    .height(180.dp)
                                    .width(180.dp)
                                    .padding(top = 10.dp, end = 10.dp, bottom = 10.dp, start = 5.dp),
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