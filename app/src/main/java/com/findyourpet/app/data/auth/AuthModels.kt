package com.findyourpet.app.data.auth

data class AuthUser(
    val uid: String,
    val displayName: String,
    val email: String,
    val phone: String = ""
)

sealed interface AuthUiState {
    data object Loading : AuthUiState
    data object SignedOut : AuthUiState
    data class SignedIn(val user: AuthUser) : AuthUiState
    data class Error(val message: String) : AuthUiState
    data class Unconfigured(val message: String) : AuthUiState
}
