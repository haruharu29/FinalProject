package com.example.cis3515_1

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.ImagePainter
import com.example.cis3515_1.Navigation.Screen
import com.google.firebase.installations.time.SystemClock
import model.upcomingEventsVars



/*
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun todaysEvents(upcomingEvents: List<upcomingEventsVars>,  navController: NavController)
{
    val todaysDate: String = SystemClock.getInstance().toString()
    val pagerState = rememberPagerState(pageCount = {10 })


        items(upcomingEvents) { uEvent ->
            if(uEvent.date.equals(todaysDate))
            {
                HorizontalPager(state = pagerState) { uEvent ->

                    Text(
                        text = "event name: $uEvent.nameOfEvent",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "location: $uEvent.location ",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "date: $uEvent.date ",
                        modifier = Modifier.fillMaxWidth()
                    )
                    ImagePainter()
                }
            }

    }
}*/