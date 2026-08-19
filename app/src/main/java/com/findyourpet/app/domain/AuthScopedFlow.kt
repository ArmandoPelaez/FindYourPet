package com.findyourpet.app.domain

import com.findyourpet.app.data.auth.AuthUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

/**
 * Binds a remote flow to the authenticated Firebase UID.
 *
 * The provider used to authenticate is intentionally irrelevant here: once Firebase exposes a
 * signed-in user, every authenticated backend flow follows the same lifecycle.
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal fun <T> Flow<AuthUiState>.flatMapLatestForAuthenticatedUser(
    signedOut: () -> Flow<T>,
    signedIn: (uid: String) -> Flow<T>,
): Flow<T> = map { state ->
    (state as? AuthUiState.SignedIn)?.user?.uid?.takeIf(String::isNotBlank)
}.distinctUntilChanged().flatMapLatest { uid ->
    uid?.let(signedIn) ?: signedOut()
}
