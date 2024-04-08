package com.example.cis3515_1.Screens

import Model.StudentResources
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.example.cis3515_1.BottomNavigationBar
import com.example.cis3515_1.DiscussionTopNavigationBar
import com.example.cis3515_1.R
import com.example.cis3515_1.ui.theme.Red01
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date


@Composable
fun PostDetailScreen_StudentResources(postId: String, navController: NavHostController)
{
    var post by remember { mutableStateOf<StudentResources?>(null) }
    val scope = rememberCoroutineScope()

    val currentUserId = Firebase.auth.currentUser?.email ?: ""

    // Display selected image for comment dialog
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

    LaunchedEffect(postId)
    {
        post = fetchPostFromFirestoreStudentResources(postId)
    }

    // for comments
    var showDialog by remember { mutableStateOf(false) }
    var showDeletePostDialog by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("") }

    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val pickImagesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri> ->
        imageUris = uris
    }

    var userNameEditable by remember { mutableStateOf(true) }

    // Load the username for the post, and manage editability
    LaunchedEffect(key1 = showDialog) {
        if (showDialog) {
            userName = getOrSetUsernameForPost(postId, currentUserId, null) ?: ""
            userNameEditable = userName.isEmpty()
        }
    }

    Scaffold(
        topBar = { DiscussionTopNavigationBar( onFilterSelected = {}, navController = navController, onClick = {}) },
        bottomBar = { BottomNavigationBar(navController) }
    ) { padding ->
        // Display the post
        LazyColumn(modifier = Modifier.padding(padding)) {
            if (post != null) {
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        )
                        {
                            Text(
                                text = post!!.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 34.sp,
                                lineHeight = 40.sp
                            )

                            if (currentUserId == post!!.uid) {
                                IconButton(onClick =
                                {
                                    showDeletePostDialog = true
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Post")
                                }

                                if (showDeletePostDialog) {
                                    AlertDialog(
                                        onDismissRequest = { showDeletePostDialog = false },
                                        title = { Text("Delete Post") },
                                        text = { Text("Are you sure you want to delete this post?") },
                                        confirmButton = {
                                            Button(onClick = {
                                                deletePostStudentResources(postId, navController)
                                                showDeletePostDialog = false
                                            }, colors = ButtonDefaults.buttonColors(containerColor = Red01)) {
                                                Text("Delete")
                                            }
                                        },
                                        dismissButton = {
                                            Button(onClick = { showDeletePostDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = Red01)) {
                                                Text("Cancel")
                                            }
                                        }
                                    )
                                }
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Label,
                                contentDescription = "Category",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = post!!.category,
                                fontSize = 18.sp
                            )
                        }
                        Text(
                            text = "Posted on: ${post!!.date.formatToString()}",
                            fontSize = 18.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "Posted by: ${post!!.userName}",
                            fontSize = 18.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = post!!.content,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        post!!.imageUrls.forEach { imageUrl ->
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
            } else {
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Loading...",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    }
                }
            }
        }
    }
}

suspend fun fetchPostFromFirestoreStudentResources(postId: String): StudentResources? {
    val firestore = FirebaseFirestore.getInstance()
    // Attempt to fetch the document snapshot for the given postId
    try {
        val documentSnapshot = firestore.collection("studentResources").document(postId).get().await()
        if (documentSnapshot.exists()) {
            return StudentResources(
                id = documentSnapshot.id,
                title = documentSnapshot.getString("title") ?: "",
                content = documentSnapshot.getString("content") ?: "",
                uid = documentSnapshot.getString("uid") ?: "",
                userName = documentSnapshot.getString("userName") ?: "",
                date = Date(documentSnapshot.getLong("date") ?: 0L),
                imageUrls = documentSnapshot.get("imageUrls") as List<String>? ?: emptyList(),
                category = documentSnapshot.getString("category") ?: ""
            )
        } else {
            return null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

fun deletePostStudentResources(postId: String, navController: NavController) {
    val firestore = FirebaseFirestore.getInstance()

    GlobalScope.launch(Dispatchers.IO) {
        try {
            firestore.collection("studentResources").document(postId).delete().await()
            withContext(Dispatchers.Main) {
                navController.popBackStack()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}


suspend fun getOrSetUsernameForPost(postId: String, userId: String, proposedUsername: String?): String {
    val firestore = FirebaseFirestore.getInstance()
    val usernameMapRef = firestore.collection("studentResources").document(postId).collection("usernames")

    val docSnapshot = usernameMapRef.document(userId).get().await()
    if (docSnapshot.exists()) {
        return docSnapshot.getString("username") ?: ""
    } else if (proposedUsername != null) {
        usernameMapRef.document(userId).set(mapOf("username" to proposedUsername)).await()
        return proposedUsername
    } else {
        return ""
    }
}
