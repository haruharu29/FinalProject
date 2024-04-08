package com.example.cis3515_1


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.cis3515_1.Navigation.Screen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import model.Comment
import model.CommentToComment
import model.Notification
import model.Post
import model.upcomingEventsVars
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun eventsDetailsScreen(eventId: String, navController: NavHostController)
{
    var event by remember { mutableStateOf<upcomingEventsVars?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(eventId) {
        event = fetchEventsFromFBase(eventId)
    }
    var selectedImageUrl by remember { mutableStateOf<String?>(null) }

    if (selectedImageUrl != null) {
        Dialog(onDismissRequest = { selectedImageUrl = null }) {
            Box(modifier = Modifier.clickable(onClick = { selectedImageUrl = null })) {
                AsyncImage(
                    model = selectedImageUrl,
                    contentDescription = "Selected post image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                )
            }
        }
    }

    var showDialog by remember { mutableStateOf(false) }
    var showDeleteEventDialog by remember { mutableStateOf(false) }
    var nameOfEvent by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var date: String  = Date(System.currentTimeMillis()).toString()

    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val pickImagesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri> ->
        imageUris = uris
    }


    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "name of the event") },
            text = {
                Column {
                    OutlinedTextField(
                        value = nameOfEvent,
                        onValueChange = { nameOfEvent = it },
                        label = { Text("nameOfEvent") },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(25.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("description") },
                        modifier = Modifier.height(450.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                            value = location,
                    onValueChange = { location = it },
                    label = { Text("location") },
                    singleLine = true
                    )

                    Spacer(modifier = Modifier.height(25.dp))

                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("date") },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(25.dp))



                    Button(onClick = { pickImagesLauncher.launch(arrayOf("image/*")) },
                        shape = RoundedCornerShape(8.dp),) {
                        Text("Choose Image")
                    }

                    LazyRow {
                        items(imageUris.size) { index ->
                            Image(
                                painter = rememberAsyncImagePainter(imageUris[index]),
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
                        showDialog = false
                        scope.launch {
                            uploadEvent(
                                nameOfEvent = nameOfEvent,
                                description = description,
                                location = location,
                                date = date,
                                imageUris = imageUris,
                                navController = navController
                            )
                        }
                    }
                ) {
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
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

suspend fun fetchEventsFromFBase(nameOfEvent : String): upcomingEventsVars?
{
    val firestore = FirebaseFirestore.getInstance()
    try {
        val documentSnapshot = firestore.collection("events").document(nameOfEvent).get().await()
        if (documentSnapshot.exists()) {
            return upcomingEventsVars(
                id = documentSnapshot.id,
                nameOfEvent  = documentSnapshot.getString("nameOfEvent") ?: "",
                description = documentSnapshot.getString("description") ?: "",
                location = documentSnapshot.getString("location") ?: "",
                date = documentSnapshot.getString("date") ?: "",//Date(documentSnapshot.getLong("date") ?: 0L),
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
