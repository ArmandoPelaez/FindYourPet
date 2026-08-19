package com.findyourpet.app

import com.findyourpet.app.data.auth.AuthUiState
import com.findyourpet.app.data.auth.AuthUser
import com.findyourpet.app.domain.flatMapLatestForAuthenticatedUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthScopedFlowTest {
  @Test
  fun signedOutState_usesSignedOutFlow_withoutStartingAuthenticatedFlow() = runTest {
    val authState = MutableStateFlow<AuthUiState>(AuthUiState.SignedOut)
    var authenticatedStarts = 0

    val value = authState.flatMapLatestForAuthenticatedUser(
      signedOut = { flowOf("signed-out") },
      signedIn = {
        authenticatedStarts++
        flowOf("signed-in")
      }
    ).firstValue()

    assertEquals("signed-out", value)
    assertEquals(0, authenticatedStarts)
  }

  @Test
  fun signedInState_startsTheAuthenticatedFlow_withTheFirebaseUid() = runTest {
    val authState = MutableStateFlow<AuthUiState>(signedIn("uid_google"))
    var requestedUid: String? = null

    val value = authState.flatMapLatestForAuthenticatedUser(
      signedOut = { flowOf("signed-out") },
      signedIn = { uid ->
        requestedUid = uid
        flowOf("feed-for-$uid")
      }
    ).firstValue()

    assertEquals("feed-for-uid_google", value)
    assertEquals("uid_google", requestedUid)
  }

  @Test
  fun logout_cancelsPreviousListener_andNextLoginCreatesANewOne() = runTest {
    val authState = MutableStateFlow<AuthUiState>(signedIn("uid_google"))
    val startedUids = mutableListOf<String>()
    val cancelledUids = mutableListOf<String>()

    val scopedFlow = authState.flatMapLatestForAuthenticatedUser(
      signedOut = { flowOf("signed-out") },
      signedIn = { uid ->
        flow {
          startedUids += uid
          emit("feed-for-$uid")
          try {
            awaitCancellation()
          } finally {
            cancelledUids += uid
          }
        }
      }
    )
    val collection = backgroundScope.launch { scopedFlow.collect() }
    runCurrent()

    assertEquals(listOf("uid_google"), startedUids)

    authState.value = AuthUiState.SignedOut
    runCurrent()
    assertEquals(listOf("uid_google"), cancelledUids)

    authState.value = signedIn("uid_google")
    runCurrent()
    assertEquals(listOf("uid_google", "uid_google"), startedUids)

    collection.cancel()
  }

  @Test
  fun repeatedSignedInState_forSameUid_doesNotDuplicateListener() = runTest {
    val authState = MutableStateFlow<AuthUiState>(signedIn("uid_google"))
    var starts = 0

    val scopedFlow = authState.flatMapLatestForAuthenticatedUser(
      signedOut = { flowOf("signed-out") },
      signedIn = {
        starts++
        flowOf("feed")
      }
    )
    val collection = backgroundScope.launch { scopedFlow.collect() }
    runCurrent()

    authState.value = signedIn("uid_google")
    runCurrent()

    assertEquals(1, starts)
    collection.cancel()
  }

  @Test
  fun loadingAndErrorStates_cancelAuthenticatedFlow_andExposeSignedOutState() = runTest {
    val authState = MutableStateFlow<AuthUiState>(signedIn("uid_google"))
    val values = mutableListOf<String>()
    val scopedFlow = authState.flatMapLatestForAuthenticatedUser(
      signedOut = { flowOf("signed-out") },
      signedIn = { flowOf("signed-in") }
    )
    val collection = backgroundScope.launch { scopedFlow.collect { values += it } }
    runCurrent()

    authState.value = AuthUiState.Loading
    runCurrent()
    authState.value = AuthUiState.Error("Authentication failed")
    runCurrent()

    assertTrue(values.contains("signed-in"))
    assertEquals("signed-out", values.last())
    collection.cancel()
  }

  private fun signedIn(uid: String): AuthUiState.SignedIn =
    AuthUiState.SignedIn(
      AuthUser(
        uid = uid,
        displayName = "Test User",
        email = "test@example.com"
      )
    )

  private suspend fun <T> kotlinx.coroutines.flow.Flow<T>.firstValue(): T =
    first()
}
