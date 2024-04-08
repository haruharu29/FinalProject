package com.example.cis3515_1

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.cis3515_1.Navigation.Screen
import model.upcomingEventsVars

@Composable
fun todaysEvents(upcomingEvents: List<upcomingEventsVars>,  navController: NavController)
{
    LazyColumn {
        items(upcomingEvents) { uEvent ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { navController.navigate(Screen.PostDetail.createRoute(uEvent.nameOfEvent)) }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = uEvent.nameOfEvent,
                        maxLines = 2,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        lineHeight = 26.sp
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    )
                    {
                        Column()
                        {
                            Text(
                                text = "Event: ${uEvent.nameOfEvent}",
                                fontSize = 20.sp,
                                color = Color.Black
                            )
                            Text(
                                text = "Date: ${uEvent.date}",
                                fontSize = 20.sp,
                                color = Color.Black
                            )
                            Text(
                                text = "Location: ${uEvent.location}",
                                fontSize = 20.sp,
                                color = Color.Black
                            )
                            Text(
                                text = "Description: ${uEvent.description}",
                                fontSize = 20.sp,
                                color = Color.Black
                            )
                        }
                    }

                }
                Text(
                    text = uEvent.nameOfEvent,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                uEvent.imageUrls.firstOrNull()?.let { imageUrl ->
                    AsyncImage(
                        model = imageUrl,
                        placeholder = painterResource(id = R.drawable.tuj_logo),
                        error = painterResource(id = R.drawable.tuj_logo),
                        contentDescription = "Post image",
                        contentScale = ContentScale.Crop,
                        modifier =   Modifier
                            .fillMaxWidth()
                            .heightIn(min = 200.dp)
                            .clip(RoundedCornerShape(6.dp))
                    )
                }

            }
        }
    }
}