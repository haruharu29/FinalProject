package com.example.cis3515_1


import Model.upcomingEventsVars
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun eventsSearch(navController: NavController) {
    var searchText by remember { mutableStateOf("") }
    var searchResults by remember(searchText) { mutableStateOf<List<upcomingEventsVars>>(emptyList()) }

    LaunchedEffect(searchText) {
        if (searchText.isNotEmpty()) {
            searchResults = fetchEventsFromFirestore(searchQuery = searchText)
        } else {
            searchResults = emptyList()
        }
    }

    Scaffold(
        topBar = {
            EventsTopNavigationBar(navController, onClick = {})
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            eventsSearchBox(onSearch = {
                searchText = it
            })
            eventsSearchResultList(events = searchResults, navController = navController)
        }
    }
}

@Composable
fun eventsSearchBox(onSearch: (String) -> Unit = {}) {
    val searchText = remember { mutableStateOf("") }

    OutlinedTextField(
        value = searchText.value,
        onValueChange = {
            searchText.value = it
            onSearch(it)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 8.dp),
        label = { Text("Search") },
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        },
        trailingIcon = {
            if (searchText.value.isNotEmpty()) {
                IconButton(onClick = { searchText.value = "" }) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                }
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            onSearch(searchText.value)
        })
    )
}
@Composable
fun eventsSearchResultList(events: List<upcomingEventsVars>, navController: NavController) {
    LazyColumn {
        items(events) { event ->
            eventsCard(events = event, navController = navController)
        }
    }
}

@Composable
fun eventsCard(events: upcomingEventsVars, navController: NavController) {

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

                Column()
                {


                    Text(
                        text = events.nameOfEvent,
                        fontSize = 26.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "\uD83D\uDCC5: ${events.date}",
                        fontSize = 20.sp,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "\uD83D\uDCCD: ${events.location}",
                        fontSize = 20.sp,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "${events.description}",
                        fontSize = 18.sp,
                        fontStyle = FontStyle.Italic,
                        color = Color.Black
                    )
                }
            }

        }

        events.imageUrls.firstOrNull()?.let { imageUrl ->
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
    }

}

suspend fun fetchEventsFromFirestore(searchQuery: String = ""): List<upcomingEventsVars> {
    val firestore = FirebaseFirestore.getInstance()
    var query: Query = firestore.collection("events")

    val events = mutableListOf<upcomingEventsVars>()
    try {
        val snapshot = query.orderBy("date", Query.Direction.DESCENDING).get().await()
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        for (document in snapshot.documents) {
            val nameOfEvent = document.getString("nameOfEvent") ?: ""
            val description = document.getString("description") ?: ""
            val location = document.getString("location") ?: ""
            val date = document.getString("date") ?: ""
            val eventDate = LocalDate.parse(date, formatter)

            if (!eventDate.isBefore(today) && (searchQuery.isEmpty() ||
                        nameOfEvent.contains(searchQuery, ignoreCase = true) ||
                        date.contains(searchQuery, ignoreCase = true) ||
                        description.contains(searchQuery, ignoreCase = true) ||
                        location.contains(searchQuery, ignoreCase = true))) {

                val event = upcomingEventsVars(
                    id = document.id,
                    nameOfEvent = nameOfEvent,
                    description = description,
                    location = location,
                    date = date,
                    imageUrls = document.get("imageUrls") as List<String>? ?: emptyList(),
                )

                events.add(event)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return events
}