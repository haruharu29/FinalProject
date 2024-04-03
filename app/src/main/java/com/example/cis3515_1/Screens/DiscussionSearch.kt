package com.example.cis3515_1.Screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.cis3515_1.Navigation.Screen
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import Model.Post
import com.example.cis3515_1.BottomNavigationBar
import com.example.cis3515_1.DiscussionTopNavigationBar
import com.example.cis3515_1.R
import java.util.Date

@Composable
fun DiscussionSearch(navController: NavController, onClick: suspend () -> Unit) {
    var searchText by remember { mutableStateOf("") }
    var searchResults by remember(searchText) { mutableStateOf<List<Post>>(emptyList()) }

    LaunchedEffect(searchText) {
        if (searchText.isNotEmpty()) {
            searchResults = fetchPostsFromFirestore(searchQuery = searchText)
        } else {
            searchResults = emptyList()
        }
    }

    Scaffold(
        topBar = {
            DiscussionTopNavigationBar(
                navController = navController,
                onFilterSelected = {}
            ) { onClick }
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            SearchBox(onSearch = {
                searchText = it
            })
            SearchResultList(posts = searchResults, navController = navController)
        }
    }
}

@Composable
fun SearchBox(onSearch: (String) -> Unit = {}) {
    val searchText = remember { mutableStateOf("") }

    OutlinedTextField(
        value = searchText.value,
        onValueChange = {
            searchText.value = it
            onSearch(it)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 8.dp),
        label = { Text("Search") },
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        },
        trailingIcon = {
            if (searchText.value.isNotEmpty()) {
                IconButton(onClick = { searchText.value = "" }) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                }
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            onSearch(searchText.value)
        })
    )
}
@Composable
fun SearchResultList(posts: List<Post>, navController: NavController) {
    LazyColumn {
        items(posts) { post ->
            PostCard(post = post, navController = navController)
        }
    }
}

@Composable
fun PostCard(post: Post, navController: NavController) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { navController.navigate(Screen.PostDetail.createRoute(post.id)) }
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
                Column(modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.End)
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
                        Text(
                            text = post.category,
//                                    TODO:adjust the fontsize
                            fontSize = 12.sp
                        )
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

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Comment,
                    contentDescription = "Comments",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "${post.commentNum}")
            }

        }
    }
}

suspend fun fetchPostsFromFirestore(selectedFilter: String = "All", searchQuery: String = ""): List<Post> {
    val firestore = FirebaseFirestore.getInstance()
    var query: Query = firestore.collection("posts")

    if (selectedFilter != "All") {
        query = query.whereEqualTo("category", selectedFilter)
    }

    val posts = mutableListOf<Post>()
    try {
        val snapshot = query.orderBy("date", Query.Direction.DESCENDING).get().await()
        for (document in snapshot.documents) {
            val title = document.getString("title") ?: ""
            val content = document.getString("content") ?: ""
            if (searchQuery.isEmpty() || title.contains(searchQuery, ignoreCase = true) || content.contains(searchQuery, ignoreCase = true)) {
                val post = Post(
                    id = document.id,
                    title = title,
                    content = content,
                    uid = document.getString("uid") ?: "",
                    userName = document.getString("userName") ?: "",
                    date = Date(document.getLong("date") ?: 0L),
                    commentNum = document.getLong("commentNum")?.toInt() ?: 0,
                    imageUrls = document.get("imageUrls") as List<String>? ?: emptyList(),
                    category = document.getString("category") ?: ""
                )
                posts.add(post)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return posts
}