package com.findyourpet.app.data.profile

data class UserProfileDocument(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)
