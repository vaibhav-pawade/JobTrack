package com.vaibhavpawade.jobtrack.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import com.vaibhavpawade.jobtrack.data.model.JobStatus

data class StatusUiStyle(
    val chipContainerColor: Color,
    val chipContentColor: Color,
    val cardTintColor: Color,
    val indicatorColor: Color
)

fun JobStatus.toUiStyle(colorScheme: ColorScheme): StatusUiStyle {
    return when (this) {
        JobStatus.SAVED -> StatusUiStyle(
            chipContainerColor = colorScheme.tertiaryContainer.copy(alpha = 0.6f),
            chipContentColor = colorScheme.onTertiaryContainer,
            cardTintColor = colorScheme.tertiaryContainer,
            indicatorColor = colorScheme.tertiary
        )
        JobStatus.APPLIED -> StatusUiStyle(
            chipContainerColor = colorScheme.primaryContainer.copy(alpha = 0.6f),
            chipContentColor = colorScheme.onPrimaryContainer,
            cardTintColor = colorScheme.primaryContainer,
            indicatorColor = colorScheme.primary
        )
        JobStatus.INTERVIEWING -> StatusUiStyle(
            chipContainerColor = colorScheme.secondaryContainer.copy(alpha = 0.6f),
            chipContentColor = colorScheme.onSecondaryContainer,
            cardTintColor = colorScheme.secondaryContainer,
            indicatorColor = colorScheme.secondary
        )
        JobStatus.ACCEPTED -> StatusUiStyle(
            chipContainerColor = Color(0xFFC8E6C9), // Light Green
            chipContentColor = Color(0xFF2E7D32), // Dark Green
            cardTintColor = Color(0xFFC8E6C9),
            indicatorColor = Color(0xFF4CAF50)
        )
        JobStatus.REJECTED, JobStatus.NO_REPLY -> StatusUiStyle(
            chipContainerColor = colorScheme.errorContainer.copy(alpha = 0.4f),
            chipContentColor = colorScheme.onErrorContainer,
            cardTintColor = colorScheme.errorContainer,
            indicatorColor = colorScheme.error
        )
    }
}
