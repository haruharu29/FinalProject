package Model

import java.util.Date

data class Comment(
    val id: String,
    val postId: String,
    val content: String,
    val uid: String,
    val userName: String,
    val date: Date,
    val imageUrls: List<String> = emptyList(),
)