package com.vaibhavpawade.jobtrack.ui.features.addedit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vaibhavpawade.jobtrack.data.local.JobApplicationDao
import com.vaibhavpawade.jobtrack.data.model.JobApplication
import com.vaibhavpawade.jobtrack.data.model.JobStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddEditJobViewModel @Inject constructor(
    private val dao: JobApplicationDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var companyName by mutableStateOf("")
    var jobRole by mutableStateOf("")
    var location by mutableStateOf("")
    var jobPostingUrl by mutableStateOf("")
    var notes by mutableStateOf("")
    var salary by mutableStateOf("")
    var date by mutableStateOf<Date?>(null)
    var status by mutableStateOf(JobStatus.SAVED)

    val isEditing = savedStateHandle.get<Long>("jobId") != -1L
    private var jobId: Long? = savedStateHandle.get<Long>("jobId")?.takeIf { it != -1L }

    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    init {
        jobId?.let {
            viewModelScope.launch {
                dao.getJobApplicationById(it).collect { job ->
                    job?.let {
                        companyName = job.companyName
                        jobRole = job.jobRole
                        location = job.location
                        jobPostingUrl = job.jobPostingUrl.orEmpty()
                        notes = job.notes
                        salary = job.salaryRange.orEmpty()
                        date = job.dateApplied
                        status = job.status
                    }
                }
            }
        }
    }

    fun onEvent(event: AddEditJobEvent) {
        when (event) {
            is AddEditJobEvent.OnCompanyNameChange -> companyName = event.value
            is AddEditJobEvent.OnJobRoleChange -> jobRole = event.value
            is AddEditJobEvent.OnLocationChange -> location = event.value
            is AddEditJobEvent.OnJobPostingUrlChange -> jobPostingUrl = event.value
            is AddEditJobEvent.OnNotesChange -> notes = event.value
            is AddEditJobEvent.OnSalaryChange -> salary = event.value
            is AddEditJobEvent.OnDateChange -> date = event.value
            is AddEditJobEvent.OnStatusChange -> status = event.value
            is AddEditJobEvent.OnSaveClick -> saveJob()
        }
    }

    private fun saveJob() {
        viewModelScope.launch {
            if (companyName.isBlank() || jobRole.isBlank() || location.isBlank()) {
                _uiEvents.emit(UiEvent.ShowSnackbar("Company Name, Job Role and Location are required."))
                return@launch
            }

            val jobApplication = JobApplication(
                id = jobId ?: 0,
                companyName = companyName,
                jobRole = jobRole,
                location = location,
                jobPostingUrl = jobPostingUrl,
                notes = notes,
                dateApplied = date,
                salaryRange = salary,
                status = status,
                dateSaved = Date(),
                lastUpdated = Date()
            )

            if (isEditing) {
                dao.updateJobApplication(jobApplication)
            } else {
                dao.insertJobApplication(jobApplication)
            }
            _uiEvents.emit(UiEvent.NavigateBack)
        }
    }
}

sealed class UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent()
    object NavigateBack : UiEvent()
}

sealed class AddEditJobEvent {
    data class OnCompanyNameChange(val value: String) : AddEditJobEvent()
    data class OnJobRoleChange(val value: String) : AddEditJobEvent()
    data class OnLocationChange(val value: String) : AddEditJobEvent()
    data class OnJobPostingUrlChange(val value: String) : AddEditJobEvent()
    data class OnNotesChange(val value: String) : AddEditJobEvent()
    data class OnSalaryChange(val value: String) : AddEditJobEvent()
    data class OnDateChange(val value: Date?) : AddEditJobEvent()
    data class OnStatusChange(val value: JobStatus) : AddEditJobEvent()
    object OnSaveClick : AddEditJobEvent()
}