package com.findyourpet.app.domain

object OwnershipPolicy {
    fun canManagePost(currentUid: String, ownerId: String): Boolean =
        currentUid.isNotBlank() && currentUid == ownerId

    fun isChatParticipant(currentUid: String, ownerId: String, reporterId: String): Boolean =
        currentUid.isNotBlank() && (currentUid == ownerId || currentUid == reporterId)
}
