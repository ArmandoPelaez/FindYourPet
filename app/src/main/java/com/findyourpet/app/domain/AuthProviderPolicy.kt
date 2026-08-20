package com.findyourpet.app.domain

import com.findyourpet.app.data.auth.AuthFailure

internal enum class AuthProvider {
    EMAIL_PASSWORD,
    GOOGLE,
    UNKNOWN,
}

internal fun List<String>.authProvider(): AuthProvider {
    val normalized = map(String::trim).filter(String::isNotBlank).toSet()
    return when {
        normalized == setOf("password") -> AuthProvider.EMAIL_PASSWORD
        normalized == setOf("google.com") -> AuthProvider.GOOGLE
        else -> AuthProvider.UNKNOWN
    }
}

internal fun AuthProvider.emailPasswordConflict(): AuthFailure? = when (this) {
    AuthProvider.GOOGLE -> AuthFailure.GoogleRequired
    AuthProvider.EMAIL_PASSWORD,
    AuthProvider.UNKNOWN -> null
}

internal fun AuthProvider.googleConflict(): AuthFailure? = when (this) {
    AuthProvider.EMAIL_PASSWORD -> AuthFailure.EmailPasswordRequired
    AuthProvider.GOOGLE,
    AuthProvider.UNKNOWN -> null
}
