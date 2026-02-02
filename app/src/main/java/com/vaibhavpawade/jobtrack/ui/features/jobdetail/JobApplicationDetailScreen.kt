package com.vaibhavpawade.jobtrack.ui.features.jobdetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobApplicationDetailScreen(
    viewModel: JobDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onEditJob: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.uiEvents.collectLatest {
            when (it) {
                is UiEvent.NavigateBack -> {
                    onNavigateBack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Job Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    val state = uiState
                    if (state is JobDetailUiState.Success) {
                        IconButton(onClick = { onEditJob(state.jobApplication.id) }) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                        }
                    }
                    IconButton(onClick = { viewModel.onEvent(JobDetailEvent.OnDeleteClick) }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val state = uiState) {
                is JobDetailUiState.Loading -> {
                    CircularProgressIndicator()
                }

                is JobDetailUiState.NotFound -> {
                    Text(text = "Job not found")
                }

                is JobDetailUiState.Success -> {
                    val jobApplication = state.jobApplication
                    Text(
                        text = jobApplication.companyName,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = jobApplication.jobRole, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = jobApplication.location, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}
