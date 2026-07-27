package com.findyourpet.app.data.repository

import android.content.Context
import com.findyourpet.app.data.local.AppDatabase
import com.findyourpet.app.data.local.entity.AppNotificationEntity
import com.findyourpet.app.data.local.entity.ChatMessageEntity
import com.findyourpet.app.data.local.entity.ChatSessionEntity
import com.findyourpet.app.data.local.entity.ContactGrantEntity
import com.findyourpet.app.data.local.entity.PetPostEntity
import com.findyourpet.app.data.local.entity.SightingAlertEntity
import com.findyourpet.app.data.remote.BackendCollections
import com.findyourpet.app.data.remote.BackendSyncState
import com.findyourpet.app.data.remote.RemoteMappers.toChatMessageEntity
import com.findyourpet.app.data.remote.RemoteMappers.toChatSessionEntity
import com.findyourpet.app.data.remote.RemoteMappers.toContactGrantEntity
import com.findyourpet.app.data.remote.RemoteMappers.toDocument
import com.findyourpet.app.data.remote.RemoteMappers.toNotificationEntity
import com.findyourpet.app.data.remote.RemoteMappers.toPetPostEntity
import com.findyourpet.app.data.remote.RemoteMappers.toSightingEntity
import com.findyourpet.app.util.NotificationHelper
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.tasks.await
import java.util.UUID

class PetRepository(context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val petDao = database.petDao()
    private val appContext = context.applicationContext
    private val firestore = configuredFirestore(appContext)

    val usesRemoteBackend: Boolean = firestore != null

    val postFeedState: Flow<BackendSyncState<List<PetPostEntity>>> =
        firestore?.let { db ->
            observeQuery(
                query = db.collection(BackendCollections.PET_POSTS)
                    .orderBy("dateLost", Query.Direction.DESCENDING),
                initialData = emptyList()
            ) { snapshot ->
                snapshot.documents.mapNotNull { it.data?.toPetPostEntity(it.id) }
            }.onEach { state ->
                if (!state.hasError) {
                    petDao.clearPosts()
                    petDao.insertPosts(state.data)
                }
            }
        } ?: petDao.getAllPosts().toLocalState(emptyList())

    val allPosts: Flow<List<PetPostEntity>> = postFeedState.map { it.data }

    val allNotifications: Flow<List<AppNotificationEntity>> =
        petDao.getAllNotifications()

    fun getPostById(postId: String): Flow<PetPostEntity?> =
        getPostByIdState(postId).map { it.data }

    fun getPostByIdState(postId: String): Flow<BackendSyncState<PetPostEntity?>> =
        firestore?.let { db ->
            observeDocument(
                document = db.collection(BackendCollections.PET_POSTS).document(postId),
                initialData = null
            ) { snapshot ->
                snapshot.data?.toPetPostEntity(snapshot.id)
            }.onEach { state ->
                state.data?.let { petDao.insertPost(it) }
            }
        } ?: petDao.getPostById(postId).toLocalState(null)

    fun getPostsByOwner(ownerId: String): Flow<List<PetPostEntity>> =
        getPostsByOwnerState(ownerId).map { it.data }

    fun getPostsByOwnerState(ownerId: String): Flow<BackendSyncState<List<PetPostEntity>>> =
        firestore?.let { db ->
            observeQuery(
                query = db.collection(BackendCollections.PET_POSTS).whereEqualTo("ownerId", ownerId),
                initialData = emptyList()
            ) { snapshot ->
                snapshot.documents
                    .mapNotNull { it.data?.toPetPostEntity(it.id) }
                    .sortedByDescending { it.dateLost }
            }
        } ?: petDao.getPostsByOwner(ownerId).toLocalState(emptyList())

    fun getSightingsForPost(postId: String): Flow<List<SightingAlertEntity>> =
        getSightingsForPostState(postId).map { it.data }

    fun getSightingsForPostState(postId: String): Flow<BackendSyncState<List<SightingAlertEntity>>> =
        firestore?.let { db ->
            observeQuery(
                query = db.collection(BackendCollections.SIGHTINGS).whereEqualTo("postId", postId),
                initialData = emptyList()
            ) { snapshot ->
                snapshot.documents
                    .mapNotNull { it.data?.toSightingEntity(it.id) }
                    .sortedByDescending { it.timestamp }
            }.onEach { state ->
                if (!state.hasError) {
                    petDao.clearSightingsForPost(postId)
                    petDao.insertSightings(state.data)
                }
            }
        } ?: petDao.getSightingsForPost(postId).toLocalState(emptyList())

    fun getMessagesForChat(chatId: String): Flow<List<ChatMessageEntity>> =
        getMessagesForChatState(chatId).map { it.data }

    fun getMessagesForChatState(chatId: String): Flow<BackendSyncState<List<ChatMessageEntity>>> =
        firestore?.let { db ->
            observeQuery(
                query = db.collection(BackendCollections.CHAT_SESSIONS)
                    .document(chatId)
                    .collection(BackendCollections.MESSAGES)
                    .orderBy("timestamp", Query.Direction.ASCENDING),
                initialData = emptyList()
            ) { snapshot ->
                snapshot.documents.mapNotNull { it.data?.toChatMessageEntity(it.id) }
            }.onEach { state ->
                if (!state.hasError) {
                    petDao.clearMessagesForChat(chatId)
                    petDao.insertMessages(state.data)
                }
            }
        } ?: petDao.getMessagesForChat(chatId).toLocalState(emptyList())

    fun getChatSessionsForUser(userId: String): Flow<List<ChatSessionEntity>> =
        getChatSessionsForUserState(userId).map { it.data }

    fun getChatSessionsForUserState(userId: String): Flow<BackendSyncState<List<ChatSessionEntity>>> =
        firestore?.let { db ->
            observeQuery(
                query = db.collection(BackendCollections.CHAT_SESSIONS)
                    .whereArrayContains("participantIds", userId),
                initialData = emptyList()
            ) { snapshot ->
                snapshot.documents
                    .mapNotNull { it.data?.toChatSessionEntity(it.id) }
                    .sortedByDescending { it.lastMessageTimestamp }
            }.onEach { state ->
                if (!state.hasError) {
                    petDao.clearChatSessionsNotForUser(userId)
                    petDao.insertChatSessions(state.data)
                }
            }
        } ?: petDao.getChatSessionsForUser(userId).toLocalState(emptyList())

    fun getChatSessionById(chatId: String): Flow<ChatSessionEntity?> =
        getChatSessionByIdState(chatId).map { it.data }

    fun getChatSessionByIdState(chatId: String): Flow<BackendSyncState<ChatSessionEntity?>> =
        firestore?.let { db ->
            observeDocument(
                document = db.collection(BackendCollections.CHAT_SESSIONS).document(chatId),
                initialData = null
            ) { snapshot ->
                snapshot.data?.toChatSessionEntity(snapshot.id)
            }.onEach { state ->
                state.data?.let { petDao.insertChatSession(it) }
            }
        } ?: petDao.getChatSessionById(chatId).toLocalState(null)

    fun getActiveContactGrantForChat(chatId: String): Flow<ContactGrantEntity?> =
        getActiveContactGrantForChatState(chatId).map { it.data }

    fun getActiveContactGrantForChatState(chatId: String): Flow<BackendSyncState<ContactGrantEntity?>> =
        firestore?.let { db ->
            observeDocument(
                document = contactGrantRef(db, chatId),
                initialData = null
            ) { snapshot ->
                snapshot.data
                    ?.toContactGrantEntity(snapshot.id)
                    ?.takeIf { it.isActive }
            }.onEach { state ->
                if (!state.hasError) {
                    val grant = state.data
                    if (grant != null) {
                        petDao.insertContactGrant(grant)
                    } else {
                        petDao.clearContactGrantForChat(chatId)
                    }
                }
            }
        } ?: petDao.getActiveContactGrantForChat(chatId).toLocalState(null)

    fun getNotificationsForUser(userId: String): Flow<BackendSyncState<List<AppNotificationEntity>>> =
        firestore?.let { db ->
            observeQuery(
                query = db.collection(BackendCollections.USERS)
                    .document(userId)
                    .collection(BackendCollections.NOTIFICATIONS),
                initialData = emptyList()
            ) { snapshot ->
                snapshot.documents
                    .mapNotNull { it.data?.toNotificationEntity(it.id) }
                    .sortedByDescending { it.timestamp }
            }.onEach { state ->
                if (!state.hasError) {
                    petDao.clearNotificationsNotForUser(userId)
                    petDao.insertNotifications(state.data)
                }
            }
        } ?: petDao.getAllNotifications().toLocalState(emptyList())

    suspend fun insertPost(post: PetPostEntity) {
        val db = firestore
        if (db == null) {
            petDao.insertPost(post)
            return
        }
        db.collection(BackendCollections.PET_POSTS)
            .document(post.id)
            .set(post.toDocument())
            .await()
    }

    suspend fun updatePostStatus(postId: String, status: String) {
        val db = firestore
        if (db == null) {
            petDao.updatePostStatus(postId, status)
            return
        }
        db.collection(BackendCollections.PET_POSTS)
            .document(postId)
            .update(
                mapOf(
                    "status" to status,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
            .await()
    }

    suspend fun deletePost(postId: String) {
        val db = firestore
        if (db == null) return
        db.collection(BackendCollections.PET_POSTS).document(postId).delete().await()
    }

    suspend fun submitSightingAlert(
        postId: String,
        petName: String,
        reporterId: String,
        reporterName: String,
        photoUri: String,
        locationName: String,
        latitude: Double,
        longitude: Double,
        notes: String,
        ownerId: String
    ): String {
        val db = firestore
        val timestamp = System.currentTimeMillis()
        val sightingId = UUID.randomUUID().toString()
        val derivedPost = if (db != null) {
            requireNotNull(
                db.collection(BackendCollections.PET_POSTS)
                    .document(postId)
                    .get()
                    .await()
                    .toPetPostEntity()
            ) { "La publicacion no existe en el backend." }
        } else {
            petDao.getPostById(postId).first()
        }
        val resolvedOwnerId = derivedPost?.ownerId?.ifBlank { ownerId } ?: ownerId
        require(resolvedOwnerId.isNotBlank()) { "No se pudo identificar al dueno de la publicacion." }

        val sighting = SightingAlertEntity(
            id = sightingId,
            postId = postId,
            ownerId = resolvedOwnerId,
            reporterId = reporterId,
            reporterName = reporterName,
            photoUri = photoUri,
            locationName = locationName,
            latitude = latitude,
            longitude = longitude,
            notes = notes,
            timestamp = timestamp
        )
        val chatId = BackendCollections.chatSessionId(postId, reporterId)
        val chatSession = ChatSessionEntity(
            id = chatId,
            postId = postId,
            petName = petName,
            petPhotoUri = photoUri,
            ownerId = resolvedOwnerId,
            reporterId = reporterId,
            reporterName = reporterName,
            lastMessage = "Nuevo avistamiento reportado",
            lastMessageTimestamp = timestamp,
            isContactSharedByOwner = false
        )
        val systemMsg = ChatMessageEntity(
            id = UUID.randomUUID().toString(),
            chatId = chatId,
            postId = postId,
            senderId = reporterId,
            senderName = reporterName,
            text = "ALERTA DE AVISTAMIENTO\nUbicacion: $locationName\nNota: $notes",
            photoUri = photoUri,
            timestamp = timestamp,
            isSystemMessage = true
        )
        val notification = AppNotificationEntity(
            id = UUID.randomUUID().toString(),
            recipientId = resolvedOwnerId,
            title = "Avistamiento recibido",
            message = "$reporterName reporto un posible avistamiento de $petName.",
            type = "ALERT",
            targetId = chatId,
            timestamp = timestamp
        )

        if (db == null) {
            petDao.insertSighting(sighting)
            petDao.insertChatSession(chatSession)
            petDao.insertMessage(systemMsg)
            petDao.insertNotification(notification)
        } else {
            val chatRef = db.collection(BackendCollections.CHAT_SESSIONS).document(chatId)
            db.runBatch { batch ->
                batch.set(
                    db.collection(BackendCollections.SIGHTINGS).document(sightingId),
                    sighting.toDocument(resolvedOwnerId)
                )
                batch.set(chatRef, chatSession.toDocument(), com.google.firebase.firestore.SetOptions.merge())
                batch.set(
                    chatRef.collection(BackendCollections.MESSAGES).document(systemMsg.id),
                    systemMsg.toDocument()
                )
                batch.set(
                    db.collection(BackendCollections.USERS)
                        .document(resolvedOwnerId)
                        .collection(BackendCollections.NOTIFICATIONS)
                        .document(notification.id),
                    notification.toDocument()
                )
            }.await()
        }

        NotificationHelper.showNotification(
            appContext,
            (timestamp % 10000).toInt(),
            notification.title,
            notification.message
        )

        return chatId
    }

    suspend fun sendChatMessage(
        chatId: String,
        postId: String,
        senderId: String,
        senderName: String,
        text: String,
        photoUri: String? = null
    ) {
        val db = firestore
        val timestamp = System.currentTimeMillis()
        val msg = ChatMessageEntity(
            id = UUID.randomUUID().toString(),
            chatId = chatId,
            postId = postId,
            senderId = senderId,
            senderName = senderName,
            text = text,
            photoUri = photoUri,
            timestamp = timestamp,
            isSystemMessage = false
        )

        if (db == null) {
            petDao.insertMessage(msg)
            petDao.updateChatLastMessage(chatId, "Nuevo mensaje en el chat", timestamp)
            return
        }

        val chatRef = db.collection(BackendCollections.CHAT_SESSIONS).document(chatId)
        val session = requireNotNull(
            chatRef.get().await().data?.toChatSessionEntity(chatId)
        ) { "La conversacion no existe." }
        require(senderId == session.ownerId || senderId == session.reporterId) {
            "Solo participantes pueden enviar mensajes."
        }
        val recipientId = if (senderId == session.ownerId) session.reporterId else session.ownerId
        val notification = AppNotificationEntity(
            id = UUID.randomUUID().toString(),
            recipientId = recipientId,
            title = "Nuevo mensaje",
            message = "Tienes un nuevo mensaje en una conversacion.",
            type = "CHAT",
            targetId = chatId,
            timestamp = timestamp
        )

        db.runBatch { batch ->
            batch.set(
                chatRef.collection(BackendCollections.MESSAGES).document(msg.id),
                msg.toDocument()
            )
            batch.update(
                chatRef,
                mapOf(
                    "lastMessage" to "Nuevo mensaje en el chat",
                    "lastMessageTimestamp" to timestamp,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
            batch.set(
                db.collection(BackendCollections.USERS)
                    .document(recipientId)
                    .collection(BackendCollections.NOTIFICATIONS)
                    .document(notification.id),
                notification.toDocument()
            )
        }.await()
    }

    suspend fun toggleChatContactSharing(
        chatId: String,
        isShared: Boolean,
        ownerId: String,
        ownerName: String,
        phone: String,
        email: String
    ) {
        val db = firestore
        val timestamp = System.currentTimeMillis()

        if (db == null) {
            val session = requireNotNull(petDao.getChatSessionById(chatId).first()) {
                "La conversacion no existe."
            }
            require(ownerId == session.ownerId) {
                "Solo el dueno puede compartir o revocar contacto."
            }
            petDao.updateChatContactShared(chatId, isShared)
            if (isShared) {
                petDao.insertContactGrant(
                    ContactGrantEntity(
                        id = BackendCollections.OWNER_CONTACT_GRANT,
                        chatId = chatId,
                        postId = session.postId,
                        ownerId = session.ownerId,
                        reporterId = session.reporterId,
                        sharedBy = ownerId,
                        sharedAt = timestamp,
                        revokedAt = null,
                        isActive = true,
                        ownerName = ownerName,
                        ownerPhone = phone,
                        ownerEmail = email
                    )
                )
            } else {
                petDao.clearContactGrantForChat(chatId)
            }
            val localText = if (isShared) {
                "$ownerName habilito el contacto dentro de esta conversacion."
            } else {
                "$ownerName oculto sus datos de contacto."
            }
            petDao.insertMessage(
                ChatMessageEntity(
                    id = UUID.randomUUID().toString(),
                    chatId = chatId,
                    postId = session.postId,
                    senderId = session.ownerId,
                    senderName = ownerName,
                    text = localText,
                    photoUri = null,
                    timestamp = timestamp,
                    isSystemMessage = true
                )
            )
            petDao.updateChatLastMessage(chatId, "Contacto actualizado en este chat", timestamp)
            petDao.insertNotification(
                AppNotificationEntity(
                    id = UUID.randomUUID().toString(),
                    recipientId = session.reporterId,
                    title = "Contacto actualizado",
                    message = if (isShared) {
                        "El dueno habilito contacto dentro de la conversacion."
                    } else {
                        "El dueno actualizo la disponibilidad de contacto."
                    },
                    type = "CONTACT_SHARED",
                    targetId = chatId,
                    timestamp = timestamp
                )
            )
            return
        }

        val chatRef = db.collection(BackendCollections.CHAT_SESSIONS).document(chatId)
        val session = requireNotNull(
            chatRef.get().await().data?.toChatSessionEntity(chatId)
        ) { "La conversacion no existe." }
        require(ownerId == session.ownerId) {
            "Solo el dueno puede compartir o revocar contacto."
        }

        val text = if (isShared) {
            "$ownerName habilito el contacto dentro de esta conversacion."
        } else {
            "$ownerName oculto sus datos de contacto."
        }
        val systemMsg = ChatMessageEntity(
            id = UUID.randomUUID().toString(),
            chatId = chatId,
            postId = session.postId,
            senderId = session.ownerId,
            senderName = ownerName,
            text = text,
            photoUri = null,
            timestamp = timestamp,
            isSystemMessage = true
        )
        val notification = AppNotificationEntity(
            id = UUID.randomUUID().toString(),
            recipientId = session.reporterId,
            title = "Contacto actualizado",
            message = if (isShared) {
                "El dueno habilito contacto dentro de la conversacion."
            } else {
                "El dueno actualizo la disponibilidad de contacto."
            },
            type = "CONTACT_SHARED",
            targetId = chatId,
            timestamp = timestamp
        )
        val grant = ContactGrantEntity(
            id = BackendCollections.OWNER_CONTACT_GRANT,
            chatId = chatId,
            postId = session.postId,
            ownerId = session.ownerId,
            reporterId = session.reporterId,
            sharedBy = ownerId,
            sharedAt = timestamp,
            revokedAt = null,
            isActive = true,
            ownerName = ownerName,
            ownerPhone = phone,
            ownerEmail = email
        )
        val grantRef = contactGrantRef(db, chatId)

        db.runBatch { batch ->
            batch.update(
                chatRef,
                mapOf(
                    "isContactSharedByOwner" to isShared,
                    "lastMessage" to "Contacto actualizado en este chat",
                    "lastMessageTimestamp" to timestamp,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
            if (isShared) {
                batch.set(grantRef, grant.toDocument(), com.google.firebase.firestore.SetOptions.merge())
            } else {
                batch.delete(grantRef)
            }
            batch.set(
                chatRef.collection(BackendCollections.MESSAGES).document(systemMsg.id),
                systemMsg.toDocument()
            )
            batch.set(
                db.collection(BackendCollections.USERS)
                    .document(session.reporterId)
                    .collection(BackendCollections.NOTIFICATIONS)
                    .document(notification.id),
                notification.toDocument()
            )
        }.await()
        if (!isShared) {
            petDao.clearContactGrantForChat(chatId)
        }
    }

    suspend fun markNotificationAsRead(userId: String, id: String) {
        val db = firestore
        if (db != null) {
            db.collection(BackendCollections.USERS)
                .document(userId)
                .collection(BackendCollections.NOTIFICATIONS)
                .document(id)
                .update("isRead", true)
                .await()
        }
        petDao.markNotificationAsRead(id)
    }

    suspend fun clearPrivateCache() {
        petDao.clearSightings()
        petDao.clearMessages()
        petDao.clearChatSessions()
        petDao.clearContactGrants()
        petDao.clearNotifications()
    }

    suspend fun retainPrivateCacheForUser(userId: String) {
        petDao.clearChatSessionsNotForUser(userId)
        petDao.clearContactGrantsNotForUser(userId)
        petDao.clearNotificationsNotForUser(userId)
    }

    suspend fun seedInitialDataIfNeeded() {
        if (usesRemoteBackend) return

        val existingPosts = petDao.getAllPosts().first()
        if (existingPosts.isEmpty()) {
            val now = System.currentTimeMillis()
            val dayMillis = 86400000L

            val posts = listOf(
                PetPostEntity(
                    id = "post_1",
                    petName = "Max",
                    species = "Perro",
                    breed = "Golden Retriever",
                    color = "Dorado claro",
                    features = "Mancha blanca circular en el pecho. Lleva collar rojo con placa en forma de hueso. Responde al nombre de Max.",
                    status = "PERDIDO",
                    photoUri = "https://images.unsplash.com/photo-1552053831-71594a27632d?auto=format&fit=crop&w=600&q=80",
                    dateLost = now - (2 * dayMillis),
                    lastSeenLocation = "Parque Central, San Jose",
                    latitude = 9.9333,
                    longitude = -84.0833,
                    rewardAmount = "$200 USD",
                    ownerId = "owner_1",
                    ownerName = "Carlos Ramirez",
                    ownerPhone = "+506 8888-9900",
                    ownerEmail = "carlos.ramirez@email.com",
                    ownerAddress = "Calle 5, San Jose"
                ),
                PetPostEntity(
                    id = "post_2",
                    petName = "Luna",
                    species = "Gato",
                    breed = "Siames",
                    color = "Crema y marron oscuro",
                    features = "Ojos azul intenso, orejas y cola oscuras. Algo asustadiza pero carinosa con la comida.",
                    status = "PERDIDO",
                    photoUri = "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=600&q=80",
                    dateLost = now - dayMillis,
                    lastSeenLocation = "Colonia Miraflores, San Pedro",
                    latitude = 9.9350,
                    longitude = -84.0500,
                    rewardAmount = "$150 USD",
                    ownerId = "owner_2",
                    ownerName = "Maria Elena Gomez",
                    ownerPhone = "+506 7011-2233",
                    ownerEmail = "maria.gomez@email.com",
                    ownerAddress = "Av 4, San Pedro"
                ),
                PetPostEntity(
                    id = "post_3",
                    petName = "Rocky",
                    species = "Perro",
                    breed = "Beagle",
                    color = "Tricolor",
                    features = "Orejas largas caidas, cola con punta blanca. Portaba arnes azul reflejante.",
                    status = "AVISTADO",
                    photoUri = "https://images.unsplash.com/photo-1537151608828-ea2b11777ee8?auto=format&fit=crop&w=600&q=80",
                    dateLost = now - (3 * dayMillis),
                    lastSeenLocation = "Cerca del supermercado, Curridabat",
                    latitude = 9.9167,
                    longitude = -84.0333,
                    rewardAmount = "Sin recompensa",
                    ownerId = "owner_3",
                    ownerName = "Andres Solis",
                    ownerPhone = "+506 8322-1100",
                    ownerEmail = "andres.solis@email.com",
                    ownerAddress = "Barrio Pinto, Curridabat"
                )
            )

            petDao.insertPosts(posts)

            val alert = SightingAlertEntity(
                id = "sighting_1",
                postId = "post_1",
                ownerId = "owner_1",
                reporterId = "finder_1",
                reporterName = "Sofia Vargas",
                photoUri = "https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&w=600&q=80",
                locationName = "Frente a la cafeteria central, Calle 3",
                latitude = 9.9340,
                longitude = -84.0820,
                notes = "Vi a un perro parecido tomando agua junto a la entrada.",
                timestamp = now - (12 * 3600000L)
            )
            petDao.insertSighting(alert)

            val chatId = BackendCollections.chatSessionId("post_1", "finder_1")
            val chatSession = ChatSessionEntity(
                id = chatId,
                postId = "post_1",
                petName = "Max",
                petPhotoUri = "https://images.unsplash.com/photo-1552053831-71594a27632d?auto=format&fit=crop&w=600&q=80",
                ownerId = "owner_1",
                reporterId = "finder_1",
                reporterName = "Sofia Vargas",
                lastMessage = "Nuevo mensaje en el chat",
                lastMessageTimestamp = now - (10 * 3600000L),
                isContactSharedByOwner = false
            )
            petDao.insertChatSession(chatSession)

            petDao.insertMessages(
                listOf(
                    ChatMessageEntity(
                        id = "msg_1",
                        chatId = chatId,
                        postId = "post_1",
                        senderId = "finder_1",
                        senderName = "Sofia Vargas",
                        text = "ALERTA DE AVISTAMIENTO\nFrente a la cafeteria central\nVi un Golden Retriever muy parecido con collar rojo.",
                        photoUri = "https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&w=600&q=80",
                        timestamp = now - (12 * 3600000L),
                        isSystemMessage = true
                    ),
                    ChatMessageEntity(
                        id = "msg_2",
                        chatId = chatId,
                        postId = "post_1",
                        senderId = "finder_1",
                        senderName = "Sofia Vargas",
                        text = "Hola Carlos. Acabo de enviar la foto. Crees que sea tu perrito?",
                        photoUri = null,
                        timestamp = now - (10 * 3600000L),
                        isSystemMessage = false
                    )
                )
            )

            petDao.insertNotification(
                AppNotificationEntity(
                    id = "notif_1",
                    recipientId = "owner_1",
                    title = "Avistamiento recibido",
                    message = "Sofia Vargas reporto un posible avistamiento de Max.",
                    type = "ALERT",
                    targetId = chatId,
                    timestamp = now - (12 * 3600000L),
                    isRead = false
                )
            )
        }
    }

    private fun <T> observeQuery(
        query: Query,
        initialData: T,
        mapper: (QuerySnapshot) -> T
    ): Flow<BackendSyncState<T>> =
        callbackFlow {
            trySend(BackendSyncState.loading(initialData))
            val registration = query.addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                if (error != null) {
                    trySend(BackendSyncState.error(initialData, error.message ?: "Backend read failed."))
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    trySend(
                        BackendSyncState.data(
                            data = mapper(snapshot),
                            isFromCache = snapshot.metadata.isFromCache,
                            hasPendingWrites = snapshot.metadata.hasPendingWrites()
                        )
                    )
                }
            }
            awaitClose { registration.remove() }
        }

    private fun <T> observeDocument(
        document: com.google.firebase.firestore.DocumentReference,
        initialData: T,
        mapper: (DocumentSnapshot) -> T
    ): Flow<BackendSyncState<T>> =
        callbackFlow {
            trySend(BackendSyncState.loading(initialData))
            val registration = document.addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                if (error != null) {
                    trySend(BackendSyncState.error(initialData, error.message ?: "Backend read failed."))
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    trySend(
                        BackendSyncState.data(
                            data = mapper(snapshot),
                            isFromCache = snapshot.metadata.isFromCache,
                            hasPendingWrites = snapshot.metadata.hasPendingWrites()
                        )
                    )
                }
            }
            awaitClose { registration.remove() }
        }

    private fun <T> Flow<T>.toLocalState(initialData: T): Flow<BackendSyncState<T>> =
        map { BackendSyncState.data(it, isFromCache = true, hasPendingWrites = false, isRemoteBackend = false) }
            .onStart { emit(BackendSyncState.loading(initialData, isRemoteBackend = false)) }
            .catch { emit(BackendSyncState.error(initialData, it.message ?: "Local cache read failed.", isRemoteBackend = false)) }

    private fun contactGrantRef(db: FirebaseFirestore, chatId: String) =
        db.collection(BackendCollections.CHAT_SESSIONS)
            .document(chatId)
            .collection(BackendCollections.CONTACT_GRANTS)
            .document(BackendCollections.OWNER_CONTACT_GRANT)

    private fun configuredFirestore(context: Context): FirebaseFirestore? =
        runCatching {
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            }
            if (FirebaseApp.getApps(context).isEmpty()) null else FirebaseFirestore.getInstance()
        }.getOrNull()
}
