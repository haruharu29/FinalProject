package model

import java.util.Date

data class upcomingEventsVars(
    val id: String,
    val nameOfEvent: String,
    val description: String,
    val location: String,
    val date: String,
    var imageUrls: List<String> = emptyList()

)
