package com.example.watchit.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.watchit.data.local.AppDatabase
import com.example.watchit.data.local.LibraryDao
import com.example.watchit.data.remote.LinkPreviewFetcher
import com.example.watchit.ui.repo.LibraryRepository
import com.example.watchit.ui.repo.LibraryRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    val MIGRATION_1_2 = object : Migration(1,2){
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS movies_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                year INTEGER,
                posterUrl TEXT,
                sourceUrl TEXT NOT NULL,
                sourcePlatform TEXT NOT NULL,
                createdAt INTEGER NOT NULL
                )
            """.trimIndent()
            )
            db.execSQL("""
            INSERT INTO movies_new (id, title, year, posterUrl, sourceUrl, sourcePlatform, createdAt)
            SELECT 
                MIN(id), title, year, posterUrl, sourceUrl, sourcePlatform, createdAt
            FROM movies
            GROUP BY sourceUrl
        """)

            // 3️⃣ Drop old table
            db.execSQL("DROP TABLE movies")

            // 4️⃣ Rename
            db.execSQL("ALTER TABLE movies_new RENAME TO movies")

            // 5️⃣ Create UNIQUE index
            db.execSQL("""
            CREATE UNIQUE INDEX index_movies_sourceUrl ON movies(sourceUrl)
        """)
        }
    }
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "movie_reco.db"
        ).addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides
    fun provideLibraryDao(db: AppDatabase): LibraryDao{
        return db.libraryDao()
    }

    @Provides
    @Singleton
    fun provideLinkFetcher(): LinkPreviewFetcher = LinkPreviewFetcher()

    @Provides
    @Singleton
    fun provideLibraryRepository(dao: LibraryDao, linkPreviewFetcher: LinkPreviewFetcher): LibraryRepository = LibraryRepositoryImpl(dao, provideLinkFetcher())

}