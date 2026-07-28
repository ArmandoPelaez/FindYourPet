package com.findyourpet.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pet_posts")
data class PetPostEntity(
    @PrimaryKey val id: String,
    val petName: String,
    val species: String, // Perro, Gato, Ave, Otro
    val breed: String,
    val color: String,
    val features: String,
    val status: String, // PERDIDO, AVISTADO, REUNIDO
    val photoUri: String,
    val dateLost: Long,
    val lastSeenLocation: String,
    val latitude: Double,
    val longitude: Double,
    val rewardAmount: String,
    val ownerId: String,
    val ownerName: String,
    val ownerPhone: String,
    val ownerEmail: String,
    val ownerAddress: String
)

@Entity(tableName = "sighting_alerts")
data class SightingAlertEntity(
    @PrimaryKey val id: String,
    val postId: String,
    val ownerId: String = "",
    val reporterId: String,
    val reporterName: String,
    val photoUri: String,
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val notes: String,
    val timestamp: Long
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val chatId: String,
    val postId: String,
    val senderId: String,
    val senderName: String,
    val text: String,
    val photoUri: String?,
    val timestamp: Long,
    val isSystemMessage: Boolean = false
)

@Entity(tableName = "chat_sessions")
data class ChatSessionEntity(
    @PrimaryKey val id: String,
    val postId: String,
    val petName: String,
    val petPhotoUri: String,
    val ownerId: String,
    val reporterId: String,
    val reporterName: String,
    val lastMessage: String,
    val lastMessageTimestamp: Long,
    val isContactSharedByOwner: Boolean = false
)

@Entity(tableName = "contact_grants")
data class ContactGrantEntity(
    @PrimaryKey val id: String,
    val chatId: String,
    val postId: String,
    val ownerId: String,
    val reporterId: String,
    val sharedBy: String,
    val sharedAt: Long,
    val revokedAt: Long? = null,
    val isActive: Boolean = false,
    val ownerName: String,
    val ownerPhone: String,
    val ownerEmail: String
)

@Entity(tableName = "app_notifications")
data class AppNotificationEntity(
    @PrimaryKey val id: String,
    val recipientId: String = "",
    val title: String,
    val message: String,
    val type: String, // ALERT, CHAT, CONTACT_SHARED
    val targetId: String,
    val timestamp: Long,
    val isRead: Boolean = false
)
