package com.example.cis3515_1

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cis3515_1.Navigation.Screen
import com.example.cis3515_1.ui.theme.Red01
import com.example.cis3515_1.ui.theme.Red05

@OptIn(ExperimentalMaterial3Api::class)
//@Preview(showBackground = true)
@Composable
fun HomeScreen(modifier: Modifier = Modifier, navController: NavHostController)
{
    Column(horizontalAlignment = Alignment.CenterHorizontally)
    {
        Scaffold(topBar = { TopNavigationBar()}, bottomBar = { BottomNavigationBar(navController) })
        {padding ->
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(padding))
            {
                Column(){
                    val gradient = Brush.verticalGradient(colors = listOf(Red05, Red01))

                    Card(modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp))
                    {
                        Box(
                            Modifier
                                .background(brush = gradient)
                                .height(200.dp)
                                .fillMaxWidth())
                        {
                            Text("Today's event", Modifier.padding(16.dp))
                        }

                    }

                    Row()
                    {

                        Button(onClick = {navController.navigate(Screen.UpcomingEvent.route)}, modifier = Modifier
                            .padding(10.dp)
                            .height(180.dp)
                            .width(170.dp), shape = ShapeDefaults.Large)
                        {
                            Text("Upcoming Event", Modifier.padding(16.dp))
                        }

                        Button(onClick = {navController.navigate(Screen.Club.route)}, modifier = Modifier
                            .padding(10.dp)
                            .height(180.dp)
                            .width(180.dp), shape = ShapeDefaults.Large)
                        {
                            Text("Club", Modifier.padding(16.dp))
                        }

                    }

                    Row (){
                        Button(onClick = {navController.navigate(Screen.InformationSession.route)}, modifier = Modifier
                            .padding(10.dp)
                            .height(180.dp)
                            .width(180.dp), shape = ShapeDefaults.Large)
                        {
                            Text("Information Session", Modifier.padding(16.dp))
                        }

                        Button(onClick = {navController.navigate(Screen.Discussion.createRoute("All"))}, modifier = Modifier
                            .padding(10.dp)
                            .height(180.dp)
                            .width(180.dp), shape = ShapeDefaults.Large)
                        {
                            Text("Discussion", Modifier.padding(16.dp))
                        }
                    }

                }
            }
        }
    }
}
