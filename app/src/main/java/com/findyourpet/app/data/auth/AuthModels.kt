package com.findyourpet.app.data.auth

data class AuthUser(
    val uid: String,
    val displayName: String,
    val email: String,
    val phone: String = ""
)

sealed interface AuthFailure {
    data object EmailPasswordRequired : AuthFailure
    data object GoogleRequired : AuthFailure
    data object AuthenticationFailed : AuthFailure
}

class AuthDomainException(
    val failure: AuthFailure,
    cause: Throwable? = null,
) : IllegalStateException(null, cause)

object AuthMessages {
    const val emailPasswordRequired =
        "Esta cuenta fue creada con correo y contraseña. Iniciá sesión utilizando tu contraseña."
    const val googleRequired =
        "Esta cuenta fue creada utilizando Google. Iniciá sesión con Google."
    const val authenticationFailed =
        "No pudimos iniciar sesión. Si creaste tu cuenta con Google, seleccioná 'Continuar con Google'."

    fun forFailure(error: Throwable): String = when (error) {
        is AuthDomainException -> when (error.failure) {
            AuthFailure.EmailPasswordRequired -> emailPasswordRequired
            AuthFailure.GoogleRequired -> googleRequired
            AuthFailure.AuthenticationFailed -> authenticationFailed
        }
        else -> authenticationFailed
    }
}

sealed interface AuthUiState {
    data object Loading : AuthUiState
    data object SignedOut : AuthUiState
    data class SignedIn(val user: AuthUser) : AuthUiState
    data class Error(val message: String) : AuthUiState
    data class Unconfigured(val message: String) : AuthUiState
}
