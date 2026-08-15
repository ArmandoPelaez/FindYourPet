package com.findyourpet.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
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
    val ownerName: String
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
    val timestamp: Long,
    val idempotencyKey: String = ""
)

@Entity(
    tableName = "content_reports",
    indices = [
        Index(value = ["sightingId", "reportingUserId", "reason"], unique = true),
        Index(value = ["reportedUserId"])
    ]
)
data class ContentReportEntity(
    @PrimaryKey val id: String,
    val sightingId: String,
    val reportedUserId: String,
    val reportingUserId: String,
    val reason: String,
    val createdAt: Long,
    val status: String = MODERATION_PENDING_STATUS
)

@Entity(
    tableName = "user_blocks",
    indices = [
        Index(value = ["blockerUserId", "blockedUserId"], unique = true),
        Index(value = ["blockedUserId"])
    ]
)
data class UserBlockEntity(
    @PrimaryKey val id: String,
    val blockerUserId: String,
    val blockedUserId: String,
    val sourceSightingId: String,
    val createdAt: Long
)

@Entity(tableName = "app_notifications")
data class AppNotificationEntity(
    @PrimaryKey val id: String,
    val recipientId: String = "",
    val title: String,
    val message: String,
    val type: String, // ALERT; CHAT is retained only when decoding historical notifications.
    val targetId: String,
    val timestamp: Long,
    val isRead: Boolean = false,
    // Nullable legacy field retained solely for historical notification decoding.
    val chatId: String? = null,
    val sightingId: String? = null,
    val postId: String? = null
)

const val MODERATION_PENDING_STATUS = "PENDING"
