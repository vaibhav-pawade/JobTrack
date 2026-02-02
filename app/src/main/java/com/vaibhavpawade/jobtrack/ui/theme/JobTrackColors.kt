package com.vaibhavpawade.jobtrack.ui.theme

import androidx.compose.ui.graphics.Color
import com.vaibhavpawade.jobtrack.data.model.JobStatus

// App background
val AppBackground = Color(0xFFF6F4EC)

// Job row backgrounds
val AcceptedBackground = Color(0xFFF2EEDD)
val RejectedBackground = Color(0xFF7A3E3E)
val DefaultStatusBackground = Color(0xFFA7EDFF)

// Text colors
val DarkText = Color(0xFF1C1C1C)
val LightText = Color(0xFFFFFFFF)

fun statusBackground(status: JobStatus): Color {
    return when (status) {
        JobStatus.ACCEPTED -> AcceptedBackground
        JobStatus.REJECTED -> RejectedBackground
        else -> DefaultStatusBackground
    }
}

fun statusTextColor(status: JobStatus): Color {
    return when (status) {
        JobStatus.REJECTED -> LightText
        else -> DarkText
    }
}
