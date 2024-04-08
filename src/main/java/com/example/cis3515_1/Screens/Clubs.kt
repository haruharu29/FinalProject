package com.example.cis3515_1.Screens

import Model.Club
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.wear.compose.material.ContentAlpha
import coil.compose.AsyncImage
import com.example.cis3515_1.BottomNavigationBar
import com.example.cis3515_1.TopNavigationBar
import com.example.cis3515_1.ui.theme.Grey
import com.example.cis3515_1.ui.theme.Red01
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Date
import java.util.Locale


@Composable
fun Clubs(modifier: Modifier = Modifier, onClick: suspend () -> Unit, navController: NavHostController)
{
    val clubsViewModel: ClubsViewModel = viewModel()
    val clubs by clubsViewModel.clubs.collectAsState()
    Scaffold(topBar = {TopNavigationBar(onClick = onClick)},
        bottomBar = {BottomNavigationBar(navController)})
    { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        )
        {
            ListClubs(clubList = clubs, navController = navController)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListClubs(clubList: List<Club>, modifier: Modifier = Modifier, navController: NavHostController)
{
    LazyColumn(modifier = modifier)
    {
        items(clubList)
        { club ->
            ClubsCard(
                club = club,
                modifier = Modifier.padding(8.dp),
                navController = navController,
            )
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ClubsCard(club: Club, modifier: Modifier = Modifier, navController: NavHostController) {
    var showDialog by remember { mutableStateOf(false) }
    val currentUserId = Firebase.auth.currentUser?.email ?: ""
    var expandedState by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) 180f else 0f
    )

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Delete Club") },
            text = { Text("Are you sure you want to delete this club?") },
            confirmButton = {
                Button(onClick = {
                    deleteClub(club.id, navController)
                    showDialog = false
                },
                    colors = ButtonDefaults.buttonColors(containerColor = Red01),
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Grey),) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(modifier = Modifier
        .fillMaxWidth()
        .animateContentSize(
            animationSpec = tween(
                durationMillis = 300,
                easing = LinearOutSlowInEasing
            )
        )
        .clickable { expandedState = !expandedState },
        shape = CardDefaults.elevatedShape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent, disabledContainerColor = Grey, contentColor = Red01),
        onClick = { expandedState = !expandedState }
    )
    {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(30.dp))
        {
            Row(verticalAlignment = Alignment.CenterVertically)
            {
                ClubIcon(club)

                val clubName = if (club.isActive) club.name else "${club.name} [Inactive]"

                Text(modifier = Modifier.weight(6f),
                    text = clubName,
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis)

                IconButton(modifier = Modifier
                    .weight(1f)
                    .alpha(ContentAlpha.medium)
                    .rotate(rotationState),
                    onClick = { expandedState = !expandedState })
                {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Drop-Down Arrow"
                    )
                }
            }

            if (expandedState)
            {

                AsyncImage( model = club.imageUrl,
                    contentDescription = "Club Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop)

                Text(
                    text = club.description,
                    maxLines = 30,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier.size(20.dp))

                Text(
                    text ="Leaders:\n${club.leader1} (${club.leader1Email})\n${club.leader2} (${club.leader2Email})",
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.W900,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier.size(20.dp))

                Text(
                    text = "Since ${club.startSemester}",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.W900,
                    fontStyle = FontStyle.Italic
                )

                if (currentUserId.endsWith("@tuj.temple.edu")) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { showDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Red01)
                        ) {
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.size(5.dp))
}

@Composable
fun ClubIcon(
    club: Club,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = club.imageUrl,
        contentDescription = "Club Image",
        modifier = modifier
            .size(70.dp)
            .padding(end = 10.dp)
            .clip(MaterialTheme.shapes.small),
        contentScale = ContentScale.Crop,
    )
}

class ClubsViewModel : ViewModel() {
    private val _clubs = MutableStateFlow<List<Club>>(emptyList())
    val clubs: StateFlow<List<Club>> = _clubs

    init {
        fetchClubs()
    }

    private fun fetchClubs() {
        val db = FirebaseFirestore.getInstance()
        db.collection("clubs")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("ClubsViewModel", "Listen failed.", e)
                    return@addSnapshotListener
                }

                val clubsList = snapshot?.documents?.mapNotNull { document ->
                    try {
                        Club(
                            id = document.id,
                            name = document.getString("name") ?: "",
                            description = document.getString("description") ?: "",
                            leader1 = document.getString("leader1") ?: "",
                            leader1Email = document.getString("leader1Email") ?: "",
                            leader2 = document.getString("leader2") ?: "",
                            leader2Email = document.getString("leader2Email") ?: "",
                            startSemester = document.getString("startSemester") ?: "",
                            uploaderEmail = document.getString("uploaderEmail") ?: "",
                            date = document.getDate("date") ?: Date(),
                            imageUrl = document.getString("imageUrl") ?: "",
                            isActive = document.getBoolean("isActive") ?: true
                        ).also { club ->
                            Log.d("Club", "Fetched club: ${club.name}, isActive: ${club.isActive}")
                        }
                    } catch (e: Exception) {
                        Log.e("Club", "Error mapping document to Club", e)
                        null
                    }
                }?.sortedBy { it.name.lowercase(Locale.ROOT) } ?: emptyList()

                _clubs.value = clubsList
            }
    }


}

fun deleteClub(clubId: String, navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    db.collection("clubs").document(clubId).delete().addOnSuccessListener {
        Log.d("DeleteClub", "Club successfully deleted")
    }.addOnFailureListener { e ->
        Log.e("DeleteClub", "Error deleting club", e)
    }
}