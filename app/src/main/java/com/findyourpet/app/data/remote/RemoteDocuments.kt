package com.findyourpet.app.data.remote

data class PetPostDocument(
    val id: String = "",
    val petName: String = "",
    val species: String = "",
    val breed: String = "",
    val color: String = "",
    val features: String = "",
    val characteristics: String = "",
    val status: String = "",
    val photoUri: String = "",
    val mediaProvider: String = "",
    val mediaPublicId: String = "",
    val mediaContentType: String = "",
    val mediaSource: String = "",
    val dateLost: Long = 0L,
    val lastSeenLocation: String = "",
    val locationSource: String = "",
    val publicLocationName: String = "",
    val rewardAmount: String = "",
    val ownerId: String = "",
    val ownerName: String = "",
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)

data class SightingDocument(
    val id: String = "",
    val postId: String = "",
    val ownerId: String = "",
    val reporterId: String = "",
    val reporterName: String = "",
    val photoUri: String = "",
    val mediaProvider: String = "",
    val mediaPublicId: String = "",
    val mediaContentType: String = "",
    val mediaSource: String = "",
    val locationName: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val locationSource: String = "",
    val preciseLocationConsented: Boolean = false,
    val notes: String = "",
    val timestamp: Long = 0L,
    val idempotencyKey: String = "",
    val createdAt: Long = 0L
)

data class ChatSessionDocument(
    val id: String = "",
    val postId: String = "",
    val petName: String = "",
    val petPhotoUri: String = "",
    val ownerId: String = "",
    val reporterId: String = "",
    val reporterName: String = "",
    val participantIds: List<String> = emptyList(),
    val lastMessage: String = "",
    val lastMessageTimestamp: Long = 0L,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)

data class ChatMessageDocument(
    val id: String = "",
    val chatId: String = "",
    val postId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val text: String = "",
    val photoUri: String? = null,
    val timestamp: Long = 0L,
    val isSystemMessage: Boolean = false,
    val type: String = "text",
    val sightingId: String = "",
    val ownerId: String = "",
    val reporterId: String = "",
    val snapshot: Map<String, Any?> = emptyMap(),
    val createdAt: Long = 0L
)

data class AppNotificationDocument(
    val id: String = "",
    val recipientId: String = "",
    val title: String = "",
    val message: String = "",
    val type: String = "",
    val targetId: String = "",
    val timestamp: Long = 0L,
    val isRead: Boolean = false,
    val chatId: String = "",
    val sightingId: String = "",
    val postId: String = "",
    val createdAt: Long = 0L
)
