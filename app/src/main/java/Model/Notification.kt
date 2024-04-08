package Model

import java.util.Date
data class Notification(
    val id: String? = null,
    val postId: String = "",
    val commentId: String? = null,
    val senderId: String = "",
    val senderName: String = "",
    val type: String = "", // "postReply" or "commentReply"
    val contentPreview: String = "",
    var date: Date = Date(),
    val read: Boolean = false,
    val exists: Boolean = true

)