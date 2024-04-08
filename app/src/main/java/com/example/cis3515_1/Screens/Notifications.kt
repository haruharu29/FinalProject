package com.example.cis3515_1

import Model.Notification
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.concurrent.TimeUnit

@Composable
fun Notifications(modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier, navController: NavHostController)
{
    val firestore = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    var notifications by remember { mutableStateOf<List<Notification>>(emptyList()) }

    LaunchedEffect(key1 = currentUser) {
        currentUser?.email?.let { email ->
            val notificationsSnapshot = firestore.collection("users").document(email)
                .collection("notifications")
                .orderBy("date", Query.Direction.DESCENDING)
                .get().await()

            val updatedNotifications = notificationsSnapshot.documents.mapNotNull { snapshot ->
                snapshot.toObject<Notification>()?.copy(id = snapshot.id)?.let { notification ->
                    runBlocking {
                        checkIfContentExists(firestore, notification)
                    }
                }
            }

            notifications = updatedNotifications

            updatedNotifications.forEach { notification ->
                println("Notification exists: ${notification?.exists}")
            }
        }
    }
    Scaffold(
        topBar = { TopNavigationBar(onClick = {}, navController = navController) },
        bottomBar = { BottomNavigationBar(navController) })
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
        "read" to notification.read,
        "exists" to notification.exists
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
fun NotificationItem(notification: Notification?, navController: NavHostController) {
    notification?.let { notif ->
        if (notif.exists == true) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .clickable {
                        if (notif.postId.isNotBlank()) {
                            navController.navigate("postDetail/${notif.postId}")
                        }
                    },
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "From: ${notif.senderName}",
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Posted on: ${formatRelativeTime(notif.date)}",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val messageText = when (notif.type) {
                        "commentReply" -> "Replied to your comment: ${notif.contentPreview}"
                        "postReply" -> "Replied to your post: ${notif.contentPreview}"
                        else -> "Message: ${notif.contentPreview}"
                    }
                    Text(
                        text = messageText,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Content has been deleted",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}


fun formatRelativeTime(date: Date): String {
    val duration = Date().time - date.time

    val seconds = TimeUnit.MILLISECONDS.toSeconds(duration)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
    val hours = TimeUnit.MILLISECONDS.toHours(duration)
    val days = TimeUnit.MILLISECONDS.toDays(duration)

    return when {
        seconds < 60 -> "Just now"
        minutes < 60 -> "$minutes minutes ago"
        hours < 24 -> "$hours hours ago"
        days < 365 -> "$days days ago"
        else -> "${days / 365} years ago"
    }
}

suspend fun checkIfContentExists(firestore: FirebaseFirestore, notification: Notification): Notification? {
    return when (notification.type) {
        "commentReply" -> {
            val commentExists = firestore.collection("posts").document(notification.postId)
                .collection("comments").document(notification.commentId ?: "").get().await().exists()
            val postExists = firestore.collection("posts").document(notification.postId).get().await().exists()
            notification.copy(exists = postExists && commentExists)
        }
        "postReply" -> {
            val postExists = firestore.collection("posts").document(notification.postId).get().await().exists()
            notification.copy(exists = postExists)
        }
        else -> notification
    }
}