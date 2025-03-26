package com.example.mybudgetapp.ui

import android.os.Bundle
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
import com.example.mybudgetapp.ui.screens.GoogleAuthUiClient
import com.example.mybudgetapp.ui.screens.HomeScreen
import com.example.mybudgetapp.ui.screens.SignInScreen
import com.example.mybudgetapp.ui.theme.MyBudgetAppTheme
import com.example.mybudgetapp.ui.viewModel.CategoryViewModel
import com.example.mybudgetapp.ui.viewModel.DateAndMonthViewModel
import com.example.mybudgetapp.ui.viewModel.ExpenseViewModel
import com.example.mybudgetapp.ui.viewModel.MainCategoryViewModel
import com.example.mybudgetapp.ui.viewModel.Signinviewmodel
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    //have to do initially in the main activity before create
    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                    val categoryViewModel: CategoryViewModel = viewModel()
                    val mainCategoryViewModel: MainCategoryViewModel = viewModel()
                    val expenseViewModel: ExpenseViewModel = viewModel()

                    NavHost(navController = navController,
                        startDestination = "sign_in",
                        modifier = Modifier.padding(innerPadding)) {
                        lifecycleScope.launch {
                            googleAuthUiClient.signOut() // Properly call the suspend function inside a coroutine
                        }
                        composable("sign_in") {
                            val viewModel = viewModel<Signinviewmodel>()
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            LaunchedEffect(key1 = Unit) {
                                if(googleAuthUiClient.getSignedInUser() != null) {
                                    val userData = googleAuthUiClient.getSignedInUser()
                                    if (userData != null) {
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
                                if(state.isSignInSuccessful) {
                                    Toast.makeText(
                                        applicationContext,
                                        "Sign in successful",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    navController.navigate("home")
                                    viewModel.resetState()
                                }
                            }

                            SignInScreen(
                                state = state,
                                onSignInClick = {
                                    lifecycleScope.launch {
                                        val signInIntentSender = googleAuthUiClient.signIn()
                                        launcher.launch(
                                            IntentSenderRequest.Builder(
                                                signInIntentSender ?: return@launch
                                            ).build()
                                        )
                                    }
                                }
                            )
                        }
                        composable(route = "home") {
                            HomeScreen(navController,
                                dateAndMonthViewModel,
                                categoryViewModel,
                                mainCategoryViewModel,
                                expenseViewModel )
                        }
                    }
                }
            }
        }
    }
}
