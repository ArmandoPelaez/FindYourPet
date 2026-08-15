package com.findyourpet.app.data.remote

object BackendCollections {
    const val USERS = "users"
    const val PET_POSTS = "petPosts"
    const val SIGHTINGS = "sightings"
    const val NOTIFICATIONS = "notifications"
    const val CONTENT_REPORTS = "contentReports"
    const val USER_BLOCKS = "userBlocks"

    fun userBlockId(blockerUserId: String, blockedUserId: String): String =
        "${blockerUserId}_$blockedUserId"

    fun contentReportId(sightingId: String, reportingUserId: String, reason: String): String =
        "${sightingId}_${reportingUserId}_$reason"
}
