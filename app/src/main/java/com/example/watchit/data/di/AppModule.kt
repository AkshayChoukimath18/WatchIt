package com.example.watchit.data.di

import android.content.Context
import androidx.room.Room
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

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "movie_reco.db"
        ).build()
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