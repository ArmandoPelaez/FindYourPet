package com.findyourpet.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.findyourpet.app.data.local.entity.AppNotificationEntity
import com.findyourpet.app.data.local.entity.ContentReportEntity
import com.findyourpet.app.data.local.entity.PetPostEntity
import com.findyourpet.app.data.local.entity.SightingAlertEntity
import com.findyourpet.app.data.local.entity.UserBlockEntity
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

    // Sightings
    @Query("SELECT * FROM sighting_alerts WHERE postId = :postId ORDER BY timestamp DESC")
    fun getSightingsForPost(postId: String): Flow<List<SightingAlertEntity>>

    @Query("SELECT * FROM sighting_alerts WHERE ownerId = :ownerId ORDER BY timestamp DESC")
    fun getSightingsForOwner(ownerId: String): Flow<List<SightingAlertEntity>>

    @Query("SELECT * FROM sighting_alerts WHERE id = :sightingId")
    fun getSightingById(sightingId: String): Flow<SightingAlertEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSighting(sighting: SightingAlertEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSightings(sightings: List<SightingAlertEntity>)

    @Query("DELETE FROM sighting_alerts WHERE postId = :postId")
    suspend fun clearSightingsForPost(postId: String)

    @Query("DELETE FROM sighting_alerts WHERE ownerId = :ownerId")
    suspend fun clearSightingsForOwner(ownerId: String)

    // Moderation
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertContentReport(report: ContentReportEntity)

    @Query("SELECT * FROM content_reports WHERE id = :reportId")
    suspend fun getContentReport(reportId: String): ContentReportEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUserBlock(block: UserBlockEntity)

    @Query("SELECT * FROM user_blocks WHERE blockerUserId = :blockerUserId AND blockedUserId = :blockedUserId LIMIT 1")
    suspend fun getUserBlock(blockerUserId: String, blockedUserId: String): UserBlockEntity?

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

    @Query("DELETE FROM app_notifications WHERE recipientId != :userId")
    suspend fun clearNotificationsNotForUser(userId: String)
}
