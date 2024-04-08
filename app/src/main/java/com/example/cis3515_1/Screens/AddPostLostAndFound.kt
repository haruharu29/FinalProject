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
import coil.compose.rememberAsyncImagePainter
import com.example.cis3515_1.DiscussionTopNavigationBar
import com.example.cis3515_1.ui.theme.Red01
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPostLostAndFoundScreen(modifier: Modifier = Modifier, navController: NavController, onClick: suspend () -> Unit) {
    Scaffold(
        topBar = { DiscussionTopNavigationBar(
            navController = navController, onFilterSelected = {
            }) { onClick }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            var title by rememberSaveable { mutableStateOf("") }
            var content by rememberSaveable { mutableStateOf("") }
            var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

            val pickImagesLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenMultipleDocuments()
            ) { uris: List<Uri> ->
                imageUris = uris
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Content") },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Content") },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { pickImagesLauncher.launch(arrayOf("image/*")) },
                shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = Red01)) {
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

            Button(
                onClick = {
                    uploadPostLost(
                        title = title,
                        content = content,
                        proposedUsername = /*userName*/"Temple University Japan Campus",
                        imageUris = imageUris,
                        navController = navController
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Red01),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit")
            }
        }
    }
}

fun uploadPostLost(
    title: String,
    content: String,
    imageUris: List<Uri>,
    navController: NavController,
    proposedUsername: String
) {
    val firestore = FirebaseFirestore.getInstance()
    val storageRef = FirebaseStorage.getInstance().reference
    val userEmail = Firebase.auth.currentUser?.email ?: ""

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val imageUrls = imageUris.mapNotNull { uri ->
                val imageRef = storageRef.child("lost/${System.currentTimeMillis()}_${uri.lastPathSegment}")
                val uploadTask = imageRef.putFile(uri).await()
                imageRef.downloadUrl.await().toString()
            }

            val post = hashMapOf(
                "title" to title,
                "content" to content,
                "uid" to userEmail,
                "userName" to proposedUsername,
                "date" to System.currentTimeMillis(),
                "imageUrls" to imageUrls
            )

            val documentReference = firestore.collection("lost").add(post).await()
            val postId = documentReference.id

            if(proposedUsername.isNotEmpty()) {
                getOrSetUsernameForPost_l(postId, userEmail, proposedUsername)
            }

            withContext(Dispatchers.Main) {
                navController.navigate("LostAndFound")
            }
        }

        catch (e: Exception)
        {
            withContext(Dispatchers.Main)
            {
                navController.navigate("HomeScreen")
            }
        }
    }
}