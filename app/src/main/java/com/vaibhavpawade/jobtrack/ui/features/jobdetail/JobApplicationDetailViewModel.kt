package com.vaibhavpawade.jobtrack.ui.features.jobdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vaibhavpawade.jobtrack.data.local.JobApplicationDao
import com.vaibhavpawade.jobtrack.data.model.JobApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class JobApplicationDetailUiState(
    val jobApplication: JobApplication? = null
)

@HiltViewModel
class JobApplicationDetailViewModel @Inject constructor(
    private val jobApplicationDao: JobApplicationDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val jobId: Long = savedStateHandle.get<Long>("jobId") ?: -1

    val uiState: StateFlow<JobApplicationDetailUiState> =
        jobApplicationDao.getJobApplicationById(jobId)
            .map { JobApplicationDetailUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = JobApplicationDetailUiState()
            )

    fun saveJobApplication(jobApplication: JobApplication, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                if (jobId == -1L) {
                    jobApplicationDao.insertJobApplication(jobApplication)
                } else {
                    jobApplicationDao.updateJobApplication(jobApplication.copy(id = jobId))
                }
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }
}