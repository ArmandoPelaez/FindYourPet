package com.findyourpet.app.domain

import com.findyourpet.app.data.auth.AuthUiState
import com.findyourpet.app.data.profile.UserProfileDocument
import com.findyourpet.app.ui.viewmodel.UserProfile

object AuthSessionMapper {
    val signedOutUser = UserProfile(
        id = "",
        name = "Signed out",
        phone = "",
        email = ""
    )

    fun isAuthenticated(state: AuthUiState): Boolean = state is AuthUiState.SignedIn

    fun activeUser(state: AuthUiState, profile: UserProfileDocument?): UserProfile =
        when (state) {
            is AuthUiState.SignedIn -> UserProfile(
                id = state.user.uid,
                name = profile?.displayName?.ifBlank { state.user.displayName } ?: state.user.displayName,
                phone = profile?.phone ?: state.user.phone,
                email = profile?.email?.ifBlank { state.user.email } ?: state.user.email
            )
            else -> signedOutUser
        }
}
