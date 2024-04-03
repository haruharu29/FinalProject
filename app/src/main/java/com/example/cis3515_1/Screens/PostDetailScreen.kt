package com.example.cis3515_1

import Model.Comment
import Model.CommentToComment
import Model.Notification
import Model.Post
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
import com.example.cis3515_1.Screens.formatToString
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.Date


@Composable
fun PostDetailScreen(postId: String, navController: NavHostController)
{
    var post by remember { mutableStateOf<Post?>(null) }
    val scope = rememberCoroutineScope()

    val comments = remember { mutableStateOf<List<Comment>>(emptyList()) }
    val currentUserId = Firebase.auth.currentUser?.email ?: ""

    // Fetch post details and comments whenever postId changes
    LaunchedEffect(postId) {
        comments.value = fetchCommentsFromFirestore(postId).sortedBy { it.date }
        post = fetchPostFromFirestore(postId)
    }

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

    // for comments
    var showDialog by remember { mutableStateOf(false) }
    var showDeletePostDialog by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

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


    // Show a dialog for adding a new comment
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Add Comment") },
            text = {
                Column {
                    OutlinedTextField(
                        value = userName,
                        onValueChange = { if (userNameEditable) userName = it },
                        label = { Text("Username") },
                        singleLine = true,
                        readOnly = !userNameEditable,
//                        TODO: Set the background color
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
                        scope.launch {
                            uploadComment(
                                postId = postId,
                                content = content,
                                proposedUserName = userName,
                                imageUris = imageUris,
                                navController = navController
                            )
                        }
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
        topBar = { DiscussionTopNavigationBar( onFilterSelected = {}, navController = navController, onClick = {})},
        bottomBar = { BottomNavigationBar(navController) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Filled.AddComment, contentDescription = "Comment")
            }
        }
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
                                                deletePost(postId, navController)
                                                showDeletePostDialog = false
                                            }) {
                                                Text("Delete")
                                            }
                                        },
                                        dismissButton = {
                                            Button(onClick = { showDeletePostDialog = false }) {
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

            // Display comments
            if (comments.value.isNotEmpty()) {
                items(comments.value) { comment ->
                    CommentItem(
                        comment = comment,
                        currentUserId = currentUserId,
                        postId = postId,
                        navController = navController,
                        onDelete = {
                            deleteComment(comment.postId, comment.id, navController)
                        },
                        onReply = { postId, userName, replyContent, commentId ->
                            scope.launch {
                                uploadReply(postId, userName, replyContent, commentId, navController)
                            }
                        }
                    )
                }
            }
        }
    }


}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CommentItem(comment: Comment,
                currentUserId: String,
                postId: String,
                navController: NavController,
                onDelete: () -> Unit,
                onReply: (String, String, String, String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showDialogForReply by remember { mutableStateOf(false) }
    var replyContent by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }
    var replies by remember { mutableStateOf<List<CommentToComment>>(emptyList()) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedReplyId by remember { mutableStateOf("") }
    var userNameEditable by remember { mutableStateOf(true) }


    // Fetch replies when the comment ID changes
    LaunchedEffect(comment.id) {
        replies = fetchRepliesForComment(comment.postId, comment.id)
    }

    // Show reply input dialog
    if (showDialogForReply) {
        ReplyInputDialog(
            onDismissRequest = { showDialogForReply = false },
            onReply = {
                onReply(comment.postId, userName, replyContent, comment.id)
                showDialogForReply = false
                replyContent = ""
            },
            userName = userName,
            replyContent = replyContent,
            userNameEditable = userNameEditable,
            onReplyContentChange = { replyContent = it },
            onUserNameChange = { if (userNameEditable) userName = it }
        )
    }

    // Comment card
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
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Text(
                        text = "Posted by: ${comment.userName}",
                        fontSize = 20.sp,
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
                                    CoroutineScope(Dispatchers.IO).launch {
                                        val userId = Firebase.auth.currentUser?.email ?: ""
                                        userName = getOrSetUsernameForPost(postId, userId, null) ?: ""
                                        userNameEditable = userName.isEmpty()
                                        showDialogForReply = true
                                    }
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
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = comment.content,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
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

                // Replies
                Column {
                    replies.sortedBy { it.date }.forEach { reply ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceBright.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth()
                                .combinedClickable(
                                    onClick = {
                                    },
                                    onLongClick = {
                                        if (currentUserId == reply.uid) {
                                            selectedReplyId = reply.id
                                            showDeleteDialog = true
                                        }
                                    }
                                )
                        ) {
                            Text(
                                text = "${reply.userName}: ${reply.content}",
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    if (showDeleteDialog) {
                        AlertDialog(
                            onDismissRequest = { showDeleteDialog = false },
                            title = { Text("Delete Reply") },
                            text = { Text("Are you sure you want to delete this reply?") },
                            confirmButton = {
                                Button(onClick = {
                                    deleteReply(postId, comment.id, selectedReplyId, navController)
                                    showDeleteDialog = false
                                }) {
                                    Text("Delete")
                                }
                            },
                            dismissButton = {
                                Button(onClick = { showDeleteDialog = false }) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }
                }
            }



        }
    }
}

suspend fun fetchPostFromFirestore(postId: String): Post? {
    val firestore = FirebaseFirestore.getInstance()
    // Attempt to fetch the document snapshot for the given postId
    try {
        val documentSnapshot = firestore.collection("posts").document(postId).get().await()
        if (documentSnapshot.exists()) {
            return Post(
                id = documentSnapshot.id,
                title = documentSnapshot.getString("title") ?: "",
                content = documentSnapshot.getString("content") ?: "",
                uid = documentSnapshot.getString("uid") ?: "",
                userName = documentSnapshot.getString("userName") ?: "Anonymous",
                date = Date(documentSnapshot.getLong("date") ?: 0L),
                commentNum = documentSnapshot.getLong("commentNum")?.toInt() ?: 0,
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

suspend fun fetchCommentsFromFirestore(postId: String): List<Comment> {
    val firestore = FirebaseFirestore.getInstance()
    val comments = mutableListOf<Comment>()

    // Attempt to fetch the Comments snapshot for the given postId
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
            // Reference to the specific comment in Firestore
            val commentRef = firestore.collection("posts").document(postId).collection("comments").document(commentId)
            val repliesCount = commentRef.collection("replies").get().await().size()

            // Delete all replies associated with the comment
            val repliesSnapshot = commentRef.collection("replies").get().await()
            repliesSnapshot.forEach { replySnapshot ->
                replySnapshot.reference.delete().await()
            }

            firestore.collectionGroup("notifications")
                .whereEqualTo("commentId", commentId)
                .get()
                .await()
                .forEach { notificationSnapshot ->
                    notificationSnapshot.reference.delete().await()
                }

            // Update the comments count in the post
            firestore.runTransaction { transaction ->
                val postRef = firestore.collection("posts").document(postId)

                val postSnapshot = transaction.get(postRef)
                val currentCommentNum = postSnapshot.getLong("commentNum") ?: 0
                val newCommentNum = currentCommentNum - (repliesCount + 1)
                if (newCommentNum >= 0) {
                    transaction.update(postRef, "commentNum", newCommentNum)
                }

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

suspend fun uploadComment(
    postId: String,
    content: String,
    proposedUserName: String,
    imageUris: List<Uri>,
    navController: NavController
){
    val firestore = FirebaseFirestore.getInstance()
    val storageRef = FirebaseStorage.getInstance().reference
    val userEmail = Firebase.auth.currentUser?.email ?: ""

    // Reference to the specific post
    val postRef = firestore.collection("posts").document(postId)
    val postSnapshot = postRef.get().await()
    val postOwnerId = postSnapshot.getString("uid")

    CoroutineScope(Dispatchers.IO).launch {
        // Set or get username for the comment based on the proposedUserName
        val userName = getOrSetUsernameForPost(postId, userEmail, proposedUserName)
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

        // Update the comment count
        firestore.runTransaction { transaction ->
            val postSnapshot = transaction.get(firestore.collection("posts").document(postId))
            val currentCommentNum = postSnapshot.getLong("commentNum") ?: 0
            transaction.update(firestore.collection("posts").document(postId), "commentNum", currentCommentNum + 1)
        }.await()

        // for notification
        firestore.collection("posts").document(postId)
            .collection("comments").add(comment).await().let {
                val postSnapshot = firestore.collection("posts").document(postId).get().await()
                val postOwnerId = postSnapshot.getString("uid")
                if (userEmail != postOwnerId) {
                    val notification = Notification(
                        postId = postId,
                        senderId = userEmail,
                        senderName = userName,
                        type = "postReply",
                        contentPreview = content.take(100),
                        date = Date(System.currentTimeMillis()),
                        exists = true
                    )
                    createNotificationForUser(postOwnerId!!, notification)
                }
            }


        withContext(Dispatchers.Main) {
            navController.popBackStack()
            val route = Screen.PostDetail.createRoute(postId)
            navController.navigate(route) {
                popUpTo(route) { inclusive = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }
}


fun deletePost(postId: String, navController: NavController) {
    val firestore = FirebaseFirestore.getInstance()

    GlobalScope.launch(Dispatchers.IO) {
        try {
            // Delete all comments associated with the post
            val commentsCollectionPath = firestore.collection("posts").document(postId).collection("comments")
            val commentsSnapshot = commentsCollectionPath.get().await()
            commentsSnapshot.forEach { commentSnapshot ->
                commentSnapshot.reference.delete().await()
            }

            // Delete all usernames (if applicable) associated with the post
            val usernamesCollectionPath = firestore.collection("posts").document(postId).collection("usernames")
            val usernamesSnapshot = usernamesCollectionPath.get().await()
            usernamesSnapshot.forEach { usernameSnapshot ->
                usernameSnapshot.reference.delete().await()
            }

            firestore.collection("posts").document(postId).delete().await()

            withContext(Dispatchers.Main) {
                navController.popBackStack()
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

suspend fun uploadReply(
    postId: String,
    proposedUserName: String,
    replyContent: String,
    commentId: String,
    navController: NavController
) {
    val firestore = FirebaseFirestore.getInstance()
    val userEmail = Firebase.auth.currentUser?.email ?: ""

    val userName = getOrSetUsernameForPost(postId, userEmail, proposedUserName)

    val reply = hashMapOf(
        "commentId" to commentId,
        "content" to replyContent,
        "uid" to userEmail,
        "userName" to userName,
        "date" to System.currentTimeMillis(),
    )

    // Increment the comment count on the post
    firestore.runTransaction { transaction ->
        val postSnapshot = transaction.get(firestore.collection("posts").document(postId))
        val currentCommentNum = postSnapshot.getLong("commentNum") ?: 0
        transaction.update(firestore.collection("posts").document(postId), "commentNum", currentCommentNum + 1)
    }.await()

    val commentSnapshot = firestore.collection("posts").document(postId)
        .collection("comments").document(commentId).get().await()
    val commentOwnerId = commentSnapshot.getString("uid")

    // for notification
    firestore.collection("posts").document(postId)
        .collection("comments").document(commentId)
        .collection("replies").add(reply).await().let {
            if (userEmail != commentOwnerId) {
                val notification = Notification(
                    postId = postId,
                    commentId = commentId,
                    senderId = userEmail,
                    senderName = userName,
                    type = "commentReply",
                    contentPreview = replyContent.take(100),
                    date = Date(System.currentTimeMillis()),
                    exists = true
                )
                createNotificationForUser(commentOwnerId!!, notification)
            }
        }

    withContext(Dispatchers.Main) {
        navController.popBackStack()
        val route = Screen.PostDetail.createRoute(postId)
        navController.navigate(route) {
            popUpTo(route) { inclusive = true }
            launchSingleTop = true
            restoreState = true
        }
    }

}

@Composable
fun ReplyInputDialog(
    onDismissRequest: () -> Unit,
    onReply: (String) -> Unit,
    replyContent: String,
    userName: String,
    userNameEditable: Boolean,
    onReplyContentChange: (String) -> Unit,
    onUserNameChange: (String) -> Unit
) {
    // Display a dialog allowing the user to input their reply
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
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = !userNameEditable
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
        // Fetch the replies for a specific comment ordered by date
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

fun deleteReply(postId: String, commentId: String, replyId: String, navController: NavController) {
    val firestore = FirebaseFirestore.getInstance()

    GlobalScope.launch(Dispatchers.IO) {
        try {
            // Reference to the specific reply to be deleted
            val replyRef = firestore.collection("posts").document(postId)
                .collection("comments").document(commentId)
                .collection("replies").document(replyId)

            replyRef.delete().await()

            // Update the comment count
            val postRef = firestore.collection("posts").document(postId)
            firestore.runTransaction { transaction ->
                val postSnapshot = transaction.get(postRef)
                val currentCommentNum = postSnapshot.getLong("commentNum") ?: 0
                if (currentCommentNum > 0) {
                    transaction.update(postRef, "commentNum", currentCommentNum - 1)
                }
            }.await()

            withContext(Dispatchers.Main) {
                navController.popBackStack()
                val route = Screen.PostDetail.createRoute(postId)
                navController.navigate(route) {
                    popUpTo(route) { inclusive = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
            }
            e.printStackTrace()
        }
    }
}

suspend fun getOrSetUsernameForPost(postId: String, userId: String, proposedUsername: String?): String {
    val firestore = FirebaseFirestore.getInstance()
    val usernameMapRef = firestore.collection("posts").document(postId).collection("usernames")

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