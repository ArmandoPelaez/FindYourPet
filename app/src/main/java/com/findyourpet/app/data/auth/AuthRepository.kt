package com.findyourpet.app.data.auth

import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val authState: StateFlow<AuthUiState>

    suspend fun signUpWithEmail(email: String, password: String, displayName: String): Result<AuthUser>

    suspend fun signInWithEmail(email: String, password: String): Result<AuthUser>

    suspend fun signInWithGoogleIdToken(idToken: String): Result<AuthUser>

    fun signOut()
}
