package com.owl.minerva.fbu.app.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.owl.minerva.fbu.app.repositories.AccountRepository
import com.owl.minerva.fbu.app.repositories.BrowserRepository
import com.owl.minerva.fbu.app.services.AccountService
import com.owl.minerva.fbu.app.services.BrowserService
import com.owl.minerva.fbu.app.services.playwright.PlaywrightService
import com.owl.minerva.fbu.app.view.models.BrowserViewModel
import com.owl.minerva.fbu.app.view.models.DashboardViewModel
import com.owl.minerva.fbu.app.view.screens.DashboardScreen
import com.owl.minerva.fbu.app.view.screens.browser.BrowserScreen
import com.owl.minerva.fbu.app.view.theme.FBUTheme

@Composable
fun App() {
    val browserRepo = remember { BrowserRepository() }
    val accountRepo = remember { AccountRepository() }
    val browserService = remember { BrowserService(browserRepo) }
    val accountService = remember { AccountService(accountRepo, browserService) }
    val playwrightService = remember { PlaywrightService() }
    val dashboardViewModel = remember { DashboardViewModel(browserService, accountService, playwrightService) }
    val browserViewModel = remember { BrowserViewModel(browserService, playwrightService) }

    var currentScreen by remember { mutableStateOf("dashboard") }

    FBUTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                NavigationRail(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxHeight().width(80.dp)
                ) {
                    Spacer(modifier = Modifier.height(24.dp))
                    NavigationRailItem(
                        selected = currentScreen == "dashboard",
                        onClick = { currentScreen = "dashboard" },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.background,
                            unselectedIconColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.secondary
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    NavigationRailItem(
                        selected = currentScreen == "browsers",
                        onClick = { currentScreen = "browsers" },
                        icon = { Icon(Icons.Default.Language, contentDescription = null) },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.background,
                            unselectedIconColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.secondary
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    NavigationRailItem(
                        selected = currentScreen == "accounts",
                        onClick = { currentScreen = "accounts" },
                        icon = { Icon(Icons.Default.People, contentDescription = null) },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.background,
                            unselectedIconColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.secondary
                        )
                    )
                }

                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    when (currentScreen) {
                        "dashboard" -> DashboardScreen(dashboardViewModel)
                        "browsers" -> BrowserScreen(browserViewModel)
                        "accounts" -> Text("Account List Screen", modifier = Modifier.padding(24.dp), color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}