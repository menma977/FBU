package com.owl.minerva.fbu.app.view.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.owl.minerva.fbu.app.view.components.Card
import com.owl.minerva.fbu.app.view.models.DashboardViewModel

@Composable
fun DashboardScreen(viewModel: DashboardViewModel) {
    LaunchedEffect(Unit) {
        viewModel.load()
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Card(title = "Total Accounts", modifier = Modifier.weight(1f)) {
                Text(
                    text = "${viewModel.accountCount}",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Card(title = "Browsers Configured", modifier = Modifier.weight(1f)) {
                Text(
                    text = "${viewModel.browserCount}",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}