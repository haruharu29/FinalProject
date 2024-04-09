package com.example.cis3515_1.Screens

import Model.upcomingEventsVars
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.cis3515_1.Screens.uploadEvent
import com.example.cis3515_1.ui.theme.getTodaysDate
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@Composable
fun eventsDetailsScreen(eventId: String, navController: NavHostController) {
    val event = remember { mutableStateOf<upcomingEventsVars?>(null) }
    val showDialog = remember { mutableStateOf(false) }
    val selectedImageUrl = remember { mutableStateOf<String?>(null) }
    val nameOfEvent = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val location = remember { mutableStateOf("") }
    val date = remember { mutableStateOf("") }
    val imageUris = remember { mutableStateOf<List<Uri>>(emptyList()) }

    val pickImagesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri> ->
        imageUris.value = uris
    }

    LaunchedEffect(eventId) {
        event.value = fetchEventsFromFBase(eventId)

    }

    event.value?.let {
        var todayDate: Int = getTodaysDate().substring(8).toInt()
        var compareDate: Int = it.date.substring(8).toInt()
        if (compareDate < todayDate) {
            deleteEvent(eventId, navController)
        }
    }
    if (selectedImageUrl.value != null) {
        Dialog(onDismissRequest = { selectedImageUrl.value = null }) {
            Box(modifier = Modifier.clickable(onClick = { selectedImageUrl.value = null })) {
                AsyncImage(
                    model = selectedImageUrl.value!!,
                    contentDescription = "Selected post image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                )
            }
        }
    }
    val todayDate23 = getTodaysDate()
    if (showDialog.value ) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = "name of the event") },
            text = {
                Column {
                    OutlinedTextField(
                        value = nameOfEvent.value,
                        onValueChange = { nameOfEvent.value = it },
                        label = { Text("nameOfEvent") },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(25.dp))

                    OutlinedTextField(
                        value = description.value,
                        onValueChange = { description.value = it },
                        label = { Text("description") },
                        modifier = Modifier.height(450.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = location.value,
                        onValueChange = { location.value = it },
                        label = { Text("location") },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(25.dp))

                    OutlinedTextField(
                        value = date.value,
                        onValueChange = { date.value = it },
                        label = { Text("date") },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(25.dp))

                    Button(onClick = { pickImagesLauncher.launch(arrayOf("image/*")) },
                        shape = RoundedCornerShape(8.dp)) {
                        Text("Choose Image")
                    }

                    LazyRow {
                        items(imageUris.value.size) { index ->
                            Image(
                                painter = rememberAsyncImagePainter(imageUris.value[index]),
                                contentDescription = "Selected image $index",
                                modifier = Modifier
                                    .size(100.dp)
                                    .padding(end = 8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog.value = false
                        GlobalScope.launch(Dispatchers.IO) {
                            uploadEvent(
                                nameOfEvent = nameOfEvent.value,
                                description = description.value,
                                location = location.value,
                                date = date.value,
                                imageUris = imageUris.value,
                                navController = navController
                            )
                        }
                    }
                ) {
                }
            },
            dismissButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

fun deleteEvent(eventId: String, navController: NavController) {
    val firestore = FirebaseFirestore.getInstance()

    GlobalScope.launch(Dispatchers.IO) {
        try {
            firestore.collection("events").document(eventId).delete().await()
            withContext(Dispatchers.Main) {
                navController.popBackStack()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

suspend fun fetchEventsFromFBase(id: String): upcomingEventsVars? {
    val firestore = FirebaseFirestore.getInstance()
    try {
        val documentSnapshot = firestore.collection("events").document(id).get().await()
        if (documentSnapshot.exists()) {
            return upcomingEventsVars(
                id = documentSnapshot.id,
                nameOfEvent = documentSnapshot.getString("nameOfEvent") ?: "",
                description = documentSnapshot.getString("description") ?: "",
                location = documentSnapshot.getString("location") ?: "",
                date = documentSnapshot.getString("date") ?: "",
                imageUrls = documentSnapshot.get("imageUrls") as List<String>? ?: emptyList()
            )
        } else {
            return null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}


