package com.vaibhavpawade.jobtrack.ui.features.jobdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vaibhavpawade.jobtrack.data.local.JobApplicationDao
import com.vaibhavpawade.jobtrack.data.model.JobApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class JobDetailUiState {
    object Loading : JobDetailUiState()
    data class Success(val jobApplication: JobApplication) : JobDetailUiState()
    object NotFound : JobDetailUiState()
}

sealed class JobDetailEvent {
    object OnDeleteClick : JobDetailEvent()
    data class OnSaveClick(val jobApplication: JobApplication) : JobDetailEvent()
}

sealed class UiEvent {
    object NavigateBack : UiEvent()
}

@HiltViewModel
class JobDetailViewModel @Inject constructor(
    private val dao: JobApplicationDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val jobId: Long = savedStateHandle.get<Long>("jobId")!!

    val uiState: StateFlow<JobDetailUiState> = dao.getJobApplicationById(jobId)
        .map {
            if (it != null) {
                JobDetailUiState.Success(it)
            } else {
                JobDetailUiState.NotFound
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = JobDetailUiState.Loading
        )

    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    fun onEvent(event: JobDetailEvent) {
        when (event) {
            is JobDetailEvent.OnDeleteClick -> {
                viewModelScope.launch {
                    dao.deleteJobApplication(jobId)
                    _uiEvents.emit(UiEvent.NavigateBack)
                }
            }

            is JobDetailEvent.OnSaveClick -> {
                viewModelScope.launch {
                    dao.updateJobApplication(event.jobApplication)
                }
            }
        }
    }
}
