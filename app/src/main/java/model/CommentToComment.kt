package model

import java.util.Date

data class CommentToComment(
    val id: String,
    val commentId: String,
    val content: String,
    val uid: String,
    val userName: String,
    val date: Date,
)
