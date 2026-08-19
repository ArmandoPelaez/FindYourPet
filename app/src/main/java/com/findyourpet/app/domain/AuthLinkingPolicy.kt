package com.findyourpet.app.domain

/**
 * The email is the only safe bridge between the pending Google credential and the password
 * sign-in that resolves an account-exists-with-different-credential collision.
 */
internal fun shouldLinkPendingGoogleCredential(
    pendingEmail: String,
    signedInEmail: String,
): Boolean {
    val normalizedPendingEmail = pendingEmail.trim()
    val normalizedSignedInEmail = signedInEmail.trim()
    return normalizedPendingEmail.isNotBlank() &&
        normalizedPendingEmail.equals(normalizedSignedInEmail, ignoreCase = true)
}

internal class AccountLinkRequiredException(email: String) : IllegalStateException(
    "Este correo ya tiene una cuenta con email y contraseña. " +
        "Ingresá esa contraseña y tocá Entrar para vincular Google ($email)."
)
