package com.findyourpet.app.data.profile

import com.findyourpet.app.data.auth.AuthUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class UnavailableUserProfileRepository : UserProfileRepository {
    override fun observeProfile(uid: String): Flow<UserProfileDocument?> = flowOf(null)

    override suspend fun ensureProfile(authUser: AuthUser): Result<UserProfileDocument> =
        Result.failure(IllegalStateException("Firestore profile storage is not configured."))

    override suspend fun updateProfile(profile: UserProfileDocument): Result<Unit> =
        Result.failure(IllegalStateException("Firestore profile storage is not configured."))
}
