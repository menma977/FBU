package com.owl.minerva.fbu.app.view.screens.browser

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.owl.minerva.fbu.app.models.Browser
import com.owl.minerva.fbu.app.view.models.BrowserViewModel

@Composable
fun BrowserScreen(viewModel: BrowserViewModel) {
    LaunchedEffect(Unit) {
        viewModel.load()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Browsers",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Button(
                    onClick = { viewModel.isAddDialogOpen = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.background
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Browser")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Profile")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (viewModel.isLoading && viewModel.browsers.isEmpty()) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(viewModel.browsers) { browserModel: Browser ->
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = browserModel.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Path: ${browserModel.path}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }

                                IconButton(onClick = { viewModel.deleteBrowser(browserModel.id) }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete Browser",
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (viewModel.isAddDialogOpen) {
            AlertDialog(
                onDismissRequest = { viewModel.isAddDialogOpen = false },
                title = { Text("Add New Browser", color = MaterialTheme.colorScheme.primary) },
                text = {
                    OutlinedTextField(
                        value = viewModel.newBrowserName,
                        onValueChange = { viewModel.newBrowserName = it },
                        label = { Text("Browser Name", color = MaterialTheme.colorScheme.secondary) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.primary,
                            unfocusedTextColor = MaterialTheme.colorScheme.primary,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                            cursorColor = MaterialTheme.colorScheme.primary
                        )
                    )
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.addBrowser() }) {
                        Text("Add", color = MaterialTheme.colorScheme.primary)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.isAddDialogOpen = false }) {
                        Text("Cancel", color = MaterialTheme.colorScheme.secondary)
                    }
                },
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}
