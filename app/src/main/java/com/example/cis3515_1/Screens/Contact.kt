package com.example.cis3515_1.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cis3515_1.BottomNavigationBar
import com.example.cis3515_1.TopNavigationBar

@Composable
fun Contact(modifier: Modifier = Modifier, onClick: suspend () -> Unit, navController: NavHostController)
{
    Scaffold(topBar = { TopNavigationBar(onClick = onClick, navController = navController) }, bottomBar = { BottomNavigationBar(navController) })
    { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        )
        {
            LazyColumn(Modifier.padding(10.dp).align(Alignment.CenterHorizontally))
            {
                item{
                        Card(
                            Modifier
                                .fillMaxWidth()
                        )
                        {
                            Text("GENERAL INQUIRIES", style = MaterialTheme.typography.titleLarge,
                                textDecoration = TextDecoration.Underline,
                                modifier = Modifier.padding(5.dp),
                                fontWeight = FontWeight.Bold)


                            Text(text = "For inquiries about Temple University, Japan Campus (TUJ), please contact the Information Center.",
                                style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(5.dp))


                            Text("Information Center", style = MaterialTheme.typography.bodyLarge,
                                    textDecoration = TextDecoration.Underline,
                                    modifier = Modifier.padding(10.dp).align(Alignment.CenterHorizontally),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.surfaceTint
                                )



                        }
                    }

                item {
                        Card(
                            Modifier
                                .padding(top = 5.dp)
                                .fillMaxWidth()
                        )
                        {
                            Text("MEDIA-RELATED INQUIRIES", style = MaterialTheme.typography.titleLarge,
                                textDecoration = TextDecoration.Underline,
                                modifier = Modifier.padding(5.dp),
                                fontWeight = FontWeight.Bold)


                            Text(text = "For all media-related inquiries, please contact Communications and Marketing Support.",
                                style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(5.dp))


                            Text("Communications & Marketing Support", style = MaterialTheme.typography.bodyLarge,
                                textDecoration = TextDecoration.Underline,
                                modifier = Modifier.padding(10.dp).align(Alignment.CenterHorizontally),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.surfaceTint
                            )
                        }
                }

                item {
                    Card(
                        Modifier
                            .padding(top = 5.dp)
                            .fillMaxWidth()
                    )
                    {
                        Text("PROGRAM INQUIRIES", style = MaterialTheme.typography.titleLarge,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.padding(5.dp),
                            fontWeight = FontWeight.Bold)


                        Text(text = "To ask about admissions, program details, information sessions, and so on related to a particular program, please select the program you are interested in from the list below.",
                            style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(5.dp))

                    }
                }
            }
        }
    }
}