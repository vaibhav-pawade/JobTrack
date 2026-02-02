package com.vaibhavpawade.jobtrack.ui.features.joblist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vaibhavpawade.jobtrack.data.local.JobApplicationDao
import com.vaibhavpawade.jobtrack.data.model.JobApplication
import com.vaibhavpawade.jobtrack.data.model.JobStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class JobListUiState(
    val isLoading: Boolean = true,
    val jobApplications: List<JobApplication> = emptyList(),
    val searchQuery: String = "",
    val statusFilter: JobStatus? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class JobListViewModel @Inject constructor(
    private val jobApplicationDao: JobApplicationDao
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _statusFilter = MutableStateFlow<JobStatus?>(null)

    val uiState: StateFlow<JobListUiState> =
        combine(_searchQuery, _statusFilter) { query, status ->
            Pair(query, status)
        }.flatMapLatest { (query, status) ->
            jobApplicationDao.getFilteredJobApplications(query, status)
                .map { jobs ->
                    JobListUiState(
                        isLoading = false,
                        jobApplications = jobs,
                        searchQuery = query,
                        statusFilter = status
                    )
                }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = JobListUiState(isLoading = true)
        )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onStatusFilterChange(status: JobStatus?) {
        _statusFilter.value = status
    }

    fun deleteJob(jobId: Long) {
        viewModelScope.launch {
            jobApplicationDao.deleteJobApplication(jobId)
        }
    }

    fun updateJobStatus(jobApplication: JobApplication, newStatus: JobStatus) {
        viewModelScope.launch {
            jobApplicationDao.updateJobApplication(
                jobApplication.copy(
                    status = newStatus,
                    lastUpdated = Date()
                )
            )
        }
    }
}
