package model

import java.util.Date

data class Post(
    var id: String = "",
    val title: String,
    val content: String,
    val uid: String,
    val userName: String,
    var date: Date,
    val commentNum: Int = 0,
    var imageUrls: List<String> = emptyList(),
    val category: String
)