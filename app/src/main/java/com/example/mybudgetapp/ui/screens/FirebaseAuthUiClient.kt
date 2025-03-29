package com.example.mybudgetapp.ui.screens

import android.util.Log
import com.example.mybudgetapp.ui.model.SignInResult
import com.example.mybudgetapp.ui.model.SignInState
import com.example.mybudgetapp.ui.model.UserData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseAuthUiClient {

    private val auth = Firebase.auth

    fun createAccountwithEmail(email: String, password: String): Flow<Authresponse> = callbackFlow {
        Log.d("FirebaseAuthClient", "12")
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful){
                    trySend(Authresponse.Success)
                }
                else {
                    //handle the case
                    trySend(Authresponse.Error(message = task.exception?.message ?: "Unknown error"))
                }

            }
        awaitClose()
    }
    fun signInwithEmail(email: String, password: String): Flow<Authresponse> = callbackFlow{
        Log.d("FirebaseAuthClient", "13")
        auth.signInWithEmailAndPassword(email,password)

            .addOnCompleteListener{ task ->
                if (task.isSuccessful){
                    val user = auth.currentUser
                    SignInResult(
                        data = user?.run {
                            UserData(
                                userId = uid,
                                username = displayName,
                                profilePictureUrl = photoUrl?.toString()
                            )
                        },
                        errorMessage = null
                    )
                    trySend(Authresponse.Success)

                }
                else {
                    trySend(Authresponse.Error(message = task.exception?.message ?: "Unknown error"))
                }
            }
        awaitClose()
    }
}

interface Authresponse {
    data object Success: Authresponse
    data class Error(val message: String): Authresponse
}