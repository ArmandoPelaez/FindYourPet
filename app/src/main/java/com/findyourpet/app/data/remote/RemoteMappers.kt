package com.findyourpet.app.data.remote

import com.findyourpet.app.data.local.entity.AppNotificationEntity
import com.findyourpet.app.data.local.entity.ChatMessageEntity
import com.findyourpet.app.data.local.entity.ChatSessionEntity
import com.findyourpet.app.data.local.entity.PetPostEntity
import com.findyourpet.app.data.local.entity.SightingAlertEntity
import com.findyourpet.app.data.local.entity.LEGACY_TEXT_MESSAGE_TYPE
import com.findyourpet.app.data.local.entity.SIGHTING_ALERT_MESSAGE_TYPE
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue

object RemoteMappers {
    fun PetPostEntity.toDocument(
        createdAt: Any = FieldValue.serverTimestamp(),
        mediaProvider: String = "",
        mediaPublicId: String = "",
        mediaContentType: String = "",
        mediaSource: String = "",
        locationSource: String = "MANUAL_COARSE"
    ): Map<String, Any?> =
        mapOf(
            "id" to id,
            "petName" to petName,
            "species" to species,
            "breed" to breed,
            "color" to color,
            "features" to features,
            "characteristics" to characteristics,
            "status" to status,
            "photoUri" to photoUri,
            "mediaProvider" to mediaProvider,
            "mediaPublicId" to mediaPublicId,
            "mediaContentType" to mediaContentType,
            "mediaSource" to mediaSource,
            "dateLost" to dateLost,
            "lastSeenLocation" to lastSeenLocation,
            "locationSource" to locationSource,
            "publicLocationName" to lastSeenLocation,
            "rewardAmount" to rewardAmount,
            "ownerId" to ownerId,
            "ownerName" to ownerName,
            "createdAt" to createdAt,
            "updatedAt" to FieldValue.serverTimestamp()
        )

    fun Map<String, Any?>.toPetPostEntity(documentId: String = string("id")): PetPostEntity =
        PetPostEntity(
            id = string("id").ifBlank { documentId },
            petName = string("petName"),
            species = string("species"),
            breed = string("breed"),
            color = string("color"),
            features = string("features"),
            characteristics = string("characteristics"),
            status = string("status").ifBlank { "PERDIDO" },
            photoUri = string("photoUri"),
            dateLost = long("dateLost"),
            lastSeenLocation = string("lastSeenLocation"),
            latitude = double("latitude"),
            longitude = double("longitude"),
            rewardAmount = string("rewardAmount"),
            ownerId = string("ownerId"),
            ownerName = string("ownerName")
        )

    fun DocumentSnapshot.toPetPostEntity(): PetPostEntity? =
        data?.toPetPostEntity(id)

    fun SightingAlertEntity.toDocument(
        ownerId: String,
        createdAt: Any = FieldValue.serverTimestamp(),
        mediaProvider: String = "",
        mediaPublicId: String = "",
        mediaContentType: String = "",
        mediaSource: String = "",
        locationSource: String = "MANUAL_COARSE",
        preciseLocationConsented: Boolean = false
    ): Map<String, Any?> =
        mapOf(
            "id" to id,
            "postId" to postId,
            "ownerId" to ownerId,
            "reporterId" to reporterId,
            "reporterName" to reporterName,
            "photoUri" to photoUri,
            "mediaProvider" to mediaProvider,
            "mediaPublicId" to mediaPublicId,
            "mediaContentType" to mediaContentType,
            "mediaSource" to mediaSource,
            "locationName" to locationName,
            "latitude" to latitude,
            "longitude" to longitude,
            "locationSource" to locationSource,
            "preciseLocationConsented" to preciseLocationConsented,
            "notes" to notes,
            "timestamp" to timestamp,
            "idempotencyKey" to idempotencyKey,
            "createdAt" to createdAt
        )

    fun Map<String, Any?>.toSightingEntity(documentId: String = string("id")): SightingAlertEntity =
        SightingAlertEntity(
            id = string("id").ifBlank { documentId },
            postId = string("postId"),
            ownerId = string("ownerId"),
            reporterId = string("reporterId"),
            reporterName = string("reporterName"),
            photoUri = string("photoUri"),
            locationName = string("locationName"),
            latitude = double("latitude"),
            longitude = double("longitude"),
            notes = string("notes"),
            timestamp = long("timestamp"),
            idempotencyKey = string("idempotencyKey")
        )

    fun ChatSessionEntity.toDocument(createdAt: Any = FieldValue.serverTimestamp()): Map<String, Any?> =
        mapOf(
            "id" to id,
            "postId" to postId,
            "petName" to petName,
            "petPhotoUri" to petPhotoUri,
            "ownerId" to ownerId,
            "reporterId" to reporterId,
            "reporterName" to reporterName,
            "participantIds" to listOf(ownerId, reporterId).distinct(),
            "lastMessage" to lastMessage,
            "lastMessageTimestamp" to lastMessageTimestamp,
            "createdAt" to createdAt,
            "updatedAt" to FieldValue.serverTimestamp()
        )

    fun Map<String, Any?>.toChatSessionEntity(documentId: String = string("id")): ChatSessionEntity =
        ChatSessionEntity(
            id = string("id").ifBlank { documentId },
            postId = string("postId"),
            petName = string("petName"),
            petPhotoUri = string("petPhotoUri"),
            ownerId = string("ownerId"),
            reporterId = string("reporterId"),
            reporterName = string("reporterName"),
            lastMessage = string("lastMessage").let { value ->
                if (value.isBlank()) "" else "Actividad en la conversacion"
            },
            lastMessageTimestamp = long("lastMessageTimestamp")
        )

    fun ChatMessageEntity.toDocument(createdAt: Any = FieldValue.serverTimestamp()): Map<String, Any?> =
        buildMap {
            put("id", id)
            put("chatId", chatId)
            put("postId", postId)
            put("senderId", senderId)
            put("senderName", senderName)
            put("text", text)
            put("photoUri", photoUri)
            put("timestamp", timestamp)
            put("isSystemMessage", isSystemMessage)
            put("type", type)
            put("createdAt", createdAt)
            if (type == SIGHTING_ALERT_MESSAGE_TYPE) {
                require(!sightingId.isNullOrBlank()) { "A sighting alert requires sightingId." }
                require(!ownerId.isNullOrBlank() && !reporterId.isNullOrBlank()) {
                    "A sighting alert requires immutable participant identities."
                }
                put("sightingId", sightingId)
                put("ownerId", ownerId)
                put("reporterId", reporterId)
                put(
                    "snapshot",
                    mapOf(
                        "petName" to snapshotPetName.orEmpty(),
                        "photoAttachmentUri" to photoAttachmentUri,
                        "locationDisplay" to locationDisplay.orEmpty(),
                        "generalDetails" to generalDetails.orEmpty(),
                        "timestamp" to (snapshotTimestamp ?: timestamp)
                    )
                )
            } else if (type.isBlank()) {
                put("type", LEGACY_TEXT_MESSAGE_TYPE)
            }
        }

    fun Map<String, Any?>.toChatMessageEntity(documentId: String = string("id")): ChatMessageEntity =
        ChatMessageEntity(
            id = string("id").ifBlank { documentId },
            chatId = string("chatId"),
            postId = string("postId"),
            senderId = string("senderId"),
            senderName = string("senderName"),
            text = string("text"),
            photoUri = this["photoUri"] as? String,
            timestamp = long("timestamp"),
            isSystemMessage = bool("isSystemMessage"),
            type = string("type").ifBlank { LEGACY_TEXT_MESSAGE_TYPE },
            sightingId = string("sightingId").ifBlank { null },
            ownerId = string("ownerId").ifBlank { null },
            reporterId = string("reporterId").ifBlank { null },
            snapshotPetName = snapshotString("petName").ifBlank { null },
            photoAttachmentUri = snapshotString("photoAttachmentUri").ifBlank { null },
            locationDisplay = snapshotString("locationDisplay").ifBlank { null },
            generalDetails = snapshotString("generalDetails").ifBlank { null },
            snapshotTimestamp = snapshotLong("timestamp")?.takeIf { it > 0L }
        )

    fun AppNotificationEntity.toDocument(createdAt: Any = FieldValue.serverTimestamp()): Map<String, Any?> {
        require(type in supportedNotificationTypes) { "Notification type is retired." }
        return buildMap {
            put("id", id)
            put("recipientId", recipientId)
            put("title", title)
            put("message", message)
            put("type", type)
            put("targetId", targetId)
            put("timestamp", timestamp)
            put("isRead", isRead)
            put("createdAt", createdAt)
            chatId?.let { put("chatId", it) }
            sightingId?.let { put("sightingId", it) }
            postId?.let { put("postId", it) }
        }
    }

    fun Map<String, Any?>.toNotificationEntity(documentId: String = string("id")): AppNotificationEntity =
        AppNotificationEntity(
            id = string("id").ifBlank { documentId },
            recipientId = string("recipientId"),
            title = string("title"),
            message = when (string("type")) {
                "ALERT" -> "Recibiste un nuevo avistamiento en tu publicacion."
                "CHAT" -> "Tienes un nuevo mensaje en una conversacion."
                else -> "Tienes una nueva notificacion."
            },
            type = string("type"),
            targetId = string("targetId"),
            timestamp = long("timestamp"),
            isRead = bool("isRead"),
            chatId = string("chatId").ifBlank { null },
            sightingId = string("sightingId").ifBlank { null },
            postId = string("postId").ifBlank { null }
        )

    private fun Map<String, Any?>.string(key: String): String = this[key] as? String ?: ""

    private fun Map<String, Any?>.bool(key: String): Boolean = this[key] as? Boolean ?: false

    private fun Map<String, Any?>.long(key: String): Long =
        when (val value = this[key]) {
            is Long -> value
            is Int -> value.toLong()
            is Double -> value.toLong()
            is Timestamp -> value.toDate().time
            else -> 0L
        }

    private fun Map<String, Any?>.double(key: String): Double =
        when (val value = this[key]) {
            is Double -> value
            is Float -> value.toDouble()
            is Long -> value.toDouble()
            is Int -> value.toDouble()
            else -> 0.0
        }

    @Suppress("UNCHECKED_CAST")
    private fun Map<String, Any?>.snapshot(): Map<String, Any?> =
        this["snapshot"] as? Map<String, Any?> ?: emptyMap()

    private fun Map<String, Any?>.snapshotString(key: String): String = snapshot()[key] as? String ?: ""

    private fun Map<String, Any?>.snapshotLong(key: String): Long? =
        when (val value = snapshot()[key]) {
            is Long -> value
            is Int -> value.toLong()
            is Double -> value.toLong()
            is Timestamp -> value.toDate().time
            else -> null
        }

    private val supportedNotificationTypes = setOf("ALERT", "CHAT")
}
