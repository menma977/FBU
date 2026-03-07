package com.owl.minerva.fbu.app.view.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.owl.minerva.fbu.app.view.components.Card
import com.owl.minerva.fbu.app.view.models.DashboardViewModel
import java.awt.FileDialog
import java.awt.Frame

@Composable
fun DashboardScreen(viewModel: DashboardViewModel) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imagePaths by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    if (viewModel.errorMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.errorMessage = null },
            title = { Text(text = "Error") },
            text = { Text(text = viewModel.errorMessage ?: "") },
            confirmButton = {
                TextButton(onClick = { viewModel.errorMessage = null }) {
                    Text("OK")
                }
            }
        )
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

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            title = "Task Configuration",
            modifier = Modifier.fillMaxWidth().weight(1f),
            action = {
                TextButton(
                    onClick = {
                        title = ""
                        description = ""
                        imagePaths = emptyList()
                    },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Clear")
                }
            }
        ) {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Button(onClick = {
                        val fileDialog = FileDialog(null as Frame?, "Select Images", FileDialog.LOAD)
                        fileDialog.isMultipleMode = true
                        fileDialog.isVisible = true
                        val files = fileDialog.files
                        if (files != null) {
                            imagePaths = files.map { it.absolutePath }
                        }
                    }) {
                        Text("Select Images")
                    }
                    Text(text = "${imagePaths.size} images selected", modifier = Modifier.padding(start = 16.dp))
                }
                
                if (imagePaths.isNotEmpty()) {
                    LazyColumn(modifier = Modifier.height(60.dp).fillMaxWidth()) {
                        items(imagePaths) { path ->
                            Text(text = path, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 150.dp, max = 300.dp),
                    minLines = 6
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        viewModel.startAutomation(title, description, imagePaths)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Start")
                }
            }
        }
    }
}
