package com.vaibhavpawade.jobtrack.ui.features.addedit

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vaibhavpawade.jobtrack.data.model.JobStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditJobScreen(
    viewModel: AddEditJobViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvents.collect {
            when (it) {
                is UiEvent.ShowSnackbar -> {
                    isLoading = false
                    snackbarHostState.showSnackbar(it.message)
                }

                is UiEvent.NavigateBack -> {
                    onNavigateBack()
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(if (viewModel.isEditing) "Edit Job" else "Add Job") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        if (!isLoading) {
                            isLoading = true
                            viewModel.onEvent(AddEditJobEvent.OnSaveClick)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    AnimatedContent(
                        targetState = isLoading,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(200)).togetherWith(fadeOut(animationSpec = tween(200)))
                        }, label = "SaveButtonAnimation"
                    ) { loading ->
                        if (loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(if (viewModel.isEditing) "Update" else "Save")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Company Info Section
            Text(
                text = "Company Info",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
            OutlinedTextField(
                value = viewModel.companyName,
                onValueChange = { viewModel.onEvent(AddEditJobEvent.OnCompanyNameChange(it)) },
                label = { Text("Company Name *") },
                leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = viewModel.jobRole,
                onValueChange = { viewModel.onEvent(AddEditJobEvent.OnJobRoleChange(it)) },
                label = { Text("Job Role *") },
                leadingIcon = { Icon(Icons.Default.Work, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Job Info Section
            Text(
                text = "Job Info",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
            OutlinedTextField(
                value = viewModel.location,
                onValueChange = { viewModel.onEvent(AddEditJobEvent.OnLocationChange(it)) },
                label = { Text("Location") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = viewModel.jobPostingUrl,
                onValueChange = { viewModel.onEvent(AddEditJobEvent.OnJobPostingUrlChange(it)) },
                label = { Text("Job Posting URL") },
                leadingIcon = { Icon(Icons.Default.Link, contentDescription = null) },
                singleLine = true,
                supportingText = { Text("e.g. https://www.linkedin.com/jobs/...") },
                modifier = Modifier.fillMaxWidth()
            )

            // Tracking Section
            Text(
                text = "Tracking",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
            OutlinedTextField(
                value = viewModel.notes,
                onValueChange = { if (it.length <= 1000) viewModel.onEvent(AddEditJobEvent.OnNotesChange(it)) },
                label = { Text("Notes") },
                leadingIcon = { Icon(Icons.AutoMirrored.Filled.Notes, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                supportingText = {
                    Text(
                        text = "${viewModel.notes.length} / 1000",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                    )
                },
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = viewModel.salary,
                onValueChange = { viewModel.onEvent(AddEditJobEvent.OnSalaryChange(it)) },
                label = { Text("Salary") },
                leadingIcon = { Icon(Icons.Default.MonetizationOn, contentDescription = null) },
                singleLine = true,
                supportingText = { Text("e.g. $120,000-$150,000") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            StatusDropdown(
                selectedStatus = viewModel.status,
                onStatusSelected = { viewModel.onEvent(AddEditJobEvent.OnStatusChange(it)) }
            )
            Spacer(modifier = Modifier.height(8.dp))
            DatePickerField(
                date = viewModel.date,
                onDateChange = { viewModel.onEvent(AddEditJobEvent.OnDateChange(it)) },
                label = "Date Applied"
            )
            Spacer(modifier = Modifier.height(100.dp)) // Add space for the sticky button
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusDropdown(selectedStatus: JobStatus, onStatusSelected: (JobStatus) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            readOnly = true,
            value = selectedStatus.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
            onValueChange = {},
            label = { Text("Status") },
            leadingIcon = {
                Box(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(getJobStatusColor(status = selectedStatus))
                )
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            JobStatus.values().forEach { status ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(getJobStatusColor(status = status))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(status.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() })
                        }
                    },
                    onClick = {
                        onStatusSelected(status)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

@Composable
fun getJobStatusColor(status: JobStatus): Color {
    return when (status) {
        JobStatus.SAVED -> MaterialTheme.colorScheme.tertiaryContainer
        JobStatus.APPLIED -> MaterialTheme.colorScheme.primaryContainer
        JobStatus.INTERVIEWING -> MaterialTheme.colorScheme.secondaryContainer
        JobStatus.ACCEPTED -> Color(0xFFB8F5B8) // Custom Green
        JobStatus.REJECTED, JobStatus.NO_REPLY -> MaterialTheme.colorScheme.errorContainer
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    date: Date?,
    onDateChange: (Date?) -> Unit,
    label: String
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val formatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    OutlinedTextField(
        value = date?.let { formatter.format(it) } ?: "",
        onValueChange = {},
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                showDatePicker = true
            },
        readOnly = true,
        label = { Text(label) },
        leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
        interactionSource = remember { MutableInteractionSource() }
            .also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        if (it is PressInteraction.Release) {
                            showDatePicker = true
                        }
                    }
                }
            }
    )

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = date?.time
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            // Adjust for timezone offset
                            val tz = TimeZone.getDefault()
                            val offset = tz.getOffset(it)
                            onDateChange(Date(it + offset))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
