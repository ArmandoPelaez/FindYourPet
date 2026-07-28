package com.findyourpet.app.data.remote

object BackendCollections {
    const val USERS = "users"
    const val PET_POSTS = "petPosts"
    const val SIGHTINGS = "sightings"
    const val CHAT_SESSIONS = "chatSessions"
    const val MESSAGES = "messages"
    const val CONTACT_GRANTS = "contactGrants"
    const val OWNER_CONTACT_GRANT = "ownerContact"
    const val NOTIFICATIONS = "notifications"

    fun chatSessionId(postId: String, reporterId: String): String = "${postId}_$reporterId"
}
