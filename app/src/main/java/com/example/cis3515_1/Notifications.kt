package com.example.cis3515_1

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import model.Notification

@Composable
fun Notifications(modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier, navController: NavHostController)
{
    val firestore = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    var notifications by remember { mutableStateOf<List<Notification>>(emptyList()) }

    LaunchedEffect(key1 = currentUser) {
        currentUser?.email?.let { uid ->
            notifications = firestore.collection("users").document(uid)
                .collection("notifications")
                .get().await().documents.mapNotNull { snapshot ->
                    snapshot.toObject<Notification>()?.copy(id = snapshot.id)
                }
        }
    }
    Scaffold(topBar = { TopNavigationBar()}, bottomBar = { BottomNavigationBar(navController) })
    { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        )
        {
            Spacer(modifier = Modifier.height(4.dp))
            LazyColumn {
                items(notifications) { notification ->
                    NotificationItem(notification, navController)
                }
            }

        }
    }
}

fun createNotificationForUser(receiverUserId: String, notification: Notification) {
    val firestore = FirebaseFirestore.getInstance()

    val newNotification = hashMapOf(
        "postId" to notification.postId,
        "commentId" to notification.commentId,
        "senderId" to notification.senderId,
        "senderName" to notification.senderName,
        "type" to notification.type,
        "contentPreview" to notification.contentPreview,
        "date" to notification.date,
        "read" to notification.read
    )

    firestore.collection("users").document(receiverUserId)
        .collection("notifications").add(newNotification)
        .addOnSuccessListener {

        }
        .addOnFailureListener { e ->
            e.printStackTrace()
        }
}

@Composable
fun NotificationItem(notification: Notification, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable {
                navController.navigate("postDetail/${notification.postId}")
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "From: ${notification.senderName}",
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Message: ${notification.contentPreview}",
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Type: ${notification.type}",
                fontSize = 14.sp
            )
        }
    }
}
