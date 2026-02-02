package com.vaibhavpawade.jobtrack.ui.features.joblist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vaibhavpawade.jobtrack.data.model.JobApplication
import com.vaibhavpawade.jobtrack.data.model.JobStatus
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun JobListItem(
    jobApplication: JobApplication,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = jobApplication.companyName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = jobApplication.jobRole,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = jobApplication.dateApplied?.toFormattedDateString() ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                StatusChip(status = jobApplication.status)
            }
        }
    }
}

@Composable
fun StatusChip(status: JobStatus, modifier: Modifier = Modifier) {
    val (backgroundColor, textColor, text) = when (status) {
        JobStatus.SAVED -> Triple(Color(0xFFE0E0E0), Color.Black, "💾 Saved")
        JobStatus.ACCEPTED -> Triple(Color(0xFFE8F5E9), Color(0xFF388E3C), "✔ Accepted")
        JobStatus.APPLIED -> Triple(Color(0xFFE3F2FD), Color(0xFF1976D2), "😎 Applied")
        JobStatus.INTERVIEWING -> Triple(Color(0xFFFFF8E1), Color(0xFFFBC02D), "🤔 Interviewing")
        JobStatus.REJECTED -> Triple(Color(0xFFFFEBEE), Color(0xFFD32F2F), "🙅 Rejected")
        JobStatus.NO_REPLY -> Triple(Color.LightGray, Color.Black, "💀 No reply")
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(text = text, color = textColor, style = MaterialTheme.typography.labelMedium)
    }
}

private fun java.util.Date.toFormattedDateString(): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(this)
}
