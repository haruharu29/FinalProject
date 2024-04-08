package com.example.cis3515_1.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.example.cis3515_1.Navigation.Screen
import com.example.cis3515_1.R
import com.example.cis3515_1.ui.theme.Red01
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

@Composable
fun LogIn(userEmail: FirebaseUser?, onLogout: () -> Unit, navController: NavHostController)
{
    val userEmail = Firebase.auth.currentUser?.email ?: "No email"

    Dialog(onDismissRequest = {})
    {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(10.dp),
            shape = RoundedCornerShape(16.dp)
        )
        {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally)
            {
                Icon(painter = painterResource(id = R.drawable.logout), contentDescription = null, modifier = Modifier.padding(top = 20.dp).size(40.dp))

                Text(text = "Oh no... You are leaving! \n Are you sure?", modifier = Modifier.padding(20.dp), fontWeight = FontWeight.Bold, fontSize = 17.9.sp)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center)
                {
                    TextButton(onClick = {navController.navigate(Screen.Home.route)}, modifier = Modifier.padding(8.dp))
                    {
                        Text("Just kidding!", color = Red01)
                    }

                    TextButton(onClick = { onLogout() }, modifier = Modifier.padding(8.dp))
                    {
                        Text("Log Out", color = Red01)
                    }
                }
            }
        }
    }
}