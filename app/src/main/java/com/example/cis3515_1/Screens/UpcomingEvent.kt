package com.example.cis3515_1

import Model.upcomingEventsVars
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.cis3515_1.ui.theme.Grey
import com.example.cis3515_1.ui.theme.Red01
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun UpcomingEvent( navController: NavController)
{


    val upcomingEvents = remember { mutableStateOf<List<upcomingEventsVars>>(emptyList()) }

    LaunchedEffect(upcomingEvents) {
        upcomingEvents.value = fetchEventsFromFirestore()
    }
    Scaffold(
        topBar = { EventsTopNavigationBar(navController = navController, onClick = {})
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            uPEventList(upcomingEvents = upcomingEvents.value, navController)
        }
    }
}

@Composable
fun uPEventList(upcomingEvents: List<upcomingEventsVars>, navController: NavController) {
    LazyColumn {
        items(upcomingEvents) { event ->
            uEventCard(uEvent = event, navController = navController)
        }
    }
}

@Composable
fun uEventCard(uEvent: upcomingEventsVars, navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    val currentUserId = Firebase.auth.currentUser?.email ?: ""

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)

    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            )
            {
                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Delete Event") },
                        text = { Text("Are you sure you want to delete this event?") },
                        confirmButton = {
                            Button(onClick = { deleteEvent(uEvent.id, navController = navController )
                                showDialog = false
                            },
                                colors = ButtonDefaults.buttonColors(containerColor = Red01),
                            ) {
                                Text("Confirm")
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showDialog = false },
                                colors = ButtonDefaults.buttonColors(containerColor = Grey),) {
                                Text("Cancel")
                            }
                        }
                    )
                }

                Column()
                {


                    Text(
                        text = uEvent.nameOfEvent,
                        fontSize = 26.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "\uD83D\uDCC5: ${uEvent.date}",
                        fontSize = 20.sp,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "\uD83D\uDCCD: ${uEvent.location}",
                        fontSize = 20.sp,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "${uEvent.description}",
                        fontSize = 18.sp,
                        fontStyle = FontStyle.Italic,
                        color = Color.Black
                    )
                }
            }

        }

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
                    .padding(8.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        }

        if (currentUserId.endsWith("@tuj.temple.edu")) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { showDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Red01),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text("Delete")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

    }
}



fun deleteEvent(eventId: String, navController: NavController) {
    val firestore = FirebaseFirestore.getInstance()

    GlobalScope.launch(Dispatchers.IO) {
        try {
            firestore.collection("events").document(eventId).delete().await()
            withContext(Dispatchers.Main) {
                navController.navigate("UpcomingEvent") {
                    popUpTo("UpcomingEvent") {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}


suspend fun fetchEventsFromFirestore(): List<upcomingEventsVars> {
    val firestore = FirebaseFirestore.getInstance()
    val query: Query = firestore.collection("events")
    val upcomingEvents = mutableListOf<upcomingEventsVars>()

    try {
        val snapshot = query.get().await()
        val today = LocalDate.now()

        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        for (document in snapshot.documents) {
            val eventDate = LocalDate.parse(document.getString("date"), dateFormatter)
            if (!eventDate.isBefore(today)) {
                val event = upcomingEventsVars(
                    id = document.id,
                    nameOfEvent = document.getString("nameOfEvent") ?: "",
                    location = document.getString("location") ?: "",
                    description = document.getString("description") ?: "",
                    date = document.getString("date") ?: "",
                    imageUrls = document.get("imageUrls") as List<String>? ?: emptyList(),
                )
                upcomingEvents.add(event)
            }
        }

        upcomingEvents.sortBy { LocalDate.parse(it.date, dateFormatter) }

    } catch (e: Exception) {
        e.printStackTrace()
    }

    return upcomingEvents
}