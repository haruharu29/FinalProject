package com.example.cis3515_1

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import model.upcomingEventsVars
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun addEventScreen(modifier: Modifier = Modifier, navController: NavController) {
    Scaffold(
        topBar = { EventsTopNavigationBar(modifier = modifier, navController = navController
        ) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            var nameOfEvent by rememberSaveable { mutableStateOf("") }
            var description by rememberSaveable { mutableStateOf("") }
            var location by rememberSaveable { mutableStateOf("") }
            var date = datePicker()
            var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
            
            val pickImagesLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenMultipleDocuments()
            ) { uris: List<Uri> ->
                imageUris = uris
            }

            OutlinedTextField(
                value = nameOfEvent,
                onValueChange = { nameOfEvent = it },
                label = { Text("nameOfEvent") },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = date.toString(),
                onValueChange = {},
                label = { Text("Date") },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                readOnly = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { pickImagesLauncher.launch(arrayOf("image/*")) },
                shape = RoundedCornerShape(8.dp),) {
                Text("Choose Image")
            }

            LazyRow {
                items(imageUris.size) { index ->
                    Image(
                        painter = rememberImagePainter(imageUris[index]),
                        contentDescription = "Selected image $index",
                        modifier = Modifier
                            .size(100.dp)
                            .padding(end = 8.dp)
                    )
                }
            }

            Button(
                onClick = {
                    uploadEvent(
                        nameOfEvent = nameOfEvent,
                        description = description,
                        location = location,
                        date = date,
                        imageUris = imageUris,
                        navController = navController
                    )
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit")
            }
        }
    }
}

fun uploadEvent(
    nameOfEvent: String,
    description: String,
    location: String,
    date: String,
    imageUris: List<Uri>,
    navController: NavController
) {
    val firestore = FirebaseFirestore.getInstance()
    val storageRef = FirebaseStorage.getInstance().reference

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val imageUrls = imageUris.mapNotNull { uri ->
                val imageRef = storageRef.child("events/${System.currentTimeMillis()}_${uri.lastPathSegment}")
                val uploadTask = imageRef.putFile(uri).await()
                imageRef.downloadUrl.await().toString()
            }

            val event = hashMapOf(
                "nameOfEvent" to nameOfEvent,
                "description" to description,
                "location" to location,
                "date" to date,
                "imageUrls" to imageUrls
            )


            firestore.collection("events").add(event).await()

            withContext(Dispatchers.Main) {
                navController.navigate("UpcomingEvent")
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                navController.navigate("HomeScreen")
            }
        }
    }
}
