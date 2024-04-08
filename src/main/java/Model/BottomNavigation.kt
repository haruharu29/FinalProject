package Model

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavigation(
    val title: String,
    val icon: ImageVector,
    val route: String
)