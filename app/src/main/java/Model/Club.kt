package Model

import java.util.Date

data class Club(
    var id: String = "",
    val name: String = "",
    val description: String = "",
    val leader1: String = "",
    val leader1Email: String = "",
    val leader2: String = "",
    val leader2Email: String = "",
    val startSemester: String = "",
    val uploaderEmail: String = "",
    var date: Date = Date(),
    var imageUrl: String = "",
    var isActive: Boolean = true
)
