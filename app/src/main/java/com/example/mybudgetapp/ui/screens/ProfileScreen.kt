package com.example.mybudgetapp.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mybudgetapp.ui.R
import com.example.mybudgetapp.ui.model.UserData
import coil.compose.rememberImagePainter
import com.example.mybudgetapp.ui.appbars.BottomBar
import com.example.mybudgetapp.ui.appbars.MainTopBar
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(navController: NavController, currentuser: UserData?) {
    var userData: UserData? = null

    val auth = FirebaseAuth.getInstance()
    val username = currentuser?.username
    val email = auth.currentUser?.email
    val profilePictureUrl = currentuser?.profilePictureUrl
    val appLogo = painterResource(id = R.drawable.logowhite)


    // Profile Screen Layout
    Scaffold(
        bottomBar = { BottomBar(navController) },
        content = { paddingValues ->  // Add paddingValues to avoid overlap
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // App Logo (Top Center)
                Image(
                    painter = appLogo,
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(bottom = 24.dp)
                )

                // Profile Details Card (Below the Logo)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    elevation = CardDefaults.cardElevation(8.dp),
                    shape = MaterialTheme.shapes.medium.copy(CornerSize(16.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Profile Picture
                        if (!profilePictureUrl.isNullOrEmpty()) {
                            Image(
                                painter = rememberImagePainter(profilePictureUrl),  // Load remote image
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(120.dp)
                                    .padding(bottom = 16.dp),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.iconuserprofile),  // Load local default image
                                contentDescription = "Default Profile Picture",
                                modifier = Modifier
                                    .size(120.dp)
                                    .padding(bottom = 16.dp),
                                contentScale = ContentScale.Crop
                            )
                        }

                        // Email Text
                        Text(
                            text = email ?: "email is not available",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Username Text
                        Text(
                            text = username ?: "User name is not available",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Log Out Button
                        Button(
                            onClick = {
                                userData = null // Clear user data
                                auth.signOut() // Sign the user out
                                navController.navigate("sign_in") { // Navigate to Sign In screen
                                    popUpTo("profile") { inclusive = true } // Remove Profile screen from back stack
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Log Out", color = Color.White)
                        }
                    }
                }
            }
        }
    )
}