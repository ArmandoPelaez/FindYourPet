package com.findyourpet.app.data.remote

data class PetPostDocument(
    val id: String = "",
    val petName: String = "",
    val species: String = "",
    val breed: String = "",
    val color: String = "",
    val features: String = "",
    val status: String = "",
    val photoUri: String = "",
    val dateLost: Long = 0L,
    val lastSeenLocation: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val rewardAmount: String = "",
    val ownerId: String = "",
    val ownerName: String = "",
    val ownerPhone: String = "",
    val ownerEmail: String = "",
    val ownerAddress: String = "",
    val isContactRevealedToAll: Boolean = false,
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
    val locationName: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val notes: String = "",
    val timestamp: Long = 0L,
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
    val isContactSharedByOwner: Boolean = false,
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
    val createdAt: Long = 0L
)
