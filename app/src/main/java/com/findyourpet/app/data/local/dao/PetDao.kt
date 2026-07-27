package com.findyourpet.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.findyourpet.app.data.local.entity.AppNotificationEntity
import com.findyourpet.app.data.local.entity.ChatMessageEntity
import com.findyourpet.app.data.local.entity.ChatSessionEntity
import com.findyourpet.app.data.local.entity.PetPostEntity
import com.findyourpet.app.data.local.entity.SightingAlertEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PetDao {

    // Posts
    @Query("SELECT * FROM pet_posts ORDER BY dateLost DESC")
    fun getAllPosts(): Flow<List<PetPostEntity>>

    @Query("SELECT * FROM pet_posts WHERE id = :postId")
    fun getPostById(postId: String): Flow<PetPostEntity?>

    @Query("SELECT * FROM pet_posts WHERE ownerId = :ownerId ORDER BY dateLost DESC")
    fun getPostsByOwner(ownerId: String): Flow<List<PetPostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PetPostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PetPostEntity>)

    @Query("DELETE FROM pet_posts")
    suspend fun clearPosts()

    @Query("UPDATE pet_posts SET status = :status WHERE id = :postId")
    suspend fun updatePostStatus(postId: String, status: String)

    @Query("UPDATE pet_posts SET isContactRevealedToAll = :isRevealed WHERE id = :postId")
    suspend fun updatePostContactRevealed(postId: String, isRevealed: Boolean)

    // Sightings
    @Query("SELECT * FROM sighting_alerts WHERE postId = :postId ORDER BY timestamp DESC")
    fun getSightingsForPost(postId: String): Flow<List<SightingAlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSighting(sighting: SightingAlertEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSightings(sightings: List<SightingAlertEntity>)

    @Query("DELETE FROM sighting_alerts WHERE postId = :postId")
    suspend fun clearSightingsForPost(postId: String)

    // Chat Messages
    @Query("SELECT * FROM chat_messages WHERE chatId = :chatId ORDER BY timestamp ASC")
    fun getMessagesForChat(chatId: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessageEntity>)

    @Query("DELETE FROM chat_messages WHERE chatId = :chatId")
    suspend fun clearMessagesForChat(chatId: String)

    // Chat Sessions
    @Query("SELECT * FROM chat_sessions WHERE ownerId = :userId OR reporterId = :userId ORDER BY lastMessageTimestamp DESC")
    fun getChatSessionsForUser(userId: String): Flow<List<ChatSessionEntity>>

    @Query("SELECT * FROM chat_sessions WHERE id = :chatId")
    fun getChatSessionById(chatId: String): Flow<ChatSessionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatSession(session: ChatSessionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatSessions(sessions: List<ChatSessionEntity>)

    @Query("DELETE FROM chat_sessions")
    suspend fun clearChatSessions()

    @Query("UPDATE chat_sessions SET isContactSharedByOwner = :isShared WHERE id = :chatId")
    suspend fun updateChatContactShared(chatId: String, isShared: Boolean)

    @Query("UPDATE chat_sessions SET lastMessage = :lastMessage, lastMessageTimestamp = :timestamp WHERE id = :chatId")
    suspend fun updateChatLastMessage(chatId: String, lastMessage: String, timestamp: Long)

    // Notifications
    @Query("SELECT * FROM app_notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<AppNotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: AppNotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<AppNotificationEntity>)

    @Query("DELETE FROM app_notifications")
    suspend fun clearNotifications()

    @Query("UPDATE app_notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: String)

    @Query("DELETE FROM sighting_alerts")
    suspend fun clearSightings()

    @Query("DELETE FROM chat_messages")
    suspend fun clearMessages()

    @Query("DELETE FROM chat_sessions WHERE ownerId != :userId AND reporterId != :userId")
    suspend fun clearChatSessionsNotForUser(userId: String)

    @Query("DELETE FROM app_notifications WHERE recipientId != :userId")
    suspend fun clearNotificationsNotForUser(userId: String)
}
