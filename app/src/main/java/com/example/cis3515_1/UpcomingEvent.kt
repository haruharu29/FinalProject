package com.example.cis3515_1

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material3.*
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.cis3515_1.Navigation.Screen
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import model.upcomingEventsVars
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun UpcomingEvent(modifier: Modifier = androidx.compose.ui.Modifier, navController: NavController)
{
    val upcomingEvents = remember { mutableStateOf<List<upcomingEventsVars>>(emptyList()) }

    LaunchedEffect(upcomingEvents) {
        upcomingEvents.value = fetchEventsFromFirestore(upcomingEvents)
    }
    Scaffold(
        topBar = { EventsTopNavigationBar(modifier, navController)
            },
        bottomBar = { BottomNavigationBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            EventList(upcomingEvents = upcomingEvents.value, navController)
        }
    }
}

@Composable
fun EventList(upcomingEvents: List<upcomingEventsVars>, navController: NavController) {
    LazyColumn {
        items(upcomingEvents) { uEvent ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                        navController.navigate(
                            Screen.eventsDetailsScreen.createRoute(
                                uEvent.id
                            )
                        )
                    }
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 200.dp)
                                .clip(RoundedCornerShape(6.dp))
                        )
                    }

                }
            }
        }
    }


fun Date.formatToString1(): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return formatter.format(this)
}


suspend fun fetchEventsFromFirestore(upcomingEvents: MutableState<List<upcomingEventsVars>>): List<upcomingEventsVars> {
    val firestore = FirebaseFirestore.getInstance()
    var query: Query = firestore.collection("events")
    val upcominEvent = mutableListOf<upcomingEventsVars>()


    try {
        val snapshot = query.orderBy("date", Query.Direction.DESCENDING).get().await()
        for (document in snapshot.documents) {
            val uEvent = upcomingEventsVars(
                id = document.id,
                nameOfEvent = document.getString("nameOfEvent") ?: "",
                location = document.getString("location") ?: "",
                description = document.getString("description") ?: "",
                date = document.getString("date") ?: "" //Date(document.getLong("date") ?: 0L)
               )
            upcominEvent.add(uEvent)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return upcominEvent
}

