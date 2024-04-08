package com.example.cis3515_1.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.cis3515_1.BottomNavigationBar
import com.example.cis3515_1.Navigation.Screen
import com.example.cis3515_1.TopNavigationBar
import com.example.cis3515_1.ui.theme.Red01
import com.example.cis3515_1.ui.theme.Red05

@Composable
fun HomeScreen(modifier: Modifier = Modifier, onClick: suspend () -> Unit, navController: NavHostController)
{
    Column(horizontalAlignment = Alignment.CenterHorizontally)
    {
        Scaffold(
            topBar = { TopNavigationBar(onClick = onClick) },
            bottomBar = { BottomNavigationBar(navController) })
        { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
            )
            {
                LazyColumn(modifier = Modifier)
                {
                    item{
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
                                    .background(brush = gradient)
                                    .height(200.dp)
                                    .fillMaxWidth()
                            )
                            {
                                Text("Today's event", Modifier.padding(16.dp))
                            }

                        }
                    }

                    item{
                        Spacer(modifier = Modifier.size(10.dp))
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
