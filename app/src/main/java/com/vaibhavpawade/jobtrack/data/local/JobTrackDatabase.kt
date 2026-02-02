package com.vaibhavpawade.jobtrack.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vaibhavpawade.jobtrack.data.model.JobApplication

@Database(entities = [JobApplication::class], version = 17, exportSchema = false)
abstract class JobTrackDatabase : RoomDatabase() {
    abstract fun jobApplicationDao(): JobApplicationDao
}
