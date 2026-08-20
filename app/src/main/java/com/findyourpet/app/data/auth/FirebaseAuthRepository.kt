package com.findyourpet.app.data.auth

import android.content.Context
import com.findyourpet.app.data.auth.AuthFailure
import com.findyourpet.app.data.auth.AuthDomainException
import com.findyourpet.app.data.auth.AuthMessages
import com.findyourpet.app.domain.AuthProvider
import com.findyourpet.app.domain.authProvider
import com.findyourpet.app.domain.emailPasswordConflict
import com.findyourpet.app.domain.googleConflict
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.CancellationException
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
    ): Result<AuthUser> = authenticate {
        providerConflictForEmail(email)?.let { throw AuthDomainException(it) }
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
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<AuthUser> =
        authenticate {
            providerConflictForEmail(email)?.let { throw AuthDomainException(it) }
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            requireNotNull(result.user) { "Firebase did not return a user." }.toAuthUser()
        }

    override suspend fun signInWithGoogleIdToken(idToken: String): Result<AuthUser> =
        authenticate {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            try {
                val result = firebaseAuth.signInWithCredential(credential).await()
                requireNotNull(result.user) { "Firebase did not return a user." }.toAuthUser()
            } catch (error: FirebaseAuthUserCollisionException) {
                val conflictingEmail = error.email.orEmpty().trim()
                if (conflictingEmail.isBlank()) {
                    throw AuthDomainException(AuthFailure.AuthenticationFailed, error)
                }
                val provider = providerForEmail(conflictingEmail)
                provider.googleConflict()?.let { throw AuthDomainException(it, error) }
                throw AuthDomainException(AuthFailure.AuthenticationFailed, error)
            }
        }

    override fun signOut() {
        firebaseAuth.signOut()
        _authState.value = AuthUiState.SignedOut
    }

    private suspend fun providerConflictForEmail(email: String): AuthFailure? =
        providerForEmail(email).emailPasswordConflict()

    private suspend fun providerForEmail(email: String): AuthProvider = runCatching {
        firebaseAuth.fetchSignInMethodsForEmail(email.trim()).await().getSignInMethods().orEmpty().authProvider()
    }.getOrDefault(AuthProvider.UNKNOWN)

    private suspend fun authenticate(block: suspend () -> AuthUser): Result<AuthUser> {
        val result = try {
            Result.success(block())
        } catch (error: CancellationException) {
            throw error
        } catch (error: AuthDomainException) {
            Result.failure(error)
        } catch (error: Throwable) {
            Result.failure(AuthDomainException(AuthFailure.AuthenticationFailed, error))
        }
        updateStateFromResult(result)
        return result
    }

    private fun updateStateFromResult(result: Result<AuthUser>) {
        _authState.value = result.fold(
            onSuccess = { AuthUiState.SignedIn(it) },
            onFailure = { error ->
                firebaseAuth.currentUser?.toAuthState()
                    ?: AuthUiState.Error(AuthMessages.forFailure(error))
            }
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
