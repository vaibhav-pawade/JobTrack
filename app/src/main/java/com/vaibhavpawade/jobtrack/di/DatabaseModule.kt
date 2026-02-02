package com.vaibhavpawade.jobtrack.di

import android.content.Context
import androidx.room.Room
import com.vaibhavpawade.jobtrack.data.local.JobApplicationDao
import com.vaibhavpawade.jobtrack.data.local.JobTrackDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideJobTrackDatabase(@ApplicationContext context: Context): JobTrackDatabase {
        return Room.databaseBuilder(
            context,
            JobTrackDatabase::class.java,
            "jobtrack_database"
        ).fallbackToDestructiveMigration()
         .build()
    }

    @Provides
    @Singleton
    fun provideJobApplicationDao(database: JobTrackDatabase): JobApplicationDao {
        return database.jobApplicationDao()
    }
}
