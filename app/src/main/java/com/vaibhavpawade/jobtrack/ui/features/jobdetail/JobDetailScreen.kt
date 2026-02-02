package com.vaibhavpawade.jobtrack.ui.features.jobdetail

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vaibhavpawade.jobtrack.data.model.JobApplication
import com.vaibhavpawade.jobtrack.data.model.JobStatus
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailScreen(
    viewModel: JobDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onEditJob: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState is JobDetailUiState.Success) {
                        val job = (uiState as JobDetailUiState.Success).jobApplication
                        IconButton(onClick = { onEditJob(job.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { viewModel.onEvent(JobDetailEvent.OnDeleteClick) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            if (uiState is JobDetailUiState.Success) {
                val job = (uiState as JobDetailUiState.Success).jobApplication
                JobDetailContent(job = job)
            }
        }
    }
}

@Composable
private fun JobDetailContent(job: JobApplication) {
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = job.companyName,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${job.salaryRange ?: ""} • ${job.jobRole} • ${job.location}",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        job.jobPostingUrl?.let {
            AssistChip(
                onClick = {
                    if (it.startsWith("http://") || it.startsWith("https://")) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                        context.startActivity(intent)
                    }
                },
                label = { Text("Open Link") },
                leadingIcon = {
                    Icon(
                        Icons.AutoMirrored.Filled.Launch,
                        contentDescription = "Open job posting"
                    )
                }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        AnimatedContent(
            targetState = job.status,
            transitionSpec = {
                (slideInVertically { height -> height } + fadeIn())
                    .togetherWith(fadeOut(animationSpec = tween(200)))
            },
            label = "StatusChip"
        ) {
            StatusChip(status = it)
        }

        Spacer(modifier = Modifier.height(24.dp))

        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Application Date", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                job.dateApplied?.let {
                    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    Text(text = sdf.format(it))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        var isExpanded by remember { mutableStateOf(false) }
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Notes", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = job.notes,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                )
                if (job.notes.lines().size > 2) {
                    Text(
                        text = if (isExpanded) "Read less" else "Read more",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: JobStatus) {
    val color = when (status) {
        JobStatus.SAVED -> MaterialTheme.colorScheme.tertiaryContainer
        JobStatus.APPLIED -> MaterialTheme.colorScheme.primaryContainer
        JobStatus.INTERVIEWING -> MaterialTheme.colorScheme.secondaryContainer
        JobStatus.ACCEPTED -> MaterialTheme.colorScheme.surfaceVariant
        JobStatus.REJECTED, JobStatus.NO_REPLY -> MaterialTheme.colorScheme.errorContainer
    }
    
    ElevatedCard(colors = CardDefaults.cardColors(containerColor = color)) {
        Text(
            text = status.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}
