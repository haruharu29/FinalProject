package com.example.cis3515_1.Screens

import Model.LostAndFound
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.cis3515_1.BottomNavigationBar
import com.example.cis3515_1.DiscussionTopNavigationBar
import com.example.cis3515_1.Navigation.Screen
import com.example.cis3515_1.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.Date

@Composable
fun LostAndFoundStaff(navController: NavHostController, onClick: suspend () -> Unit)
{
    val posts = remember { mutableStateOf<List<LostAndFound>>(emptyList()) }

    LaunchedEffect(posts)
    {
        posts.value = fetchPostsFromFirestore_Lost(posts)
    }

    Scaffold(
        topBar = { DiscussionTopNavigationBar(
            navController = navController,
            onFilterSelected = {}
        ) { onClick }
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LostAndFoundList(posts = posts.value, navController)
        }
    }
}

@Composable
fun LostAndFoundList(posts: List<LostAndFound>, navController: NavHostController) {
    LazyColumn {
        items(posts) { post ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { navController.navigate(Screen.PostDetail_LostAndFound.createRoute(post.id)) }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = post.title,
                        maxLines = 2,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        lineHeight = 26.sp
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    )
                    {
                        Column()
                        {
                            Text(
                                text = "Posted on: ${post.date.formatToString()}",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.End
                        )
                        {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End
                            )
                            {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Label,
                                    contentDescription = "Label",
                                    modifier = Modifier.size(16.dp),
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                /*Text(
                                    text = post.category,
//                                    TODO:adjust the fontsize
                                    fontSize = 12.sp
                                )*/
                            }
                        }

                    }
                    Text(
                        text = post.content,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    post.imageUrls.firstOrNull()?.let { imageUrl ->
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
        }
    }
}
suspend fun fetchPostsFromFirestore_Lost(posts: MutableState<List<LostAndFound>>): List<LostAndFound> {
    val firestore = FirebaseFirestore.getInstance()
    var query: Query = firestore.collection("lost")
    val posts = mutableListOf<LostAndFound>()

    try {
        val snapshot = query.orderBy("date", Query.Direction.DESCENDING).get().await()
        for (document in snapshot.documents) {
            val post = LostAndFound(
                id = document.id,
                title = document.getString("title") ?: "",
                content = document.getString("content") ?: "",
                uid = document.getString("uid") ?: "",
                userName = document.getString("username") ?: "",
                date = Date(document.getLong("date") ?: 0L),
                imageUrls = document.get("imageUrls") as List<String>? ?: emptyList(),
            )
            posts.add(post)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return posts
}