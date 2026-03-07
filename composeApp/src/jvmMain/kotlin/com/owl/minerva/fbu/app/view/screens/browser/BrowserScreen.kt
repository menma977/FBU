package com.owl.minerva.fbu.app.view.screens.browser

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.owl.minerva.fbu.app.models.Browser
import com.owl.minerva.fbu.app.view.models.BrowserViewModel

@Composable
fun BrowserScreen(viewModel: BrowserViewModel) {
    LaunchedEffect(Unit) {
        viewModel.load()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            // Header
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
                    enabled = viewModel.isBrowserInstalled && !viewModel.isInstalling,
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

            // System Status / Installation Area
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "System Status",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = if (viewModel.isBrowserInstalled) "Chromium is ready." else "Chromium is not installed.",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (viewModel.isBrowserInstalled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                        }
                        
                        if (!viewModel.isBrowserInstalled && !viewModel.isInstalling) {
                            Button(onClick = { viewModel.performInstallation() }) {
                                Text("Install Browser")
                            }
                        }
                    }
                    
                    if (viewModel.isInstalling) {
                        Spacer(modifier = Modifier.height(16.dp))
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(8.dp)
                        ) {
                            val scrollState = rememberScrollState()
                            LaunchedEffect(viewModel.installationLogs) {
                                scrollState.animateScrollTo(scrollState.maxValue)
                            }
                            Text(
                                text = viewModel.installationLogs,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.verticalScroll(scrollState)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Browser List
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

                                Row {
                                    IconButton(
                                        onClick = { viewModel.openBrowser(browserModel) },
                                        enabled = viewModel.isBrowserInstalled && !viewModel.isLaunchingInProgress
                                    ) {
                                        if (viewModel.isLaunchingInProgress) {
                                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                        } else {
                                            Icon(
                                                Icons.Default.Launch,
                                                contentDescription = "Open Browser",
                                                tint = if (viewModel.isBrowserInstalled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                            )
                                        }
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
        }

        // Add Dialog
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
                    TextButton(
                        onClick = { viewModel.addBrowser() },
                        enabled = !viewModel.isCopyingInProgress
                    ) {
                        if (viewModel.isCopyingInProgress) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Copying...", color = MaterialTheme.colorScheme.primary)
                            }
                        } else {
                            Text("Add", color = MaterialTheme.colorScheme.primary)
                        }
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
        
        // Error Dialog
        if (viewModel.errorMessage != null) {
            AlertDialog(
                onDismissRequest = { viewModel.errorMessage = null },
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Error", color = MaterialTheme.colorScheme.error)
                    }
                },
                text = { Text(text = viewModel.errorMessage ?: "") },
                confirmButton = {
                    TextButton(onClick = { viewModel.errorMessage = null }) {
                        Text("OK")
                    }
                },
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}
