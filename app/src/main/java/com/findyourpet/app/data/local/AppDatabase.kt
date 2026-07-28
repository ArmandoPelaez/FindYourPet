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
import com.findyourpet.app.data.local.entity.ContactGrantEntity
import com.findyourpet.app.data.local.entity.PetPostEntity
import com.findyourpet.app.data.local.entity.SightingAlertEntity

@Database(
    entities = [
        PetPostEntity::class,
        SightingAlertEntity::class,
        ChatMessageEntity::class,
        ChatSessionEntity::class,
        ContactGrantEntity::class,
        AppNotificationEntity::class
    ],
    version = 3,
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
                ).addMigrations(MIGRATION_2_3).build()
                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
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
                        ownerName TEXT NOT NULL,
                        ownerPhone TEXT NOT NULL,
                        ownerEmail TEXT NOT NULL,
                        ownerAddress TEXT NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO pet_posts_new (
                        id, petName, species, breed, color, features, status, photoUri,
                        dateLost, lastSeenLocation, latitude, longitude, rewardAmount,
                        ownerId, ownerName, ownerPhone, ownerEmail, ownerAddress
                    )
                    SELECT
                        id, petName, species, breed, color, features, status, photoUri,
                        dateLost, lastSeenLocation, latitude, longitude, rewardAmount,
                        ownerId, ownerName, ownerPhone, ownerEmail, ownerAddress
                    FROM pet_posts
                    """.trimIndent()
                )
                db.execSQL("DROP TABLE pet_posts")
                db.execSQL("ALTER TABLE pet_posts_new RENAME TO pet_posts")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS contact_grants (
                        id TEXT NOT NULL PRIMARY KEY,
                        chatId TEXT NOT NULL,
                        postId TEXT NOT NULL,
                        ownerId TEXT NOT NULL,
                        reporterId TEXT NOT NULL,
                        sharedBy TEXT NOT NULL,
                        sharedAt INTEGER NOT NULL,
                        revokedAt INTEGER,
                        isActive INTEGER NOT NULL,
                        ownerName TEXT NOT NULL,
                        ownerPhone TEXT NOT NULL,
                        ownerEmail TEXT NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }
    }
}
