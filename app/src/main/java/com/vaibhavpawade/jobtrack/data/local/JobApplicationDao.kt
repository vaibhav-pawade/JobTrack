package com.vaibhavpawade.jobtrack.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.vaibhavpawade.jobtrack.data.model.JobApplication
import com.vaibhavpawade.jobtrack.data.model.JobStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface JobApplicationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJobApplication(jobApplication: JobApplication)

    @Update
    suspend fun updateJobApplication(jobApplication: JobApplication)

    @Query("DELETE FROM job_applications WHERE id = :jobId")
    suspend fun deleteJobApplication(jobId: Long)

    @Query("SELECT * FROM job_applications ORDER BY dateSaved DESC")
    fun getAllJobApplications(): Flow<List<JobApplication>>

    @Query("SELECT * FROM job_applications WHERE id = :jobId")
    fun getJobApplicationById(jobId: Long): Flow<JobApplication?>

    @Query("""
        SELECT * FROM job_applications 
        WHERE (:status IS NULL OR status = :status) 
        AND (companyName LIKE '%' || :query || '%' OR jobRole LIKE '%' || :query || '%')
        ORDER BY dateSaved DESC
        """)
    fun getFilteredJobApplications(query: String, status: JobStatus?): Flow<List<JobApplication>>
}
