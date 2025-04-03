package com.example.mybudgetapp.ui.screens

import android.app.Activity
import android.content.Intent
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.example.mybudgetapp.ui.model.UserData


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
                override fun onSuccess(result: LoginResult) {
                    handleFacebookAccessToken(result.accessToken,
                        onSuccess,onError)
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
    fun getSignedInUser(): UserData? = FirebaseAuth.getInstance().currentUser?.run {
        UserData(
            userId = uid,
            username = displayName,
            profilePictureUrl = photoUrl?.toString()
        )
    }


}
