package com.vaibhavpawade.jobtrack.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.Date

@Entity(tableName = "job_applications")
@TypeConverters(DateConverter::class, JobStatusConverter::class)
data class JobApplication(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val companyName: String,
    val jobRole: String,
    val location: String,
    val jobPostingUrl: String? = null,
    val notes: String,
    var dateApplied: Date?,
    val salaryRange: String? = null,
    val status: JobStatus = JobStatus.SAVED,
    val dateSaved: Date = Date(),
    var lastUpdated: Date = Date()
)

enum class JobStatus {
    SAVED,
    APPLIED,
    INTERVIEWING,
    ACCEPTED,
    REJECTED,
    NO_REPLY
}

class DateConverter {
    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }

    @TypeConverter
    fun toTimestamp(date: Date?): Long? {
        return date?.time
    }
}

class JobStatusConverter {
    @TypeConverter
    fun toStatus(value: String) = enumValueOf<JobStatus>(value)

    @TypeConverter
    fun fromStatus(value: JobStatus) = value.name
}
