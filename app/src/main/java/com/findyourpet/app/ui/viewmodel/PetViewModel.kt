package com.findyourpet.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.findyourpet.app.data.local.entity.AppNotificationEntity
import com.findyourpet.app.data.local.entity.ChatMessageEntity
import com.findyourpet.app.data.local.entity.ChatSessionEntity
import com.findyourpet.app.data.local.entity.PetPostEntity
import com.findyourpet.app.data.local.entity.SightingAlertEntity
import com.findyourpet.app.data.repository.PetRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

data class UserProfile(
    val id: String,
    val name: String,
    val phone: String,
    val email: String
)

class PetViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PetRepository(application)

    // Current User Profile State
    val currentUser = MutableStateFlow(
        UserProfile(
            id = "user_1",
            name = "Carlos Ramírez",
            phone = "+506 8888-9900",
            email = "carlos.ramirez@email.com"
        )
    )

    fun switchUserRole(isOwnerRole: Boolean) {
        // Retained for backward compatibility
    }

    // Search and Filters
    val searchQuery = MutableStateFlow("")
    val selectedSpecies = MutableStateFlow("Todos") // Todos, Perro, Gato, Ave, Otro
    val selectedStatusFilter = MutableStateFlow("Todos") // Todos, PERDIDO, AVISTADO, REUNIDO

    val allPosts: StateFlow<List<PetPostEntity>> = repository.allPosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredPosts: StateFlow<List<PetPostEntity>> = combine(
        allPosts,
        searchQuery,
        selectedSpecies,
        selectedStatusFilter
    ) { posts, query, species, status ->
        posts.filter { post ->
            val matchesQuery = query.isBlank() ||
                    post.petName.contains(query, ignoreCase = true) ||
                    post.breed.contains(query, ignoreCase = true) ||
                    post.lastSeenLocation.contains(query, ignoreCase = true) ||
                    post.features.contains(query, ignoreCase = true)

            val matchesSpecies = species == "Todos" || post.species.equals(species, ignoreCase = true)
            val matchesStatus = status == "Todos" || post.status.equals(status, ignoreCase = true)

            matchesQuery && matchesSpecies && matchesStatus
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Post State
    val selectedPostId = MutableStateFlow<String?>("post_1")

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedPost: StateFlow<PetPostEntity?> = selectedPostId.flatMapLatest { id ->
        if (id == null) flowOf(null) else repository.getPostById(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedPostSightings: StateFlow<List<SightingAlertEntity>> = selectedPostId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.getSightingsForPost(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Chat State
    val activeChatId = MutableStateFlow<String?>("post_1_finder_1")

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeChatMessages: StateFlow<List<ChatMessageEntity>> = activeChatId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.getMessagesForChat(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeChatSession: StateFlow<ChatSessionEntity?> = activeChatId.flatMapLatest { id ->
        if (id == null) flowOf(null) else repository.getChatSessionById(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val userChatSessions: StateFlow<List<ChatSessionEntity>> = currentUser.flatMapLatest { user ->
        repository.getChatSessionsForUser(user.id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotifications: StateFlow<List<AppNotificationEntity>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfNeeded()
        }
    }

    fun selectPost(postId: String) {
        selectedPostId.value = postId
    }

    fun selectChat(chatId: String) {
        activeChatId.value = chatId
    }

    fun submitSightingAlert(
        postId: String,
        petName: String,
        photoUri: String,
        locationName: String,
        latitude: Double,
        longitude: Double,
        notes: String,
        ownerId: String,
        onComplete: (String) -> Unit
    ) {
        viewModelScope.launch {
            val user = currentUser.value
            val chatId = repository.submitSightingAlert(
                postId = postId,
                petName = petName,
                reporterId = user.id,
                reporterName = user.name,
                photoUri = photoUri.ifBlank { "https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&w=600&q=80" },
                locationName = locationName,
                latitude = latitude,
                longitude = longitude,
                notes = notes,
                ownerId = ownerId
            )
            activeChatId.value = chatId
            onComplete(chatId)
        }
    }

    fun sendChatMessage(text: String, photoUri: String? = null) {
        val chatId = activeChatId.value ?: return
        val post = selectedPost.value
        val postId = post?.id ?: "post_1"
        val user = currentUser.value

        viewModelScope.launch {
            repository.sendChatMessage(
                chatId = chatId,
                postId = postId,
                senderId = user.id,
                senderName = user.name,
                text = text,
                photoUri = photoUri
            )
        }
    }

    fun toggleContactSharing(isShared: Boolean) {
        val chatId = activeChatId.value ?: return
        val user = currentUser.value
        viewModelScope.launch {
            repository.toggleChatContactSharing(
                chatId = chatId,
                isShared = isShared,
                ownerName = user.name,
                phone = user.phone,
                email = user.email
            )
        }
    }

    fun createNewPetPost(
        petName: String,
        species: String,
        breed: String,
        color: String,
        features: String,
        photoUri: String,
        lastSeenLocation: String,
        latitude: Double,
        longitude: Double,
        rewardAmount: String,
        status: String = "PERDIDO",
        onComplete: () -> Unit
    ) {
        val user = currentUser.value
        viewModelScope.launch {
            val newPost = PetPostEntity(
                id = UUID.randomUUID().toString(),
                petName = petName,
                species = species,
                breed = breed,
                color = color,
                features = features,
                status = status,
                photoUri = photoUri.ifBlank { "https://images.unsplash.com/photo-1587300003388-59208cc962cb?auto=format&fit=crop&w=600&q=80" },
                dateLost = System.currentTimeMillis(),
                lastSeenLocation = lastSeenLocation,
                latitude = latitude,
                longitude = longitude,
                rewardAmount = rewardAmount,
                ownerId = user.id,
                ownerName = user.name,
                ownerPhone = user.phone,
                ownerEmail = user.email,
                ownerAddress = "Dirección configurada por " + user.name,
                isContactRevealedToAll = false
            )
            repository.insertPost(newPost)
            selectedPostId.value = newPost.id
            onComplete()
        }
    }

    fun updatePetStatus(postId: String, newStatus: String) {
        viewModelScope.launch {
            repository.updatePostStatus(postId, newStatus)
        }
    }

    fun markNotificationAsRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }
}
