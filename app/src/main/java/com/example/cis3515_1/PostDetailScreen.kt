package com.example.cis3515_1

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import model.Post
import java.util.Date


@Composable
fun PostDetailScreen(postId: String, navController: NavHostController)
{
    var post by remember { mutableStateOf<Post?>(null) }

    val comments = remember { mutableStateOf<List<Comment>>(emptyList()) }
    val currentUserId = Firebase.auth.currentUser?.email ?: ""

    LaunchedEffect(postId) {
        comments.value = fetchCommentsFromFirestore(postId).sortedBy { it.date }
        post = fetchPostFromFirestore(postId)
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
    var userName by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val pickImagesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri> ->
        imageUris = uris
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Add Comment") },
            text = {
                Column {
                    OutlinedTextField(
                        value = userName,
                        onValueChange = { userName = it },
                        label = { Text("Username") },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Comment") },
                        modifier = Modifier.height(150.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

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
                        uploadComment(
                            postId = postId,
                            content = content,
                            userName = userName,
                            imageUris = imageUris,
                            navController = navController
                        )
                    }
                ) {
                    Text("Post Comment")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = { DiscussionTopNavigationBar( onFilterSelected = {}, navController = navController) },
        bottomBar = { BottomNavigationBar(navController) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Filled.AddComment, contentDescription = "Comment")
            }
        }
    ) { padding ->
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
//                        TODO: change top bar instead of button
                            if (currentUserId == post!!.uid) {
                                IconButton(onClick = { deletePost(postId, navController) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Post")
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
                            Spacer(modifier = Modifier.height(8.dp))
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

            if (comments.value.isNotEmpty()) {
                items(comments.value) { comment ->
                    CommentItem(
                        comment = comment,
                        currentUserId = currentUserId,
                        onDelete = {
                            deleteComment(comment.postId, comment.id, navController)
                        },
                        onReply = { postId, userName, replyContent, commentId ->
                            uploadReply(postId, userName, replyContent, commentId, navController)
                        }
                    )
                }
            }
        }
    }


}

@Composable
fun CommentItem(comment: Comment,
                currentUserId: String,
                onDelete: () -> Unit,
                onReply: (String, String, String, String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showDialogForReply by remember { mutableStateOf(false) }
    var replyContent by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }
    var replies by remember { mutableStateOf<List<CommentToComment>>(emptyList()) }

    LaunchedEffect(comment.id) {
        replies = fetchRepliesForComment(comment.postId, comment.id)
    }

    if (showDialogForReply) {
        ReplyInputDialog(
            onDismissRequest = { showDialogForReply = false },
            onReply = {
                onReply(comment.postId, userName, replyContent, comment.id)
                showDialogForReply = false
                replyContent = ""
                userName = ""
            },
            userName = userName,
            replyContent = replyContent,
            onReplyContentChange = { replyContent = it },
            onUserNameChange = { userName = it }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.padding(16.dp).weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Text(
                        text = "Posted by: ${comment.userName}"
                    )


                        Column {
                            IconButton(onClick = { expanded = !expanded }) {
                                Icon(Icons.Default.MoreHoriz, contentDescription = "More options")
                            }

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Reply") },
                                    onClick = {
                                        showDialogForReply = true
                                        expanded = false
                                    }
                                )
                                if (currentUserId == comment.uid) {
                                    DropdownMenuItem(
                                        text = { Text("Delete") },
                                        onClick = {
                                            onDelete()
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                }
                Text(
                    text = "Posted on: ${comment.date.formatToString()}",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = comment.content,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 18.sp
                )
                comment.imageUrls.forEach { imageUrl ->
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Comment image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 200.dp)
                            .clip(RoundedCornerShape(6.dp))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }


                Column {
                    replies.sortedBy { it.date }.forEach { reply ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceBright.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.padding(4.dp).fillMaxWidth()
                        ) {
                            Text(
                                text = "${reply.userName}: ${reply.content}",
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }



        }
    }
}

suspend fun fetchPostFromFirestore(postId: String): Post? {
    val firestore = FirebaseFirestore.getInstance()
    try {
        val documentSnapshot = firestore.collection("posts").document(postId).get().await()
        return if (documentSnapshot.exists()) {
            Post(
                id = documentSnapshot.id,
                title = documentSnapshot.getString("title") ?: "",
                content = documentSnapshot.getString("content") ?: "",
                uid = documentSnapshot.getString("uid") ?: "",
                userName = documentSnapshot.getString("userName") ?: "",
                date = Date(documentSnapshot.getLong("date") ?: 0L),
                commentNum = documentSnapshot.getLong("commentNum")?.toInt() ?: 0,
                imageUrls = documentSnapshot.get("imageUrls") as List<String>? ?: emptyList(),
                category = documentSnapshot.getString("category") ?: ""
            )
        } else null
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

suspend fun fetchCommentsFromFirestore(postId: String): List<Comment> {
    val firestore = FirebaseFirestore.getInstance()
    val comments = mutableListOf<Comment>()

    try {
        val snapshot = firestore.collection("posts").document(postId)
            .collection("comments").orderBy("date", Query.Direction.DESCENDING)
            .get().await()

        for (document in snapshot.documents) {
            val comment = Comment(
                id = document.id,
                userName = document.getString("userName") ?: "",
                content = document.getString("content") ?: "",
                date = Date(document.getLong("date") ?: 0L),
                uid = document.getString("uid") ?: "",
                postId = postId,
                imageUrls = document.get("imageUrls") as List<String>? ?: emptyList(),
            )
            comments.add(comment)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return comments
}

fun deleteComment(postId: String, commentId: String, navController: NavController) {
    GlobalScope.launch(Dispatchers.IO) {
        try {
            val firestore = FirebaseFirestore.getInstance()

            firestore.runTransaction { transaction ->
                val postRef = firestore.collection("posts").document(postId)

                val postSnapshot = transaction.get(postRef)
                val currentCommentNum = postSnapshot.getLong("commentNum") ?: 0
                if (currentCommentNum > 0) {
                    transaction.update(postRef, "commentNum", currentCommentNum - 1)
                }

                val commentRef = postRef.collection("comments").document(commentId)
                transaction.delete(commentRef)
            }.await()

            launch(Dispatchers.Main) {
                navController.popBackStack()
                val route = Screen.PostDetail.createRoute(postId)
                navController.navigate(route) {
                    popUpTo(route) { inclusive = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun uploadComment(
    postId: String,
    content: String,
    userName: String,
    imageUris: List<Uri>,
    navController: NavController
){
    val firestore = FirebaseFirestore.getInstance()
    val storageRef = FirebaseStorage.getInstance().reference
    val userEmail = Firebase.auth.currentUser?.email ?: ""

    val postRef = firestore.collection("posts").document(postId)
    firestore.runTransaction { transaction ->
        val postSnapshot = transaction.get(postRef)
        val currentCommentNum = postSnapshot.getLong("commentNum") ?: 0
        transaction.update(postRef, "commentNum", currentCommentNum + 1)
    }

    CoroutineScope(Dispatchers.IO).launch {
        val imageUrls = imageUris.mapNotNull { uri ->
            val imageRef = storageRef.child("comments/${postId}/${System.currentTimeMillis()}_${uri.lastPathSegment}")
            val uploadTask = imageRef.putFile(uri).await()
            imageRef.downloadUrl.await().toString()
        }

        val comment = hashMapOf(
            "postId" to postId,
            "content" to content,
            "uid" to userEmail,
            "userName" to userName,
            "date" to System.currentTimeMillis(),
            "imageUrls" to imageUrls
        )

        firestore.collection("posts").document(postId)
            .collection("comments").add(comment)
            .addOnSuccessListener { documentReference ->
                navController.popBackStack()
                val route = Screen.PostDetail.createRoute(postId)
                navController.navigate(route) {
                    popUpTo(route) { inclusive = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
            .addOnFailureListener {
                navController.navigate("HomeScreen")
            }
    }
}

fun deletePost(postId: String, navController: NavController) {
    val firestore = FirebaseFirestore.getInstance()

    GlobalScope.launch(Dispatchers.IO) {
        try {
            firestore.collection("posts").document(postId).delete().await()
            withContext(Dispatchers.Main) {
                navController.popBackStack()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun uploadReply(postId: String, userName: String, replyContent: String, commentId: String, navController: NavController) {

    val userEmail = Firebase.auth.currentUser?.email ?: ""
    val reply = hashMapOf(
        "commentId" to commentId,
        "content" to replyContent,
        "uid" to userEmail,
        "userName" to userName,
        "date" to System.currentTimeMillis(),
    )

    val firestore = FirebaseFirestore.getInstance()
    val postRef = firestore.collection("posts").document(postId)
    firestore.runTransaction { transaction ->
        val postSnapshot = transaction.get(postRef)
        val currentCommentNum = postSnapshot.getLong("commentNum") ?: 0
        transaction.update(postRef, "commentNum", currentCommentNum + 1)
    }
    firestore.collection("posts").document(postId)
        .collection("comments").document(commentId)
        .collection("replies").add(reply)
        .addOnSuccessListener { documentReference ->
            navController.popBackStack()
            val route = Screen.PostDetail.createRoute(postId)
            navController.navigate(route) {
                popUpTo(route) { inclusive = true }
                launchSingleTop = true
                restoreState = true
            }

        }
        .addOnFailureListener {

        }
}

@Composable
fun ReplyInputDialog(
    onDismissRequest: () -> Unit,
    onReply: (String) -> Unit,
    replyContent: String,
    userName: String,
    onReplyContentChange: (String) -> Unit,
    onUserNameChange: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = "Reply to Comment") },
        text = {
            Column {
                OutlinedTextField(
                    value = userName,
                    onValueChange = onUserNameChange,
                    label = { Text("Username") },
                    placeholder = { Text("Type your username here...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = replyContent,
                    onValueChange = onReplyContentChange,
                    label = { Text("Reply") },
                    placeholder = { Text("Type your reply here...") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
    confirmButton = {
            Button(
                onClick = { onReply(replyContent) }
            ) {
                Text("Reply")
            }
        },
        dismissButton = {
            Button(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}

suspend fun fetchRepliesForComment(postId: String, commentId: String): List<CommentToComment> {
    val firestore = FirebaseFirestore.getInstance()
    val replies = mutableListOf<CommentToComment>()

    try {
        val snapshot = firestore.collection("posts").document(postId)
            .collection("comments").document(commentId)
            .collection("replies").orderBy("date", Query.Direction.ASCENDING)
            .get().await()

        for (document in snapshot.documents) {
            val reply = CommentToComment(
                id = document.id,
                userName = document.getString("userName") ?: "",
                content = document.getString("content") ?: "",
                date = Date(document.getLong("date") ?: 0L),
                uid = document.getString("uid") ?: "",
                commentId = commentId
            )
            replies.add(reply)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return replies
}



