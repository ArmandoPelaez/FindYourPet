package com.findyourpet.app.data.repository

import android.content.Context
import android.net.Uri
import androidx.room.withTransaction
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.findyourpet.app.BuildConfig
import com.findyourpet.app.data.local.AppDatabase
import com.findyourpet.app.data.local.entity.AppNotificationEntity
import com.findyourpet.app.data.local.entity.ChatMessageEntity
import com.findyourpet.app.data.local.entity.ChatSessionEntity
import com.findyourpet.app.data.local.entity.PetPostEntity
import com.findyourpet.app.data.local.entity.SightingAlertEntity
import com.findyourpet.app.data.local.entity.SIGHTING_ALERT_MESSAGE_TYPE
import com.findyourpet.app.data.product.LocationSource
import com.findyourpet.app.data.product.MediaSource
import com.findyourpet.app.data.remote.BackendCollections
import com.findyourpet.app.data.remote.BackendSyncState
import com.findyourpet.app.data.remote.RemoteMappers.toChatMessageEntity
import com.findyourpet.app.data.remote.RemoteMappers.toChatSessionEntity
import com.findyourpet.app.data.remote.RemoteMappers.toDocument
import com.findyourpet.app.data.remote.RemoteMappers.toNotificationEntity
import com.findyourpet.app.data.remote.RemoteMappers.toPetPostEntity
import com.findyourpet.app.data.remote.RemoteMappers.toSightingEntity
import com.findyourpet.app.domain.OwnershipPolicy
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
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class PetRepository(context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val petDao = database.petDao()
    private val appContext = context.applicationContext
    private val firestore = configuredFirestore(appContext)
    private val cloudinaryReady = configuredCloudinary(appContext)

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

    fun getSightingsForOwner(ownerId: String): Flow<List<SightingAlertEntity>> =
        getSightingsForOwnerState(ownerId).map { it.data }

    fun getSightingsForOwnerState(ownerId: String): Flow<BackendSyncState<List<SightingAlertEntity>>> {
        require(ownerId.isNotBlank()) { "El propietario del avistamiento es obligatorio." }
        return firestore?.let { db ->
            observeQuery(
                query = db.collection(BackendCollections.SIGHTINGS)
                    .whereEqualTo("ownerId", ownerId)
                    .orderBy("timestamp", Query.Direction.DESCENDING),
                initialData = emptyList()
            ) { snapshot ->
                snapshot.documents
                    .mapNotNull { it.data?.toSightingEntity(it.id) }
                    .sortedByDescending { it.timestamp }
            }.onEach { state ->
                if (!state.hasError) {
                    petDao.clearSightingsForOwner(ownerId)
                    petDao.insertSightings(state.data)
                }
            }
        } ?: petDao.getSightingsForOwner(ownerId).toLocalState(emptyList())
    }

    fun getSightingById(sightingId: String): Flow<SightingAlertEntity?> =
        getSightingByIdState(sightingId).map { it.data }

    fun getSightingByIdState(sightingId: String): Flow<BackendSyncState<SightingAlertEntity?>> {
        require(sightingId.isNotBlank()) { "El identificador del avistamiento es obligatorio." }
        return firestore?.let { db ->
            observeDocument(
                document = db.collection(BackendCollections.SIGHTINGS).document(sightingId),
                initialData = null,
                required = true,
                validator = { snapshot ->
                    val data = snapshot.data
                    when {
                        data == null -> "El avistamiento no existe."
                        data["postId"] !is String || (data["postId"] as String).isBlank() ->
                            "El avistamiento no tiene una publicacion valida."
                        data["ownerId"] !is String || (data["ownerId"] as String).isBlank() ->
                            "El avistamiento no tiene un propietario valido."
                        data["reporterId"] !is String || (data["reporterId"] as String).isBlank() ->
                            "El avistamiento no tiene un reportante valido."
                        else -> null
                    }
                }
            ) { snapshot -> snapshot.data?.toSightingEntity(snapshot.id) }
                .onEach { state ->
                    state.data?.let { petDao.insertSighting(it) }
                }
        } ?: petDao.getSightingById(sightingId).toLocalState(null)
    }

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
                    .filter { it.type != RETIRED_CONTACT_NOTIFICATION_TYPE }
                    .sortedByDescending { it.timestamp }
            }.onEach { state ->
                if (!state.hasError) {
                    petDao.clearNotificationsNotForUser(userId)
                    petDao.insertNotifications(state.data)
                }
            }
        } ?: petDao.getAllNotifications()
            .map { notifications -> notifications.filter { it.type != RETIRED_CONTACT_NOTIFICATION_TYPE } }
            .toLocalState(emptyList())

    suspend fun insertPost(
        post: PetPostEntity,
        mediaSource: MediaSource = MediaSource.GALLERY,
        locationSource: LocationSource = LocationSource.MANUAL_COARSE
    ) {
        val db = firestore
        if (db == null) {
            petDao.insertPost(post)
            return
        }
        val uploaded = uploadImageForCloudinary(sourceUri = post.photoUri)
        val storedPost = post.copy(photoUri = uploaded.displayUrl)
        db.collection(BackendCollections.PET_POSTS)
            .document(post.id)
            .set(
                storedPost.toDocument(
                    mediaProvider = uploaded.provider,
                    mediaPublicId = uploaded.publicId,
                    mediaContentType = uploaded.contentType,
                    mediaSource = mediaSource.name,
                    locationSource = locationSource.name
                )
            )
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
        ownerId: String,
        mediaSource: MediaSource? = null,
        locationSource: LocationSource = LocationSource.MANUAL_COARSE,
        idempotencyKey: String? = null
    ): String {
        val db = firestore
        val timestamp = System.currentTimeMillis()
        val stableSubmissionKey = (idempotencyKey?.takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString())
            .replace(Regex("[^A-Za-z0-9_-]"), "_")
            .take(80)
        val sightingId = "sighting_$stableSubmissionKey"
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
        requireNotNull(derivedPost) { "La publicacion no existe." }
        val resolvedOwnerId = derivedPost.ownerId
        require(resolvedOwnerId.isNotBlank()) { "No se pudo identificar al dueno de la publicacion." }
        require(ownerId == resolvedOwnerId) { "El dueno no coincide con la publicacion." }
        require(reporterId.isNotBlank()) { "No se pudo identificar al reportero." }
        require(locationName.isNotBlank()) { "Indica donde viste la mascota." }
        require(notes.length <= 1000) { "Los detalles del avistamiento son demasiado extensos." }
        require(OwnershipPolicy.canReportSighting(reporterId, resolvedOwnerId)) {
            "No puedes reportar avistamientos de tu propia publicacion."
        }
        val uploadedPhoto = if (photoUri.isBlank() || db == null) {
            UploadedImage(displayUrl = photoUri, provider = "", publicId = "", contentType = "")
        } else {
            uploadImageForCloudinary(sourceUri = photoUri)
        }

        val sighting = SightingAlertEntity(
            id = sightingId,
            postId = postId,
            ownerId = resolvedOwnerId,
            reporterId = reporterId,
            reporterName = reporterName,
            photoUri = uploadedPhoto.displayUrl,
            locationName = locationName,
            latitude = latitude,
            longitude = longitude,
            notes = notes,
            timestamp = timestamp,
            idempotencyKey = stableSubmissionKey
        )
        val notification = AppNotificationEntity(
            id = "${sightingId}_notification",
            recipientId = resolvedOwnerId,
            title = "Nuevo avistamiento",
            message = "Recibiste un nuevo avistamiento en tu publicacion.",
            type = "ALERT",
            targetId = sightingId,
            timestamp = timestamp,
            sightingId = sightingId,
            postId = postId
        )

        if (db == null) {
            database.withTransaction {
                petDao.insertSighting(sighting)
                petDao.insertNotification(notification)
            }
        } else {
            db.runBatch { batch ->
                batch.set(
                    db.collection(BackendCollections.SIGHTINGS).document(sightingId),
                    sighting.toDocument(
                        ownerId = resolvedOwnerId,
                        mediaProvider = uploadedPhoto.provider,
                        mediaPublicId = uploadedPhoto.publicId,
                        mediaContentType = uploadedPhoto.contentType,
                        mediaSource = mediaSource?.name.orEmpty(),
                        locationSource = locationSource.name,
                        preciseLocationConsented = locationSource == LocationSource.DEVICE_GPS
                    )
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

        return sightingId
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
            isSystemMessage = false,
            type = "text"
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
            timestamp = timestamp,
            chatId = chatId,
            postId = postId
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
        petDao.clearNotifications()
    }

    suspend fun retainPrivateCacheForUser(userId: String) {
        petDao.clearChatSessionsNotForUser(userId)
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
                    ownerName = "Carlos Ramirez"
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
                    ownerName = "Maria Elena Gomez"
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
                    ownerName = "Andres Solis"
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
                lastMessageTimestamp = now - (10 * 3600000L)
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
                        text = "Nuevo avistamiento de Max",
                        photoUri = null,
                        timestamp = now - (12 * 3600000L),
                        isSystemMessage = false,
                        type = SIGHTING_ALERT_MESSAGE_TYPE,
                        sightingId = "sighting_1",
                        ownerId = "owner_1",
                        reporterId = "finder_1",
                        snapshotPetName = "Max",
                        photoAttachmentUri = "https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&w=600&q=80",
                        locationDisplay = "Frente a la cafeteria central, Calle 3",
                        generalDetails = "Vi a un perro parecido tomando agua junto a la entrada.",
                        snapshotTimestamp = now - (12 * 3600000L)
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
                    message = "Recibiste un nuevo avistamiento en tu publicacion.",
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
        required: Boolean = false,
        validator: ((DocumentSnapshot) -> String?)? = null,
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
                    val validationError = validator?.invoke(snapshot)
                    when {
                        required && !snapshot.exists() -> {
                            trySend(BackendSyncState.error(initialData, "El documento solicitado no existe."))
                        }
                        validationError != null -> {
                            trySend(
                                BackendSyncState.error(
                                    initialData,
                                    validationError
                                )
                            )
                        }
                        else -> {
                            trySend(
                                BackendSyncState.data(
                                    data = mapper(snapshot),
                                    isFromCache = snapshot.metadata.isFromCache,
                                    hasPendingWrites = snapshot.metadata.hasPendingWrites()
                                )
                            )
                        }
                    }
                }
            }
            awaitClose { registration.remove() }
        }

    private fun <T> Flow<T>.toLocalState(initialData: T): Flow<BackendSyncState<T>> =
        map { BackendSyncState.data(it, isFromCache = true, hasPendingWrites = false, isRemoteBackend = false) }
            .onStart { emit(BackendSyncState.loading(initialData, isRemoteBackend = false)) }
            .catch { emit(BackendSyncState.error(initialData, it.message ?: "Local cache read failed.", isRemoteBackend = false)) }

    private fun configuredFirestore(context: Context): FirebaseFirestore? =
        runCatching {
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            }
            if (FirebaseApp.getApps(context).isEmpty()) null else FirebaseFirestore.getInstance()
        }.getOrNull()

    private fun configuredCloudinary(context: Context): Boolean =
        runCatching {
            MediaManager.get()
            true
        }.recoverCatching {
            MediaManager.init(
                context,
                mapOf("cloud_name" to BuildConfig.CLOUDINARY_CLOUD_NAME)
            )
            true
        }.getOrDefault(false)

    private suspend fun uploadImageForCloudinary(sourceUri: String): UploadedImage {
        val uri = Uri.parse(sourceUri)
        val scheme = uri.scheme.orEmpty()
        if (scheme !in setOf("content", "file")) {
            return UploadedImage(displayUrl = sourceUri, provider = "", publicId = "", contentType = "")
        }
        require(cloudinaryReady) { "Cloudinary no esta configurado." }
        return suspendCancellableCoroutine { continuation ->
            MediaManager.get()
                .upload(uri)
                .unsigned(BuildConfig.CLOUDINARY_UPLOAD_PRESET)
                .option("resource_type", "image")
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) = Unit

                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) = Unit

                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val secureUrl = resultData["secure_url"] as? String
                        val publicId = resultData["public_id"] as? String
                        if (secureUrl.isNullOrBlank() || publicId.isNullOrBlank()) {
                            continuation.resumeWithException(
                                IllegalStateException("Cloudinary no devolvio la referencia de la imagen.")
                            )
                            return
                        }
                        val format = resultData["format"] as? String
                        continuation.resume(
                            UploadedImage(
                                displayUrl = secureUrl,
                                provider = "CLOUDINARY",
                                publicId = publicId,
                                contentType = format?.let { "image/$it" } ?: "image/jpeg"
                            )
                        )
                    }

                    override fun onError(requestId: String, error: ErrorInfo) {
                        continuation.resumeWithException(
                            IllegalStateException(error.description ?: "La subida de imagen fallo.")
                        )
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo) {
                        continuation.resumeWithException(
                            IllegalStateException(error.description ?: "La subida de imagen fue reprogramada.")
                        )
                    }
                })
                .dispatch()
        }
    }

    private data class UploadedImage(
        val displayUrl: String,
        val provider: String,
        val publicId: String,
        val contentType: String
    )

    private companion object {
        const val RETIRED_CONTACT_NOTIFICATION_TYPE = "CONTACT_SHARED"
    }
}
