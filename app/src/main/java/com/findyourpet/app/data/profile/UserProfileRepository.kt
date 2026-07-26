package com.findyourpet.app.data.profile

import com.findyourpet.app.data.auth.AuthUser
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    fun observeProfile(uid: String): Flow<UserProfileDocument?>

    suspend fun ensureProfile(authUser: AuthUser): Result<UserProfileDocument>

    suspend fun updateProfile(profile: UserProfileDocument): Result<Unit>
}
