package com.example.mybudgetapp.ui.viewModel

import androidx.lifecycle.ViewModel
import com.example.mybudgetapp.ui.model.SignInResult
import com.example.mybudgetapp.ui.model.SignInState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class Signinviewmodel: ViewModel() {

    //Google Part***********************************************************************
    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun onSignInResult(result: SignInResult) {
        _state.update { it.copy(
            isSignInSuccessful = result.data != null ,
            signInError = result.errorMessage
        ) }
    }
    // Handle sign-in failure (e.g., Facebook login error)
    fun onSignInFailure(errorMessage: String) {
        _state.update { it.copy(
            isSignInSuccessful = false,
            signInError = errorMessage
        ) }
    }
    fun resetState() {
        _state.update { SignInState() }
    }
    fun updateSignInState(isSuccessful: Boolean) {
        _state.value = _state.value.copy(isSignInSuccessful = isSuccessful)
    }
    //**********************************************************************************
}