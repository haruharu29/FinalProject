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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
fun RegisterScreen(modifier: Modifier = Modifier, navController: NavHostController)
{
    var confirmPassword by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {},
        bottomBar = {},
        modifier = modifier.fillMaxSize()
    )
    { paddingValues ->

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
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    val description = if (passwordVisible) "Hide password" else "Show password"

                    IconButton(onClick = {passwordVisible = !passwordVisible}){
                        Icon(imageVector  = image, description)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    val description = if (passwordVisible) "Hide password" else "Show password"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, description)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            if(errorMessage.isNotEmpty())
            {
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }
            Button(onClick = {
                if (!userEmail.endsWith("@temple.edu")) {
                    errorMessage = "Please use a @temple.edu email address"
                } else
                    if (password != confirmPassword) {
                        errorMessage = "Passwords do not match"
                    } else {
                        registerUser(userEmail, password, navController) { success, message ->
                            if (success) {
                                errorMessage = "Registration successful. Please check your email to verify your account before logging in."
                            } else {
                                errorMessage = message
                            }
                        }
                    }
            }) {
                Text("Register")
            }

//                if (errorMessage.isNotEmpty()) {
//                    Button(onClick = {
//                        resendVerificationEmail(userEmail) { success, message ->
//                            if (success) {
//                                errorMessage = "Verification email resent. Please check your inbox."
//                            } else {
//                                errorMessage = message
//                            }
//                        }
//                    }) {
//                        Text("Resend Verification Email")
//                    }
//                }


        }

    }
}

//fun resendVerificationEmail(email: String, onResult: (Boolean, String) -> Unit) {
//    val user = Firebase.auth.currentUser
//    if (user != null && !user.isEmailVerified) {
//        user.sendEmailVerification().addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                onResult(true, "Verification email sent.")
//            } else {
//                onResult(false, task.exception?.message ?: "Failed to resend verification email.")
//            }
//        }
//    }
//}

fun registerUser(email: String, password: String, navController: NavHostController, onResult: (Boolean, String) -> Unit) {
    val auth = Firebase.auth
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if(task.isSuccessful) {
                val user = auth.currentUser
                user?.sendEmailVerification()?.addOnCompleteListener { verificationTask ->
                    if (verificationTask.isSuccessful) {
                        onResult(true, "Registered successfully. Please check your email to verify your account.")
                    } else {
                        user.delete()
                        onResult(false, verificationTask.exception?.message ?: "Failed to send verification email. Please try registering again.")
                    }
                }
            } else {
                onResult(false, task.exception?.message ?: "Registration failed")
            }
        }
}