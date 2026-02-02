package com.vaibhavpawade.jobtrack.ui.features.joblist

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vaibhavpawade.jobtrack.data.model.JobApplication
import com.vaibhavpawade.jobtrack.data.model.JobStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun JobListScreen(
    viewModel: JobListViewModel = hiltViewModel(),
    onAddJobClick: () -> Unit,
    onJobClick: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    JobListScreenContent(
        uiState = uiState,
        onAddJobClick = onAddJobClick,
        onJobClick = onJobClick,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onStatusFilterChange = viewModel::onStatusFilterChange,
        onDeleteJob = viewModel::deleteJob,
        onUpdateJobStatus = viewModel::updateJobStatus
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun JobListScreenContent(
    uiState: JobListUiState,
    onAddJobClick: () -> Unit,
    onJobClick: (Long) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onStatusFilterChange: (JobStatus?) -> Unit,
    onDeleteJob: (Long) -> Unit,
    onUpdateJobStatus: (JobApplication, JobStatus) -> Unit
) {
    val listState = rememberLazyListState()
    val expandedFab by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("All Applications", style = MaterialTheme.typography.headlineMedium) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent
                    )
                )
                SearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = onSearchQueryChange,
                    onSearch = {},
                    active = false,
                    onActiveChange = {},
                    placeholder = { Text("Company name") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Outlined.Close, contentDescription = "Clear search")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {

                }
                FilterChips(
                    selectedStatus = uiState.statusFilter,
                    onStatusSelected = onStatusFilterChange
                )
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddJobClick,
                expanded = expandedFab,
                icon = { Icon(Icons.Default.Add, "Add Job") },
                text = { Text(text = "Add Job") },
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            when {
                uiState.isLoading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.jobApplications.isEmpty() -> {
                    EmptyState(
                        searchQuery = uiState.searchQuery,
                        onClearSearch = { onSearchQueryChange("") }
                    )
                }

                else -> {
                    Text(
                        text = "${uiState.jobApplications.size} applications • Updated today",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    JobList(
                        jobs = uiState.jobApplications,
                        onJobClick = onJobClick,
                        onDelete = onDeleteJob,
                        onStatusChange = onUpdateJobStatus,
                        modifier = Modifier.animateContentSize()
                    )
                }
            }
        }
    }
}

@Composable
fun FilterChips(
    selectedStatus: JobStatus?,
    onStatusSelected: (JobStatus?) -> Unit
) {
    val statuses = listOf("All") + JobStatus.values().map { it.name.replace("_", " ") }
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(statuses) { statusString ->
            val isSelected = when (statusString) {
                "All" -> selectedStatus == null
                else -> selectedStatus?.name?.replace("_", " ") == statusString
            }
            FilterChip(
                selected = isSelected,
                onClick = {
                    val newStatus = when (statusString) {
                        "All" -> null
                        else -> JobStatus.valueOf(statusString.replace(" ", "_").uppercase())
                    }
                    onStatusSelected(newStatus)
                },
                label = { Text(statusString.lowercase().replaceFirstChar { it.uppercase() }) },
                leadingIcon = if (isSelected) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Done icon"
                        )
                    }
                } else {
                    null
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun JobList(
    jobs: List<JobApplication>,
    onJobClick: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    onStatusChange: (JobApplication, JobStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(jobs, key = { it.id }) { job ->
            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = {
                    when (it) {
                        SwipeToDismissBoxValue.EndToStart -> {
                            onDelete(job.id)
                            true
                        }

                        SwipeToDismissBoxValue.StartToEnd -> {
                            val nextStatus = when (job.status) {
                                JobStatus.SAVED -> JobStatus.APPLIED
                                JobStatus.APPLIED -> JobStatus.INTERVIEWING
                                JobStatus.INTERVIEWING -> JobStatus.ACCEPTED
                                else -> job.status
                            }
                            onStatusChange(job, nextStatus)
                            false
                        }
                        else -> false
                    }
                }
            )
            SwipeToDismissBox(
                state = dismissState,
                modifier = Modifier.animateItemPlacement(),
                backgroundContent = {
                    val color = when (dismissState.targetValue) {
                        SwipeToDismissBoxValue.EndToStart -> Color.Red.copy(alpha = 0.8f)
                        SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.primary.copy(
                            alpha = 0.8f
                        )

                        else -> Color.Transparent
                    }
                    val icon = when (dismissState.targetValue) {
                        SwipeToDismissBoxValue.EndToStart -> Icons.Default.Delete
                        SwipeToDismissBoxValue.StartToEnd -> Icons.Default.Edit
                        else -> null
                    }
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(color)
                            .padding(horizontal = 20.dp),
                        contentAlignment = when (dismissState.targetValue) {
                            SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                            else -> Alignment.CenterStart
                        }
                    ) {
                        if (icon != null) {
                            Icon(icon, contentDescription = "Swipe Action")
                        }
                    }
                }) {
                JobListItem(jobApplication = job, onClick = { onJobClick(job.id) })
            }
        }
    }
}

@Composable
private fun JobListItem(jobApplication: JobApplication, onClick: () -> Unit) {
    val backgroundColor = when (jobApplication.status) {
        JobStatus.ACCEPTED -> Color(0xFFC7E3D6)
        JobStatus.REJECTED -> Color(0xFFF0BBC4)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = when (jobApplication.status) {
        JobStatus.REJECTED -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = jobApplication.companyName,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = jobApplication.jobRole,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = jobApplication.dateApplied?.let {
                        SimpleDateFormat("d MMM", Locale.getDefault()).format(it)
                    } ?: "",
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(modifier = Modifier.height(4.dp))
                val statusText = jobApplication.status.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
                AssistChip(
                    onClick = { /* No-op */ },
                    label = { Text(statusText) }
                )
            }
        }
    }
}

@Composable
private fun EmptyState(searchQuery: String, onClearSearch: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "No applications found")
        if (searchQuery.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onClearSearch) {
                Text("Clear search")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun JobListScreenPreview() {
    JobListScreenContent(
        uiState = JobListUiState(
            isLoading = false,
            jobApplications = listOf(
                JobApplication(
                    id = 1,
                    companyName = "Google",
                    jobRole = "Android Developer",
                    status = JobStatus.APPLIED,
                    dateApplied = Date(),
                    location = "",
                    notes = ""
                ),
                JobApplication(
                    id = 2,
                    companyName = "Meta",
                    jobRole = "iOS Developer",
                    status = JobStatus.INTERVIEWING,
                    dateApplied = Date(),
                    location = "",
                    notes = ""
                )
            )
        ),
        onAddJobClick = {},
        onJobClick = {},
        onSearchQueryChange = {},
        onStatusFilterChange = {},
        onDeleteJob = {},
        onUpdateJobStatus = { _, _ -> }
    )
}
