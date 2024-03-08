package com.example.cis3515_1

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.cis3515_1.Navigation.Screen
import com.example.cis3515_1.ui.theme.Red01
import com.example.cis3515_1.ui.theme.Red05
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun AccountScreen(modifier: Modifier = Modifier, navController: NavHostController)
{
    val user = Firebase.auth.currentUser
    var userEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier.fillMaxSize()
    )
    { paddingValues ->
        if(user != null)
        {
            LoggedInScreen(userEmail, onLogout =
            {
                Firebase.auth.signOut()
                navController.navigate(Screen.Account.route)
                userEmail = ""
            })
        }
        else
        {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            )
            {
                OutlinedTextField(
                    value = userEmail,
                    onValueChange = { userEmail = it },
                    label = { Text("Email Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                if(errorMessage.isNotEmpty())
                {
                    Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Button(onClick =
                {
                    if(userEmail.endsWith("@temple.edu"))
                    {
                        loginUser(userEmail, password)
                        { success, message ->
                            if(success)
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
                })
                {
                    Text("Login")
                }

                TextButton(onClick =
                {
                    if(userEmail.endsWith("@temple.edu"))
                    {
                        registerUser(userEmail, password)
                        { success, message ->
                            if(success)
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
                })
                {
                    Text("No account? Register")
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

fun registerUser(email: String, password: String, onResult: (Boolean, String) -> Unit) {
    val auth = Firebase.auth
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if(task.isSuccessful)
            {
                onResult(true, "Registration successful")
            }
            else
            {
                onResult(false, task.exception?.message ?: "Registration failed")
            }
        }
}

fun loginUser(email: String, password: String, onResult: (Boolean, String) -> Unit) {
    val auth = Firebase.auth
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if(task.isSuccessful)
            {
                onResult(true, "Login successful")
            }
            else
            {
                onResult(false, task.exception?.message ?: "Login failed")
            }
        }
}

@Composable
fun LoggedInScreen(userEmail: String, onLogout: () -> Unit) {
    val userEmail = Firebase.auth.currentUser?.email ?: "No email"
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {
        Text(text = "Logged in as $userEmail", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onLogout() })
        {
            Text("Logout")
        }
    }
}