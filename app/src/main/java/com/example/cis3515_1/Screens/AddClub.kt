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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import com.example.cis3515_1.BottomNavigationBar
import com.example.cis3515_1.DiscussionTopNavigationBar
import com.example.cis3515_1.getOrSetUsernameForPost
import com.example.cis3515_1.ui.theme.Red01
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddClubScreen(modifier: Modifier = Modifier, navController: NavController, onClick: suspend () -> Unit) {
    Scaffold(
        topBar = {
            DiscussionTopNavigationBar(
                navController = navController, onFilterSelected = {
                }) { onClick }
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            var name by rememberSaveable { mutableStateOf("") }
            var description by rememberSaveable { mutableStateOf("") }
            var leader1 by rememberSaveable { mutableStateOf("") }
            var leader1Email by rememberSaveable { mutableStateOf("") }
            var leader2 by rememberSaveable { mutableStateOf("") }
            var leader2Email by rememberSaveable { mutableStateOf("") }
            var startSemester by rememberSaveable { mutableStateOf("") }
            var imageUri by remember { mutableStateOf<Uri?>(null) }

            val pickImageLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri: Uri? ->
                imageUri = uri
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Club name") },
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
                value = leader1,
                onValueChange = { leader1 = it },
                label = { Text("Leader1") },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = leader1Email,
                onValueChange = { leader1Email = it },
                label = { Text("Leader1 Email") },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = leader2,
                onValueChange = { leader2 = it },
                label = { Text("Leader2") },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = leader2Email,
                onValueChange = { leader2Email = it },
                label = { Text("Leader2 Email") },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = startSemester,
                onValueChange = { startSemester = it },
                label = { Text("Start Semester") },
                placeholder = { Text("Spring 2024") },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))


            Button(
                onClick = { pickImageLauncher.launch("image/*") },
                colors = ButtonDefaults.buttonColors(containerColor = Red01),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Choose Image")
            }

            imageUri?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .size(100.dp)
                        .padding(end = 8.dp)
                )
            }

            Button(
                onClick = {
                    imageUri?.let {
                        uploadClub(
                            name = name,
                            description = description,
                            leader1 = leader1,
                            leader1Email = leader1Email,
                            leader2 = leader2,
                            leader2Email = leader2Email,
                            startSemester = startSemester,
                            imageUri = it,
                            navController = navController
                        )
                    }
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Red01),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit")
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

    }

}

fun uploadClub(
    name: String,
    description: String,
    leader1: String,
    leader1Email: String,
    leader2: String,
    leader2Email: String,
    startSemester: String,
    imageUri: Uri,
    navController: NavController
) {
    val firestore = FirebaseFirestore.getInstance()
    val storageRef = FirebaseStorage.getInstance().reference
    val userEmail = Firebase.auth.currentUser?.email ?: ""

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val imageRef =
                storageRef.child("clubs/${System.currentTimeMillis()}_${imageUri.lastPathSegment}")
            val uploadTask = imageRef.putFile(imageUri).await()
            val imageUrl = imageRef.downloadUrl.await().toString()

            val club = hashMapOf(
                "name" to name,
                "description" to description,
                "leader1" to leader1,
                "leader1Email" to leader1Email,
                "leader2" to leader2,
                "leader2Email" to leader2Email,
                "startSemester" to startSemester,
                "imageUrl" to imageUrl,
                "date" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
                "uploaderEmail" to userEmail,
                "isActive" to true
            )

            firestore.collection("clubs").add(club).await()
            withContext(Dispatchers.Main) {
                navController.navigate("Club")
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                navController.navigate("HomeScreen")
            }
        }
    }
}
