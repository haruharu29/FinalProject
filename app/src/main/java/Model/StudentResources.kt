package Model

import java.util.Date

data class StudentResources (
    var id: String = "",
    val title: String,
    val content: String,
    val uid: String,
    val userName: String,
    var date: Date,
    var imageUrls: List<String> = emptyList(),
    val category: String
)