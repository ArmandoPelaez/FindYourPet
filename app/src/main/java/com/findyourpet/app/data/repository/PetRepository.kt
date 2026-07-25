package com.findyourpet.app.data.repository

import android.content.Context
import com.findyourpet.app.data.local.AppDatabase
import com.findyourpet.app.data.local.entity.AppNotificationEntity
import com.findyourpet.app.data.local.entity.ChatMessageEntity
import com.findyourpet.app.data.local.entity.ChatSessionEntity
import com.findyourpet.app.data.local.entity.PetPostEntity
import com.findyourpet.app.data.local.entity.SightingAlertEntity
import com.findyourpet.app.util.NotificationHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.UUID

class PetRepository(context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val petDao = database.petDao()
    private val appContext = context.applicationContext

    val allPosts: Flow<List<PetPostEntity>> = petDao.getAllPosts()
    val allNotifications: Flow<List<AppNotificationEntity>> = petDao.getAllNotifications()

    fun getPostById(postId: String): Flow<PetPostEntity?> = petDao.getPostById(postId)

    fun getPostsByOwner(ownerId: String): Flow<List<PetPostEntity>> = petDao.getPostsByOwner(ownerId)

    fun getSightingsForPost(postId: String): Flow<List<SightingAlertEntity>> = petDao.getSightingsForPost(postId)

    fun getMessagesForChat(chatId: String): Flow<List<ChatMessageEntity>> = petDao.getMessagesForChat(chatId)

    fun getChatSessionsForUser(userId: String): Flow<List<ChatSessionEntity>> = petDao.getChatSessionsForUser(userId)

    fun getChatSessionById(chatId: String): Flow<ChatSessionEntity?> = petDao.getChatSessionById(chatId)

    suspend fun insertPost(post: PetPostEntity) {
        petDao.insertPost(post)
    }

    suspend fun updatePostStatus(postId: String, status: String) {
        petDao.updatePostStatus(postId, status)
    }

    suspend fun updatePostContactRevealed(postId: String, isRevealed: Boolean) {
        petDao.updatePostContactRevealed(postId, isRevealed)
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
        val sightingId = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis()

        val sighting = SightingAlertEntity(
            id = sightingId,
            postId = postId,
            reporterId = reporterId,
            reporterName = reporterName,
            photoUri = photoUri,
            locationName = locationName,
            latitude = latitude,
            longitude = longitude,
            notes = notes,
            timestamp = timestamp
        )
        petDao.insertSighting(sighting)

        // Demo flow: create or update a local chat session.
        val chatId = "${postId}_$reporterId"
        val existingSession = petDao.getChatSessionById(chatId).first()

        val chatSession = ChatSessionEntity(
            id = chatId,
            postId = postId,
            petName = petName,
            petPhotoUri = photoUri,
            ownerId = ownerId,
            reporterId = reporterId,
            reporterName = reporterName,
            lastMessage = "🚨 ALERTA: Nuevo avistamiento reportado en $locationName",
            lastMessageTimestamp = timestamp,
            isContactSharedByOwner = existingSession?.isContactSharedByOwner ?: false
        )
        petDao.insertChatSession(chatSession)

        // Demo system message inside the local chat.
        val systemMsg = ChatMessageEntity(
            id = UUID.randomUUID().toString(),
            chatId = chatId,
            postId = postId,
            senderId = reporterId,
            senderName = reporterName,
            text = "ALERTA DE AVISTAMIENTO\nUbicación: $locationName ($latitude, $longitude)\nNota: $notes",
            photoUri = photoUri,
            timestamp = timestamp,
            isSystemMessage = true
        )
        petDao.insertMessage(systemMsg)

        // Local demo notification.
        val notifTitle = "Alerta de avistamiento para $petName"
        val notifMsg = "$reporterName cree haber visto a $petName cerca de $locationName. Revisa la foto y ubicación."

        val notification = AppNotificationEntity(
            id = UUID.randomUUID().toString(),
            title = notifTitle,
            message = notifMsg,
            type = "ALERT",
            targetId = chatId,
            timestamp = timestamp
        )
        petDao.insertNotification(notification)

        NotificationHelper.showNotification(appContext, (timestamp % 10000).toInt(), notifTitle, notifMsg)

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
        petDao.insertMessage(msg)
        petDao.updateChatLastMessage(chatId, text, timestamp)

        // Notify counterpart
        val notifTitle = "💬 Mensaje de $senderName"
        val notification = AppNotificationEntity(
            id = UUID.randomUUID().toString(),
            title = notifTitle,
            message = text,
            type = "CHAT",
            targetId = chatId,
            timestamp = timestamp
        )
        petDao.insertNotification(notification)
        NotificationHelper.showNotification(appContext, (timestamp % 10000).toInt(), notifTitle, text)
    }

    suspend fun toggleChatContactSharing(chatId: String, isShared: Boolean, ownerName: String, phone: String, email: String) {
        petDao.updateChatContactShared(chatId, isShared)
        val timestamp = System.currentTimeMillis()

        val text = if (isShared) {
            "$ownerName autorizó mostrar sus datos de contacto:\nTeléfono: $phone\nEmail: $email"
        } else {
            "$ownerName ocultó sus datos de contacto."
        }

        val session = petDao.getChatSessionById(chatId).first()
        if (session != null) {
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
            petDao.insertMessage(systemMsg)

            if (isShared) {
                val notifTitle = "Contacto compartido"
                val notifMsg = "$ownerName te ha compartido sus datos de contacto directos."
                val notification = AppNotificationEntity(
                    id = UUID.randomUUID().toString(),
                    title = notifTitle,
                    message = notifMsg,
                    type = "CONTACT_SHARED",
                    targetId = chatId,
                    timestamp = timestamp
                )
                petDao.insertNotification(notification)
                NotificationHelper.showNotification(appContext, (timestamp % 10000).toInt(), notifTitle, notifMsg)
            }
        }
    }

    suspend fun markNotificationAsRead(id: String) {
        petDao.markNotificationAsRead(id)
    }

    suspend fun seedInitialDataIfNeeded() {
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
                    lastSeenLocation = "Parque Central, San José",
                    latitude = 9.9333,
                    longitude = -84.0833,
                    rewardAmount = "$200 USD",
                    ownerId = "owner_1",
                    ownerName = "Carlos Ramírez",
                    ownerPhone = "+506 8888-9900",
                    ownerEmail = "carlos.ramirez@email.com",
                    ownerAddress = "Calle 5, San José",
                    isContactRevealedToAll = false
                ),
                PetPostEntity(
                    id = "post_2",
                    petName = "Luna",
                    species = "Gato",
                    breed = "Siamés",
                    color = "Crema y marrón oscuro",
                    features = "Ojos azul intenso, orejas y cola oscuras. Algo asustadiza pero cariñosa con la comida.",
                    status = "PERDIDO",
                    photoUri = "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=600&q=80",
                    dateLost = now - (1 * dayMillis),
                    lastSeenLocation = "Colonia Miraflores, San Pedro",
                    latitude = 9.9350,
                    longitude = -84.0500,
                    rewardAmount = "$150 USD",
                    ownerId = "owner_2",
                    ownerName = "María Elena Gómez",
                    ownerPhone = "+506 7011-2233",
                    ownerEmail = "maria.gomez@email.com",
                    ownerAddress = "Av 4, San Pedro",
                    isContactRevealedToAll = false
                ),
                PetPostEntity(
                    id = "post_3",
                    petName = "Rocky",
                    species = "Perro",
                    breed = "Beagle",
                    color = "Tricolor (Blanco, Marrón, Negro)",
                    features = "Orejas largas caídas, cola con punta blanca. Portaba arnés azul reflejante.",
                    status = "AVISTADO",
                    photoUri = "https://images.unsplash.com/photo-1537151608828-ea2b11777ee8?auto=format&fit=crop&w=600&q=80",
                    dateLost = now - (3 * dayMillis),
                    lastSeenLocation = "Cerca del Supermercado Másxmenos, Curridabat",
                    latitude = 9.9167,
                    longitude = -84.0333,
                    rewardAmount = "Sin recompensa",
                    ownerId = "owner_3",
                    ownerName = "Andrés Solís",
                    ownerPhone = "+506 8322-1100",
                    ownerEmail = "andres.solis@email.com",
                    ownerAddress = "Barrio Pinto, Curridabat",
                    isContactRevealedToAll = false
                ),
                PetPostEntity(
                    id = "post_4",
                    petName = "Coco",
                    species = "Ave",
                    breed = "Ninfa / Carolinas",
                    color = "Gris con mejillas anaranjadas",
                    features = "Copete amarillo muy vistoso. Sabe silbar la melodía de los Simpsons.",
                    status = "REUNIDO",
                    photoUri = "https://images.unsplash.com/photo-1522858547137-f1dcec554f55?auto=format&fit=crop&w=600&q=80",
                    dateLost = now - (5 * dayMillis),
                    lastSeenLocation = "Barrio Escalante",
                    latitude = 9.9380,
                    longitude = -84.0620,
                    rewardAmount = "Reunido con su familia",
                    ownerId = "owner_4",
                    ownerName = "Lucía Fernández",
                    ownerPhone = "+506 8765-4321",
                    ownerEmail = "lucia.f@email.com",
                    ownerAddress = "Escalante",
                    isContactRevealedToAll = false
                )
            )

            posts.forEach { petDao.insertPost(it) }

            // Demo seed: initial sighting alert for Max.
            val alertId = "sighting_1"
            val alert = SightingAlertEntity(
                id = alertId,
                postId = "post_1",
                reporterId = "finder_1",
                reporterName = "Sofía Vargas (Vecina)",
                photoUri = "https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&w=600&q=80",
                locationName = "Frente a la Cafetería Central, Calle 3",
                latitude = 9.9340,
                longitude = -84.0820,
                notes = "Vi a un perro idéntico a Max tomando agua junto a la entrada. Parece algo desorientado pero en buen estado.",
                timestamp = now - (12 * 3600000L)
            )
            petDao.insertSighting(alert)

            // Demo seed: initial local chat session.
            val chatId = "post_1_finder_1"
            val chatSession = ChatSessionEntity(
                id = chatId,
                postId = "post_1",
                petName = "Max",
                petPhotoUri = "https://images.unsplash.com/photo-1552053831-71594a27632d?auto=format&fit=crop&w=600&q=80",
                ownerId = "owner_1",
                reporterId = "finder_1",
                reporterName = "Sofía Vargas (Vecina)",
                lastMessage = "¡Hola Carlos! Acabo de enviar la foto de Max cerca de la cafetería.",
                lastMessageTimestamp = now - (10 * 3600000L),
                isContactSharedByOwner = false
            )
            petDao.insertChatSession(chatSession)

            // Demo seed: messages inside the local chat.
            val msg1 = ChatMessageEntity(
                id = "msg_1",
                chatId = chatId,
                postId = "post_1",
                senderId = "finder_1",
                senderName = "Sofía Vargas",
                text = "ALERTA DE AVISTAMIENTO\nFrente a la Cafetería Central\nVi un Golden Retriever muy parecido con collar rojo.",
                photoUri = "https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&w=600&q=80",
                timestamp = now - (12 * 3600000L),
                isSystemMessage = true
            )
            val msg2 = ChatMessageEntity(
                id = "msg_2",
                chatId = chatId,
                postId = "post_1",
                senderId = "finder_1",
                senderName = "Sofía Vargas",
                text = "¡Hola Carlos! Acabo de enviar la foto. ¿Crees que sea tu perrito?",
                photoUri = null,
                timestamp = now - (10 * 3600000L),
                isSystemMessage = false
            )
            val msg3 = ChatMessageEntity(
                id = "msg_3",
                chatId = chatId,
                postId = "post_1",
                senderId = "owner_1",
                senderName = "Carlos Ramírez",
                text = "¡Hola Sofía! ¡Sí, se parece muchísimo! Muchísimas gracias por avisar. Voy en camino.",
                photoUri = null,
                timestamp = now - (8 * 3600000L),
                isSystemMessage = false
            )

            petDao.insertMessage(msg1)
            petDao.insertMessage(msg2)
            petDao.insertMessage(msg3)

            // Demo seed: initial local notification.
            val notif = AppNotificationEntity(
                id = "notif_1",
                title = "Alerta de avistamiento para Max",
                message = "Sofía Vargas cree haber visto a Max cerca de Frente a la Cafetería Central.",
                type = "ALERT",
                targetId = chatId,
                timestamp = now - (12 * 3600000L),
                isRead = false
            )
            petDao.insertNotification(notif)
        }
    }
}
