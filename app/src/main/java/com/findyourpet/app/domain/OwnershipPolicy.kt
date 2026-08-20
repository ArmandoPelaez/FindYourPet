package com.findyourpet.app.domain

object OwnershipPolicy {
    fun canManagePost(currentUid: String, ownerId: String): Boolean =
        currentUid.isNotBlank() && currentUid == ownerId

    fun canReportSighting(currentUid: String, ownerId: String): Boolean =
        currentUid.isNotBlank() && ownerId.isNotBlank() && currentUid != ownerId

    fun canAppearInDiscoveryFeed(currentUid: String, ownerId: String): Boolean =
        currentUid.isBlank() || ownerId.isBlank() || currentUid != ownerId

    fun canAppearInDiscoveryFeed(currentUid: String, ownerId: String, status: String): Boolean =
        canAppearInDiscoveryFeed(currentUid, ownerId) && status.uppercase() != REUNITED_STATUS

    fun canMarkAsReunited(currentUid: String, ownerId: String, status: String): Boolean =
        canManagePost(currentUid, ownerId) && status.uppercase() == LOST_STATUS

    private const val LOST_STATUS = "PERDIDO"
    private const val REUNITED_STATUS = "REUNIDO"

}
