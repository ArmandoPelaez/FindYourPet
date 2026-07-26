package com.findyourpet.app.data.profile

import com.findyourpet.app.data.auth.AuthUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class FirestoreUserProfileRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : UserProfileRepository {
    override fun observeProfile(uid: String): Flow<UserProfileDocument?> =
        callbackFlow {
            val registration = firestore.collection(collectionName)
                .document(uid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    trySend(snapshot?.toObject(UserProfileDocument::class.java))
                }
            awaitClose { registration.remove() }
        }

    override suspend fun ensureProfile(authUser: AuthUser): Result<UserProfileDocument> =
        runCatching {
            val ref = firestore.collection(collectionName).document(authUser.uid)
            val existing = ref.get().await().toObject(UserProfileDocument::class.java)
            if (existing != null) return@runCatching existing

            val now = System.currentTimeMillis()
            val profile = UserProfileDocument(
                uid = authUser.uid,
                displayName = authUser.displayName,
                email = authUser.email,
                phone = authUser.phone,
                createdAt = now,
                updatedAt = now
            )
            ref.set(profile).await()
            profile
        }

    override suspend fun updateProfile(profile: UserProfileDocument): Result<Unit> =
        runCatching {
            val updated = profile.copy(updatedAt = System.currentTimeMillis())
            firestore.collection(collectionName)
                .document(profile.uid)
                .set(updated, SetOptions.merge())
                .await()
        }

    private companion object {
        const val collectionName = "users"
    }
}
