package com.muzafrik.app.di

import android.content.Context
import androidx.room.Room
import com.muzafrik.app.data.local.AppDatabase
import com.muzafrik.app.data.local.MusicDao
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
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "muzafrik.db").build()

    @Provides
    fun provideMusicDao(db: AppDatabase): MusicDao = db.musicDao()
}
