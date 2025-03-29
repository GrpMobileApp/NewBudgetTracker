package com.example.mybudgetapp.ui.screens

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
import com.example.mybudgetapp.ui.MainActivity.Companion
import com.example.mybudgetapp.ui.R
import com.example.mybudgetapp.ui.model.SignInState
import com.google.firebase.auth.FirebaseAuth


@Composable
fun SignUpScreen(
    navController: NavController,
) {


    //variables for email
    var email by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    var isError by remember { mutableStateOf(false) }

    //variables for password
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isPasswordError by remember { mutableStateOf(false) }  // Initialize as false
    var passwordTouched by remember { mutableStateOf(false) }  // Track if password field was touched

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
            text = "Sign Up to My Budget",
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

        // Register Button ***********************************************************************
        Button(
            onClick = {
                // Trigger password validation when the button is clicked
                if (password.length < 6) {
                    isPasswordError = true
                }

                // Handle email/password sign up here
                val auth = FirebaseAuth.getInstance()
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            navController.navigate("sign_in")

                        }else {
                            Log.e("SignUp", "Sign-un failed: ${task.exception?.message}")
                        }
                    }

            },
            modifier = Modifier.fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            Text(text = "Sign Up", fontSize = 16.sp)
        }
        TextButton(onClick = { navController.navigate("sign_in") }) {
            Text(text = "Already have an account, SignIn")
        }
        Spacer(modifier = Modifier.height(8.dp))

    }
}
