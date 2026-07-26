package com.findyourpet.app.data.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UnavailableAuthRepository(
    message: String = "Firebase is not configured on this device."
) : AuthRepository {
    override val authState: StateFlow<AuthUiState> = MutableStateFlow(AuthUiState.Unconfigured(message))

    override suspend fun signUpWithEmail(
        email: String,
        password: String,
        displayName: String
    ): Result<AuthUser> = Result.failure(IllegalStateException(unconfiguredMessage))

    override suspend fun signInWithEmail(email: String, password: String): Result<AuthUser> =
        Result.failure(IllegalStateException(unconfiguredMessage))

    override suspend fun signInWithGoogleIdToken(idToken: String): Result<AuthUser> =
        Result.failure(IllegalStateException(unconfiguredMessage))

    override fun signOut() = Unit

    private companion object {
        const val unconfiguredMessage = "Firebase configuration is required before signing in."
    }
}
