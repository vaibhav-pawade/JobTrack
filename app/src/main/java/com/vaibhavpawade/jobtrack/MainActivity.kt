package com.vaibhavpawade.jobtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.vaibhavpawade.jobtrack.ui.NavGraph
import com.vaibhavpawade.jobtrack.ui.theme.JobTrackTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JobTrackTheme(darkTheme = isSystemInDarkTheme()) {
                NavGraph()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    JobTrackTheme {
        NavGraph()
    }
}
