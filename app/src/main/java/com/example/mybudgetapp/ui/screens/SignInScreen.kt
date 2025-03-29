package com.example.mybudgetapp.ui.screens

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mybudgetapp.ui.MainActivity

import com.example.mybudgetapp.ui.R
import com.example.mybudgetapp.ui.model.SignInState
import com.example.mybudgetapp.ui.screens.FirebaseAuthUiClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@Composable
fun SignInScreen(
    state: SignInState,
    navController: NavController,
    onSignInClick: () -> Unit
) {
    val context = LocalContext.current

    //variables for email
    var email by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    var isError by remember { mutableStateOf(false) }

    //variables for password
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isPasswordError by remember { mutableStateOf(false) }  // Initialize as false
    var passwordTouched by remember { mutableStateOf(false) }  // Track if password field was touched
    Log.d("Signin", "4")
    // Handling sign-in error if any
    LaunchedEffect(key1 = state.signInError) {
        state.signInError?.let { error ->
            Toast.makeText(
                context,
                error,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo Image at the Top
        Image(
            painter = painterResource(id = R.drawable.logowhite),
            contentDescription = "App Logo",
            modifier = Modifier
                .height(160.dp)
                .width(160.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Sign in to My Budget",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Email field with a checkmark trailing icon and error message ****************************************
        Column(modifier = Modifier.fillMaxWidth()) {
            // Email Field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(text = "Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .onFocusChanged { focusState ->
                        if (!focusState.isFocused) {
                            isError = email.isNotEmpty() &&
                                    !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                        }
                    },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                trailingIcon = {
                    if (email.isNotEmpty() &&
                        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        Icon(
                            imageVector = Icons.Rounded.Email,
                            contentDescription = "Valid Email",
                            tint = Color.Green
                        )
                    }
                },
                isError = isError,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                })
            )

            // Error Message Below the TextField
            if (isError) {
                Text(
                    text = "Please enter a valid email address",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Password field with visibility toggle
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Password") },
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .onFocusChanged { focusState ->
                    passwordTouched = true
                    if (!focusState.isFocused && password.isNotEmpty()) {
                        // Validate password only if user has touched the field
                        if (password.length < 6) {
                            isPasswordError = true
                        }
                        else
                        {
                            isPasswordError = false
                        }
                    }
                },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    Image(imageVector = icon, contentDescription = "Toggle password visibility")
                }
            },
            isError = isPasswordError,  // Use the error flag only after validation
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            })
        )
        // Error Message Below the Password Field
        if (isPasswordError) {
            Text(
                text = "Password must be at least 6 characters",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sign In Button ***********************************************************************
        Button(
            onClick = {
                // Trigger password validation when the button is clicked
                if (password.length < 6) {
                    isPasswordError = true
                }
                MainActivity.AuthType = "STANDARD"
                MainActivity.email = email
                MainActivity.password = password
                onSignInClick()
                Log.d("Signin", "5")
                // Handle email/password sign in here

            },
            modifier = Modifier.fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            Text(text = "Sign in", fontSize = 16.sp)
        }
        TextButton(onClick = { navController.navigate("signup") }) {
            Text(text = "Don't have an account? Signup")
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Divider******************************************************
        Divider(
            color = Color.Gray,
            thickness = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Google Sign-In Button with Image***************************
        OutlinedButton(
            onClick = { Log.d("Signin", "6")
                MainActivity.AuthType = "GOOGLE"
                onSignInClick() },
            modifier = Modifier.fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .height(18.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Image(
                    painter = painterResource(id = R.drawable.icongoogle),
                    contentDescription = "Google Sign-In",
                    modifier = Modifier
                        .height(30.dp)
                )
                Text(
                    text = "Sign in with Google",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(start = 40.dp), // Adjust padding based on image size
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
