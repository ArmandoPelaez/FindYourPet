package com.findyourpet.app.data.auth

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository private constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {
    private val _authState = MutableStateFlow(firebaseAuth.currentUser.toAuthState())
    override val authState: StateFlow<AuthUiState> = _authState

    private val authListener = FirebaseAuth.AuthStateListener { auth ->
        _authState.value = auth.currentUser.toAuthState()
    }

    init {
        firebaseAuth.addAuthStateListener(authListener)
    }

    override suspend fun signUpWithEmail(
        email: String,
        password: String,
        displayName: String
    ): Result<AuthUser> = runCatching {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val user = requireNotNull(result.user) { "Firebase did not return a user." }
        if (displayName.isNotBlank()) {
            user.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()
            ).await()
        }
        user.reload().await()
        requireNotNull(firebaseAuth.currentUser ?: user).toAuthUser()
    }.also { updateStateFromResult(it) }

    override suspend fun signInWithEmail(email: String, password: String): Result<AuthUser> =
        runCatching {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            requireNotNull(result.user) { "Firebase did not return a user." }.toAuthUser()
        }.also { updateStateFromResult(it) }

    override suspend fun signInWithGoogleIdToken(idToken: String): Result<AuthUser> =
        runCatching {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            requireNotNull(result.user) { "Firebase did not return a user." }.toAuthUser()
        }.also { updateStateFromResult(it) }

    override fun signOut() {
        firebaseAuth.signOut()
        _authState.value = AuthUiState.SignedOut
    }

    private fun updateStateFromResult(result: Result<AuthUser>) {
        _authState.value = result.fold(
            onSuccess = { AuthUiState.SignedIn(it) },
            onFailure = { AuthUiState.Error(it.message ?: "Authentication failed.") }
        )
    }

    private fun FirebaseUser?.toAuthState(): AuthUiState =
        this?.toAuthUser()?.let(AuthUiState::SignedIn) ?: AuthUiState.SignedOut

    private fun FirebaseUser.toAuthUser(): AuthUser =
        AuthUser(
            uid = uid,
            displayName = displayName.orEmpty().ifBlank { email.orEmpty().substringBefore("@") },
            email = email.orEmpty(),
            phone = phoneNumber.orEmpty()
        )

    companion object {
        fun createOrNull(context: Context): FirebaseAuthRepository? {
            val appContext = context.applicationContext
            if (FirebaseApp.getApps(appContext).isEmpty()) {
                FirebaseApp.initializeApp(appContext)
            }
            return if (FirebaseApp.getApps(appContext).isEmpty()) {
                null
            } else {
                FirebaseAuthRepository(FirebaseAuth.getInstance())
            }
        }
    }
}
