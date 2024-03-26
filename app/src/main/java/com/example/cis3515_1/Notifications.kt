package com.example.cis3515_1

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import model.Notification

@Composable
fun Notifications(modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier, navController: NavHostController)
{
    Scaffold(topBar = { TopNavigationBar()}, bottomBar = { BottomNavigationBar(navController) })
    { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        )
        {

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
