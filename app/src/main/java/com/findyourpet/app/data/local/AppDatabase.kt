package com.findyourpet.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.findyourpet.app.data.local.dao.PetDao
import com.findyourpet.app.data.local.entity.AppNotificationEntity
import com.findyourpet.app.data.local.entity.ChatMessageEntity
import com.findyourpet.app.data.local.entity.ChatSessionEntity
import com.findyourpet.app.data.local.entity.PetPostEntity
import com.findyourpet.app.data.local.entity.SightingAlertEntity

@Database(
    entities = [
        PetPostEntity::class,
        SightingAlertEntity::class,
        ChatMessageEntity::class,
        ChatSessionEntity::class,
        AppNotificationEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun petDao(): PetDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mascotas_perdidas_db"
                ).addMigrations(MIGRATION_2_4, MIGRATION_3_4).build()
                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_2_4 = object : Migration(2, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                dropRetiredContactSharingState(db)
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                dropRetiredContactSharingState(db)
            }
        }

        private fun dropRetiredContactSharingState(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS pet_posts_new (
                    id TEXT NOT NULL PRIMARY KEY,
                    petName TEXT NOT NULL,
                    species TEXT NOT NULL,
                    breed TEXT NOT NULL,
                    color TEXT NOT NULL,
                    features TEXT NOT NULL,
                    status TEXT NOT NULL,
                    photoUri TEXT NOT NULL,
                    dateLost INTEGER NOT NULL,
                    lastSeenLocation TEXT NOT NULL,
                    latitude REAL NOT NULL,
                    longitude REAL NOT NULL,
                    rewardAmount TEXT NOT NULL,
                    ownerId TEXT NOT NULL,
                    ownerName TEXT NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT INTO pet_posts_new (
                    id, petName, species, breed, color, features, status, photoUri,
                    dateLost, lastSeenLocation, latitude, longitude, rewardAmount,
                    ownerId, ownerName
                )
                SELECT
                    id, petName, species, breed, color, features, status, photoUri,
                    dateLost, lastSeenLocation, latitude, longitude, rewardAmount,
                    ownerId, ownerName
                FROM pet_posts
                """.trimIndent()
            )
            db.execSQL("DROP TABLE pet_posts")
            db.execSQL("ALTER TABLE pet_posts_new RENAME TO pet_posts")

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS chat_sessions_new (
                    id TEXT NOT NULL PRIMARY KEY,
                    postId TEXT NOT NULL,
                    petName TEXT NOT NULL,
                    petPhotoUri TEXT NOT NULL,
                    ownerId TEXT NOT NULL,
                    reporterId TEXT NOT NULL,
                    reporterName TEXT NOT NULL,
                    lastMessage TEXT NOT NULL,
                    lastMessageTimestamp INTEGER NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT INTO chat_sessions_new (
                    id, postId, petName, petPhotoUri, ownerId, reporterId,
                    reporterName, lastMessage, lastMessageTimestamp
                )
                SELECT
                    id, postId, petName, petPhotoUri, ownerId, reporterId,
                    reporterName, lastMessage, lastMessageTimestamp
                FROM chat_sessions
                """.trimIndent()
            )
            db.execSQL("DROP TABLE chat_sessions")
            db.execSQL("ALTER TABLE chat_sessions_new RENAME TO chat_sessions")
            db.execSQL("DROP TABLE IF EXISTS contact_grants")
        }
    }
}
