package com.example.mybudgetapp.ui.screens

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import androidx.activity.ComponentActivity
import com.example.mybudgetapp.ui.model.UserData
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Call
import okhttp3.Callback
import java.io.IOException



class FacebookAuthUiClient(private val activity: Activity) {
    private val callbackManager = CallbackManager.Factory.create()
    val auth = FirebaseAuth.getInstance()
    var userData: UserData? = null

    fun login(onSuccess: (FirebaseUser?) -> Unit, onError: (Exception) -> Unit) {
        LoginManager.getInstance().logInWithReadPermissions(
            activity, listOf("email", "public_profile")
        )

        LoginManager.getInstance().registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    val accessToken = loginResult.accessToken?.token
                    Log.d("FacebookAuth1", "Facebook login successful: $accessToken")

                    if (accessToken != null) {
                        handleFacebookAccessToken(loginResult.accessToken,
                            onSuccess,onError)
                    } else {
                        Log.e("FacebookAuth1", "Access Token is null")
                        onError(FacebookException("Access Token is null"))
                    }
                }

                override fun onCancel() {

                }

                override fun onError(error: FacebookException) {
                    onError(error)
                }
            }
        )
    }

    private fun handleFacebookAccessToken(token: AccessToken,
                                          onSuccess: (FirebaseUser) -> Unit,
                                          onError: (Exception) -> Unit) {
        val credential = FacebookAuthProvider.getCredential(token.token)

       auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    userData =getSignedInUser()
                    user?.let { onSuccess(it) }
                } else {
                    onError(task.exception ?: Exception("Unknown error"))
                }
            }
    }

    fun getCallbackManager(): CallbackManager {
        return callbackManager
    }
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        fun getSignedInUser(): UserData? = FirebaseAuth.getInstance().currentUser?.run {
            UserData(
                userId = uid,
                username = displayName,
                profilePictureUrl = photoUrl?.toString()
            )
        }
    }

}
