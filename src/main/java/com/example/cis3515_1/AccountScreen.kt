package com.example.cis3515_1

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.cis3515_1.Navigation.Screen
import com.example.cis3515_1.ui.theme.Red01
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun AccountScreen(modifier: Modifier = Modifier, navController: NavHostController) {
    val user = Firebase.auth.currentUser
    var userEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {},
        bottomBar = {},
    )
    { paddingValues ->
        //        if(user != null && user.isEmailVerified)
        if (user != null)
        {
            LoggedInScreen(userEmail, onLogout =
            {
                Firebase.auth.signOut()
                navController.navigate(Screen.Account.route)
                userEmail = ""
            }, navController = navController)

        }

        else
        {
            Column(modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center)
            {
                
                OutlinedTextField(
                    value = userEmail,
                    onValueChange = { userEmail = it },
                    leadingIcon = {Icon(Icons.Rounded.AccountCircle, null)},
                    label = { Text("Email Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    leadingIcon = {Icon(Icons.Rounded.Key, null)},
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordVisible)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff

                        // Please provide localized description for accessibility services
                        val description = if (passwordVisible) "Hide password" else "Show password"

                        IconButton(onClick = { passwordVisible = !passwordVisible })
                        {
                            Icon(imageVector = image, description)
                        }
                                   }, modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(8.dp))

                if (errorMessage.isNotEmpty())
                {
                    Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(onClick =
                {
                    if (userEmail.endsWith("@temple.edu")||(userEmail.endsWith("@tuj.temple.edu")))
                    {
                        loginUser(userEmail, password)
                        { success, message ->
                            if (success)
                            {
                                errorMessage = ""
                                navController.navigate(Screen.Account.route)
                                userEmail = userEmail
                            }

                            else
                            {
                                errorMessage = message
                            }
                        }
                    }

                    else
                    {
                        errorMessage = "Please use a @temple.edu email address"
                    }
                }, colors = ButtonDefaults.buttonColors(containerColor = Red01)
                )
                {
                    Text("Login")
                }

                TextButton(onClick =
                {
                    navController.navigate(Screen.RegisterScreen.route)

                })
                {
                    Text("No account? Register", color = Color(0xFF8F2938))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAccountScreen()
{
    AccountScreen(modifier = Modifier, navController = rememberNavController())
}


fun loginUser(email: String, password: String, onResult: (Boolean, String) -> Unit) {
    val auth = Firebase.auth
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                if (user != null)
                {
                    onResult(true, "Login successful")
                }

                else
                {
                    auth.signOut()
                    onResult(false, "Please verify your email address before logging in.")
                }
            }

            else
            {
                onResult(false, task.exception?.message ?: "Login failed")
            }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoggedInScreen(userEmail: String, onLogout: () -> Unit, navController: NavHostController)
{
    val userEmail = Firebase.auth.currentUser?.email ?: "No email"
    /*Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        {
        Text(text = "Logged in as $userEmail", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate(Screen.Home.route)})
        {
            Text("Go to Home")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onLogout() })
        {
            Text("Logout")
        }
    }
 }*/
    Dialog(onDismissRequest = {})
    {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(10.dp),
            shape = RoundedCornerShape(16.dp))
        {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally)
            {
                Icon(painter = painterResource(id = R.drawable.logout), contentDescription = null, modifier = Modifier
                    .padding(top = 20.dp)
                    .size(40.dp))

                Text(text = "You are successfully logged in!", modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(20.dp), fontWeight = FontWeight.Bold, fontSize = 17.8.sp)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center)
                {
                    TextButton(onClick = { navController.navigate(Screen.Home.route) }, modifier = Modifier.padding(8.dp))
                    {
                        Text("Go to Home")
                    }
                }
            }
        }
    }
}