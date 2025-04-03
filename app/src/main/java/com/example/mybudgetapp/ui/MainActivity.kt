package com.example.mybudgetapp.ui

import SplashScreen
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mybudgetapp.ui.model.SignInState
import com.example.mybudgetapp.ui.model.UserData
import com.example.mybudgetapp.ui.screens.ChartScreen
import com.example.mybudgetapp.ui.screens.GoogleAuthUiClient
import com.example.mybudgetapp.ui.screens.HomeScreen
import com.example.mybudgetapp.ui.screens.InfoScreen
import com.example.mybudgetapp.ui.screens.SignInScreen
import com.example.mybudgetapp.ui.screens.SignUpScreen
import com.example.mybudgetapp.ui.screens.TransactionScreen

import com.example.mybudgetapp.ui.theme.MyBudgetAppTheme
import com.example.mybudgetapp.ui.viewModel.DateAndMonthViewModel
import com.example.mybudgetapp.ui.viewModel.ExpenseViewModel
import com.example.mybudgetapp.ui.viewModel.MainCategoryViewModel

import com.example.mybudgetapp.ui.viewModel.Signinviewmodel
import com.example.mybudgetapp.ui.viewModel.SubCategoryViewModel
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    var userData: UserData? = null

    //have to do initially in the main activity before create
    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseFirestore.setLoggingEnabled(true)
        enableEdgeToEdge()
        /*This is the correct one but still not implemented
        setContent {
            MyBudgetAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Navigation(
                        modifier = Modifier.padding(innerPadding),
                        authViewModel = authViewModel,
                        activity = this
                    )
                }
            }
        }*/
        setContent {
            MyBudgetAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    //create viewmodel instance
                    val dateAndMonthViewModel: DateAndMonthViewModel = viewModel()
                    val subCategoryViewModel: SubCategoryViewModel = viewModel()
                    val mainCategoryViewModel: MainCategoryViewModel = viewModel()
                    val expenseViewModel: ExpenseViewModel = viewModel()
                    val viewModel = viewModel<Signinviewmodel>()
                    val state by viewModel.state.collectAsStateWithLifecycle()
                    Log.d("Main1", "User Data: $userData")
                    NavHost(navController = navController,
                        startDestination = "splash",
                        modifier = Modifier.padding(innerPadding)) {
Log.d("Main", "1")
                        composable("splash") { SplashScreen(navController) }
                        composable("sign_in") {
                            Log.d("Main", "2")
                            LaunchedEffect(key1 = Unit) {

                                if(googleAuthUiClient.getSignedInUser() != null) {
                                    userData = googleAuthUiClient.getSignedInUser()
                                    if (state.isSignInSuccessful != false ) {
                                        navController.navigate("home")
                                    }
                                }
                            }

                            val launcher = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.StartIntentSenderForResult(),
                                onResult = { result ->
                                    if(result.resultCode == RESULT_OK) {
                                        lifecycleScope.launch {
                                            val signInResult = googleAuthUiClient.signInWithIntent(
                                                intent = result.data ?: return@launch
                                            )
                                            viewModel.onSignInResult(signInResult)
                                        }
                                    }
                                }
                            )

                            LaunchedEffect(key1 = state.isSignInSuccessful) {
                                Log.d("Main", state.toString())
                                if(state.isSignInSuccessful) {
                                    Toast.makeText(
                                        applicationContext,
                                        "Sign in successful",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    //userData = googleAuthUiClient.getSignedInUser()
                                    Log.d("Main", userData.toString())
                                    navController.navigate("home")
                                    viewModel.resetState()
                                }
                            }

                            SignInScreen(
                                state = state,
                                navController,
                                onSignInClick = {
                                    Log.d("Main", "2,1")
                                    lifecycleScope.launch {
                                        if (AuthType == "GOOGLE") {
                                            val signInIntentSender = googleAuthUiClient.signIn()
                                            launcher.launch(
                                                IntentSenderRequest.Builder(
                                                    signInIntentSender ?: return@launch
                                                ).build()
                                            )
                                        } else if (AuthType == "STANDARD") {
                                            val auth = FirebaseAuth.getInstance()
                                            auth.signInWithEmailAndPassword(email, password)
                                                .addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        navController.navigate("home")
                                                        viewModel.resetState()
                                                        android.util.Log.d("SignInscreen", "Sign-in successful: ${task.result.user?.uid},  ${task.result.user?.email}, ${task.result.user?.photoUrl}")
                                                    } else {
                                                        android.util.Log.e("SignIn", "Sign-in failed: ${task.exception?.message}")
                                                    }
                                                }
                                        }
                                    }
                                }
                            )
                        }
                        composable(route = "home") {Log.d("Main", "3")
                            HomeScreen(navController,
                                dateAndMonthViewModel,
                                subCategoryViewModel,
                                mainCategoryViewModel,
                                expenseViewModel )
                        }
                        composable(route = "signup") {Log.d("Main", "3")
                            SignUpScreen(navController)
                        }
                        composable(route = "outflow") {
                            TransactionScreen(
                                navController,
                                dateAndMonthViewModel,
                                expenseViewModel
                            )
                        }
                        composable(route = "info") {
                            InfoScreen(navController)
                        }
                        composable(route = "insights") {
                            ChartScreen(
                                navController,
                                dateAndMonthViewModel,
                                expenseViewModel
                            )
                        }
                    }
                }
            }
        }
    }
    companion object {
        var AuthType: String =""
        var email: String = ""
        var password: String = ""
    }
}
