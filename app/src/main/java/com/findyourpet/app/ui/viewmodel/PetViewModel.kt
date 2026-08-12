package com.findyourpet.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.findyourpet.app.data.auth.AuthUiState
import com.findyourpet.app.data.auth.FirebaseAuthRepository
import com.findyourpet.app.data.auth.UnavailableAuthRepository
import com.findyourpet.app.data.local.entity.AppNotificationEntity
import com.findyourpet.app.data.local.entity.ChatMessageEntity
import com.findyourpet.app.data.local.entity.ChatSessionEntity
import com.findyourpet.app.data.local.entity.PetPostEntity
import com.findyourpet.app.data.local.entity.SightingAlertEntity
import com.findyourpet.app.data.product.LocationSource
import com.findyourpet.app.data.product.MediaSource
import com.findyourpet.app.data.product.RealProductValidators
import com.findyourpet.app.data.profile.FirestoreUserProfileRepository
import com.findyourpet.app.data.profile.UnavailableUserProfileRepository
import com.findyourpet.app.data.profile.UserProfileDocument
import com.findyourpet.app.data.profile.UserProfileRepository
import com.findyourpet.app.data.repository.PetRepository
import com.findyourpet.app.data.remote.BackendSyncState
import com.findyourpet.app.domain.AuthSessionMapper
import com.findyourpet.app.domain.OwnershipPolicy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

data class UserProfile(
    val id: String,
    val name: String,
    val email: String
)

enum class SightingSubmissionStatus { IDLE, SUBMITTING, SUCCESS, ERROR }

data class SightingSubmissionState(
    val status: SightingSubmissionStatus = SightingSubmissionStatus.IDLE,
    val message: String? = null
)

class PetViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PetRepository(application)
    private val authRepository = FirebaseAuthRepository.createOrNull(application) ?: UnavailableAuthRepository()
    private val profileRepository: UserProfileRepository =
        if (authRepository is FirebaseAuthRepository) FirestoreUserProfileRepository() else UnavailableUserProfileRepository()
    private val activeProfile = MutableStateFlow<UserProfileDocument?>(null)
    private val _authMessage = MutableStateFlow<String?>(null)
    private val _sightingSubmissionState = MutableStateFlow(SightingSubmissionState())

    val authState: StateFlow<AuthUiState> = authRepository.authState
    val authMessage: StateFlow<String?> = _authMessage
    val sightingSubmissionState: StateFlow<SightingSubmissionState> = _sightingSubmissionState
    val isAuthenticated: StateFlow<Boolean> = authState
        .map(AuthSessionMapper::isAuthenticated)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AuthSessionMapper.isAuthenticated(authState.value))

    // Current User Profile State
    val currentUser: StateFlow<UserProfile> = combine(authState, activeProfile) { state, profile ->
        AuthSessionMapper.activeUser(state, profile)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AuthSessionMapper.signedOutUser)

    fun switchUserRole(isOwnerRole: Boolean) {
        _authMessage.value = "Owner role switching is disabled for authenticated flows."
    }

    // Search and Filters
    val searchQuery = MutableStateFlow("")
    val selectedSpecies = MutableStateFlow("Todos") // Todos, Perro, Gato, Ave, Otro
    val selectedStatusFilter = MutableStateFlow("Todos") // Todos, PERDIDO, AVISTADO, REUNIDO

    val postFeedState: StateFlow<BackendSyncState<List<PetPostEntity>>> = repository.postFeedState
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            BackendSyncState.loading(emptyList(), repository.usesRemoteBackend)
        )

    val allPosts: StateFlow<List<PetPostEntity>> = postFeedState
        .map { it.data }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredPosts: StateFlow<List<PetPostEntity>> = combine(
        allPosts,
        currentUser,
        searchQuery,
        selectedSpecies,
        selectedStatusFilter
    ) { posts, user, query, species, status ->
        posts.filter { post ->
            val matchesQuery = query.isBlank() ||
                    post.petName.contains(query, ignoreCase = true) ||
                    post.breed.contains(query, ignoreCase = true) ||
                    post.lastSeenLocation.contains(query, ignoreCase = true) ||
                    post.features.contains(query, ignoreCase = true)

            val matchesSpecies = species == "Todos" || post.species.equals(species, ignoreCase = true)
            val matchesStatus = status == "Todos" || post.status.equals(status, ignoreCase = true)
            val visibleToCurrentUser = OwnershipPolicy.canAppearInDiscoveryFeed(user.id, post.ownerId)

            visibleToCurrentUser && matchesQuery && matchesSpecies && matchesStatus
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Post State
    val selectedPostId = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedPostState: StateFlow<BackendSyncState<PetPostEntity?>> = selectedPostId.flatMapLatest { id ->
        if (id == null) {
            flowOf(BackendSyncState.data<PetPostEntity?>(null, isFromCache = false, hasPendingWrites = false, repository.usesRemoteBackend))
        } else {
            repository.getPostByIdState(id)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        BackendSyncState.loading<PetPostEntity?>(null, repository.usesRemoteBackend)
    )

    val selectedPost: StateFlow<PetPostEntity?> = selectedPostState
        .map { it.data }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedPostSightingsState: StateFlow<BackendSyncState<List<SightingAlertEntity>>> = selectedPostId.flatMapLatest { id ->
        if (id == null) {
            flowOf(BackendSyncState.data(emptyList(), isFromCache = false, hasPendingWrites = false, repository.usesRemoteBackend))
        } else {
            repository.getSightingsForPostState(id)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        BackendSyncState.loading(emptyList(), repository.usesRemoteBackend)
    )

    val selectedPostSightings: StateFlow<List<SightingAlertEntity>> = selectedPostSightingsState
        .map { it.data }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Chat State
    val activeChatId = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeChatMessagesState: StateFlow<BackendSyncState<List<ChatMessageEntity>>> = activeChatId.flatMapLatest { id ->
        if (id == null) {
            flowOf(BackendSyncState.data(emptyList(), isFromCache = false, hasPendingWrites = false, repository.usesRemoteBackend))
        } else {
            repository.getMessagesForChatState(id)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        BackendSyncState.loading(emptyList(), repository.usesRemoteBackend)
    )

    val activeChatMessages: StateFlow<List<ChatMessageEntity>> = activeChatMessagesState
        .map { it.data }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeChatSessionState: StateFlow<BackendSyncState<ChatSessionEntity?>> = activeChatId.flatMapLatest { id ->
        if (id == null) {
            flowOf(BackendSyncState.data<ChatSessionEntity?>(null, isFromCache = false, hasPendingWrites = false, repository.usesRemoteBackend))
        } else {
            repository.getChatSessionByIdState(id)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        BackendSyncState.loading<ChatSessionEntity?>(null, repository.usesRemoteBackend)
    )

    val activeChatSession: StateFlow<ChatSessionEntity?> = activeChatSessionState
        .map { it.data }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val userChatSessionsState: StateFlow<BackendSyncState<List<ChatSessionEntity>>> = currentUser.flatMapLatest { user ->
        if (user.id.isBlank()) {
            flowOf(BackendSyncState.data(emptyList(), isFromCache = false, hasPendingWrites = false, repository.usesRemoteBackend))
        } else {
            repository.getChatSessionsForUserState(user.id)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        BackendSyncState.loading(emptyList(), repository.usesRemoteBackend)
    )

    val userChatSessions: StateFlow<List<ChatSessionEntity>> = userChatSessionsState
        .map { it.data }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val notificationsState: StateFlow<BackendSyncState<List<AppNotificationEntity>>> = currentUser.flatMapLatest { user ->
        if (user.id.isBlank()) {
            flowOf(BackendSyncState.data(emptyList(), isFromCache = false, hasPendingWrites = false, repository.usesRemoteBackend))
        } else {
            repository.getNotificationsForUser(user.id)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        BackendSyncState.loading(emptyList(), repository.usesRemoteBackend)
    )

    val allNotifications: StateFlow<List<AppNotificationEntity>> = notificationsState
        .map { it.data }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            authState.collectLatest { state ->
                if (state is AuthUiState.SignedIn) {
                    val profileResult = profileRepository.ensureProfile(state.user)
                    activeProfile.value = profileResult.getOrNull()
                    repository.retainPrivateCacheForUser(state.user.uid)
                    profileResult.exceptionOrNull()?.let { error ->
                        _authMessage.value = error.message ?: "Profile could not be loaded."
                    }
                } else {
                    activeProfile.value = null
                    repository.clearPrivateCache()
                }
            }
        }
    }

    fun signUpWithEmail(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            _authMessage.value = null
            authRepository.signUpWithEmail(email, password, displayName)
                .onFailure { _authMessage.value = it.message ?: "Sign-up failed." }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authMessage.value = null
            authRepository.signInWithEmail(email, password)
                .onFailure { _authMessage.value = it.message ?: "Sign-in failed." }
        }
    }

    fun signInWithGoogleIdToken(idToken: String) {
        viewModelScope.launch {
            _authMessage.value = null
            authRepository.signInWithGoogleIdToken(idToken)
                .onFailure { _authMessage.value = it.message ?: "Google sign-in failed." }
        }
    }

    fun signOut() {
        authRepository.signOut()
        activeProfile.value = null
        _authMessage.value = null
        viewModelScope.launch {
            repository.clearPrivateCache()
        }
    }

    fun selectPost(postId: String) {
        selectedPostId.value = postId
    }

    fun selectChat(chatId: String) {
        activeChatId.value = chatId
    }

    fun resetSightingSubmissionState() {
        _sightingSubmissionState.value = SightingSubmissionState()
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
        mediaSource: MediaSource? = null,
        locationSource: LocationSource,
        idempotencyKey: String = UUID.randomUUID().toString(),
        onComplete: (String) -> Unit,
        onError: (String) -> Unit = {}
    ) {
        if (_sightingSubmissionState.value.status == SightingSubmissionStatus.SUBMITTING) return
        val user = currentAuthenticatedUser() ?: run {
            _sightingSubmissionState.value = SightingSubmissionState(
                SightingSubmissionStatus.ERROR,
                "Inicia sesion antes de reportar."
            )
            onError("Inicia sesion antes de reportar.")
            return
        }
        val validation = RealProductValidators.validateSighting(
            reporterId = user.id,
            postId = postId,
            ownerId = ownerId,
            locationName = locationName,
            locationSource = locationSource,
            photoUri = photoUri
        )
        if (!validation.isValid) {
            val message = validation.message ?: "Completa los datos del avistamiento."
            _sightingSubmissionState.value = SightingSubmissionState(SightingSubmissionStatus.ERROR, message)
            _authMessage.value = message
            onError(message)
            return
        }
        _sightingSubmissionState.value = SightingSubmissionState(SightingSubmissionStatus.SUBMITTING)
        viewModelScope.launch {
            runCatching {
                repository.submitSightingAlert(
                    postId = postId,
                    petName = petName,
                    reporterId = user.id,
                    reporterName = user.name,
                    photoUri = photoUri,
                    locationName = locationName,
                    latitude = latitude,
                    longitude = longitude,
                    notes = notes,
                    ownerId = ownerId,
                    mediaSource = mediaSource,
                    locationSource = locationSource,
                    idempotencyKey = idempotencyKey
                )
            }.onSuccess { chatId ->
                _sightingSubmissionState.value = SightingSubmissionState(SightingSubmissionStatus.SUCCESS)
                activeChatId.value = chatId
                onComplete(chatId)
            }.onFailure { error ->
                val message = backendWriteErrorMessage(error, "No se pudo enviar el avistamiento.")
                _sightingSubmissionState.value = SightingSubmissionState(SightingSubmissionStatus.ERROR, message)
                onError(message)
            }
        }
    }

    fun sendChatMessage(
        text: String,
        photoUri: String? = null,
        onComplete: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val chatId = activeChatId.value ?: run {
            onError("La conversacion no esta disponible.")
            return
        }
        val post = selectedPost.value
        val session = activeChatSession.value
        val postId = post?.id ?: session?.postId ?: run {
            onError("La publicacion asociada no esta disponible.")
            return
        }
        val user = currentAuthenticatedUser() ?: run {
            onError("Inicia sesion antes de enviar mensajes.")
            return
        }
        if (session != null && !OwnershipPolicy.isChatParticipant(user.id, session.ownerId, session.reporterId)) {
            val message = "Solo los participantes pueden enviar mensajes."
            _authMessage.value = message
            onError(message)
            return
        }

        viewModelScope.launch {
            runCatching {
                repository.sendChatMessage(
                    chatId = chatId,
                    postId = postId,
                    senderId = user.id,
                    senderName = user.name,
                    text = text,
                    photoUri = photoUri
                )
            }.onSuccess {
                onComplete()
            }.onFailure { error ->
                val message = backendWriteErrorMessage(error, "No se pudo enviar el mensaje.")
                _authMessage.value = message
                onError(message)
            }
        }
    }

    fun createNewPetPost(
        petName: String,
        species: String,
        breed: String,
        color: String,
        features: String,
        characteristics: String = "",
        photoUri: String,
        lastSeenLocation: String,
        latitude: Double,
        longitude: Double,
        rewardAmount: String,
        status: String = "PERDIDO",
        mediaSource: MediaSource,
        locationSource: LocationSource = LocationSource.MANUAL_COARSE,
        onComplete: () -> Unit,
        onError: (String) -> Unit = {}
    ) {
        val user = currentAuthenticatedUser() ?: run {
            onError("Inicia sesion antes de publicar.")
            return
        }
        val validation = RealProductValidators.validatePost(
            petName = petName,
            photoUri = photoUri,
            ownerId = user.id,
            locationName = lastSeenLocation
        )
        if (!validation.isValid) {
            val message = validation.message ?: "Completa los datos de la publicacion."
            _authMessage.value = message
            onError(message)
            return
        }
        viewModelScope.launch {
            val newPost = PetPostEntity(
                id = UUID.randomUUID().toString(),
                petName = petName,
                species = species,
                breed = breed,
                color = color,
                features = features,
                characteristics = characteristics,
                status = status,
                photoUri = photoUri,
                dateLost = System.currentTimeMillis(),
                lastSeenLocation = lastSeenLocation,
                latitude = latitude,
                longitude = longitude,
                rewardAmount = rewardAmount,
                ownerId = user.id,
                ownerName = user.name
            )
            runCatching {
                repository.insertPost(
                    post = newPost,
                    mediaSource = mediaSource,
                    locationSource = locationSource
                )
            }.onSuccess {
                selectedPostId.value = newPost.id
                onComplete()
            }.onFailure { error ->
                val message = backendWriteErrorMessage(error, "No se pudo publicar la ficha.")
                onError(message)
            }
        }
    }

    fun updatePetStatus(postId: String, newStatus: String) {
        val user = currentAuthenticatedUser() ?: return
        val post = selectedPost.value
        if (post?.id == postId && !OwnershipPolicy.canManagePost(user.id, post.ownerId)) {
            _authMessage.value = "Only the owner can update this post."
            return
        }
        viewModelScope.launch {
            runCatching {
                repository.updatePostStatus(postId, newStatus)
            }.onFailure {
                _authMessage.value = it.message ?: "No se pudo actualizar la ficha."
            }
        }
    }

    fun markNotificationAsRead(id: String) {
        val user = currentAuthenticatedUser() ?: return
        viewModelScope.launch {
            runCatching {
                repository.markNotificationAsRead(user.id, id)
            }.onFailure {
                _authMessage.value = it.message ?: "No se pudo marcar la notificacion."
            }
        }
    }

    private fun currentAuthenticatedUser(): UserProfile? {
        val user = currentUser.value
        if (user.id.isBlank()) {
            _authMessage.value = "Sign in before continuing."
            return null
        }
        return user
    }

    private fun backendWriteErrorMessage(error: Throwable, fallback: String): String {
        val rawMessage = error.message.orEmpty()
        return if (rawMessage.contains("PERMISSION_DENIED", ignoreCase = true) ||
            rawMessage.contains("Missing or insufficient permissions", ignoreCase = true)
        ) {
            "Firestore rechazo la escritura. Revisa que firestore.rules este publicado en el proyecto Firebase de prueba."
        } else {
            rawMessage.ifBlank { fallback }
        }
    }

}
