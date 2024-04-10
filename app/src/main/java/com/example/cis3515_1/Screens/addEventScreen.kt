package com.example.cis3515_1.Screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.cis3515_1.EventsTopNavigationBar
import com.example.cis3515_1.ui.theme.Red01
import com.example.cis3515_1.ui.theme.datePicker2
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun addEventScreen(modifier: Modifier = Modifier, navController: NavController) {
    Scaffold(
        topBar = { EventsTopNavigationBar(navController = navController, onClick = {}
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
            var date  by rememberSaveable { mutableStateOf("") }
            var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

            val pickImagesLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenMultipleDocuments()
            ) { uris: List<Uri> ->
                imageUris = uris
            }

            OutlinedTextField(
                value = nameOfEvent,
                onValueChange = { nameOfEvent = it },
                label = { Text("name of the Event") },
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

            //datePicker2()
            date = datePicker2()

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { pickImagesLauncher.launch(arrayOf("image/*")) },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Red01),
            ) {
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
            var showDialog by remember { mutableStateOf(false) }

            Button(
                onClick = {
                    if (imageUris.isNotEmpty()) {
                        uploadEvent(
                            nameOfEvent = nameOfEvent,
                            description = description,
                            location = location,
                            date = date,
                            imageUris = imageUris,
                            navController = navController
                        )
                    } else {
                        showDialog = true
                    }
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Red01),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit")
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(text = "Image Required") },
                    text = { Text("Please choose at least one image for the event.") },
                    confirmButton = {
                        Button(
                            onClick = { showDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Red01),
                        ) {
                            Text("OK")
                        }
                    }
                )
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