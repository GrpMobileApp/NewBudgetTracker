package com.example.mybudgetapp.ui

import SplashScreen
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
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
import com.example.mybudgetapp.ui.screens.FacebookAuthUiClient
import com.example.mybudgetapp.ui.screens.GoogleAuthUiClient
import com.example.mybudgetapp.ui.screens.HomeScreen
import com.example.mybudgetapp.ui.screens.ProfileScreen
import com.example.mybudgetapp.ui.screens.SignInScreen
import com.example.mybudgetapp.ui.screens.SignUpScreen

import com.example.mybudgetapp.ui.theme.MyBudgetAppTheme
import com.example.mybudgetapp.ui.viewModel.CategoryViewModel
import com.example.mybudgetapp.ui.viewModel.DateAndMonthViewModel
import com.example.mybudgetapp.ui.viewModel.ExpenseViewModel
import com.example.mybudgetapp.ui.viewModel.MainCategoryViewModel
import com.example.mybudgetapp.ui.viewModel.Signinviewmodel
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    var userData: UserData? = null
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var FacebookAuthUiClient: FacebookAuthUiClient

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
        //this is for facebook part testing
        //initialize facebook SDK
        FacebookSdk.sdkInitialize(this)
        FacebookAuthUiClient = FacebookAuthUiClient(this)

// Use the new API instead of onActivityResult
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            FacebookAuthUiClient.getCallbackManager().onActivityResult(
                result.resultCode,
                result.resultCode,
                result.data
            )
        }

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
                    val viewModel = viewModel<Signinviewmodel>()
                    val state by viewModel.state.collectAsStateWithLifecycle()
                    Log.d("Main1", "User Data: $userData")
                    NavHost(navController = navController,
                        startDestination = "splash",
                        modifier = Modifier.padding(innerPadding)) {

                        composable("splash") { SplashScreen(navController) }
                        composable("sign_in") {

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
                                    userData = googleAuthUiClient.getSignedInUser()
                                    navController.navigate("home") {
                                        popUpTo("sign_in") { inclusive = true } // Clears sign-in from back stack
                                    }
                                    viewModel.resetState()
                                }
                            }

                            SignInScreen(
                                state = state,
                                navController,
                                onSignInClick = {

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
                                            userData= UserData(
                                                userId = auth.currentUser?.uid.toString(),
                                                username = auth.currentUser?.displayName,
                                                profilePictureUrl = ""//auth.currentUser?.photoUrl.toString()
                                            )
                                            auth.signInWithEmailAndPassword(email, password)
                                                .addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        navController.navigate("home")
                                                        viewModel.resetState()

                                                    } else {
                                                        Toast.makeText(
                                                            applicationContext,
                                                            "Sign in Fail,Please check the credentials and try again",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }
                                                }
                                        } else if(AuthType == "FACEBOOK"){

                                            //write facebook code here
                                            FacebookAuthUiClient.login(

                                                onSuccess = { userData ->
                                                    navController.navigate("home"){
                                                        popUpTo("sign_in") { inclusive = true } // Remove Profile screen from back stack
                                                    }
                                                    viewModel.resetState()
                                                },
                                                onError = { error ->
                                                    Toast.makeText(
                                                        applicationContext,
                                                        "Sign in Fail,Please try again",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                            )
                                        }
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
                        composable(route = "signup") {Log.d("Main", "3")
                            SignUpScreen(navController)
                        }
                        composable(route = "profile") {
                            ProfileScreen(navController,userData)
                        }
                    }
                }
            }
        }
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        FacebookAuthUiClient.getCallbackManager().onActivityResult(requestCode, resultCode, data)
    }
    companion object {
        var AuthType: String =""
        var email: String = ""
        var password: String = ""
    }
}
