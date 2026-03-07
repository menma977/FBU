package com.owl.minerva.fbu.app.view.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.owl.minerva.fbu.app.models.Browser
import com.owl.minerva.fbu.app.services.AccountService
import com.owl.minerva.fbu.app.services.BrowserService
import com.owl.minerva.fbu.app.services.playwright.PlaywrightService
import com.owl.minerva.fbu.cores.interfaces.AutomationTaskInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class DashboardViewModel(
    private val browserService: BrowserService,
    private val accountService: AccountService,
    private val playwrightService: PlaywrightService
) {
    var accountCount by mutableStateOf(0)
    var browserCount by mutableStateOf(0)
    var isLoaded by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    private val scope = CoroutineScope(Dispatchers.IO)

    fun load() {
        scope.launch {
            isLoaded = true

            accountCount = accountService.index().size
            browserCount = browserService.index().size
            isLoaded = false
        }
    }

    fun startAutomation(title: String, description: String, imagePaths: List<String>) {
        scope.launch {
            if (!playwrightService.areBrowsersInstalled()) {
                errorMessage = "Please install the browser binary first from the Browser screen."
                return@launch
            }

            val browsers = browserService.index()
            if (browsers.isEmpty()) {
                errorMessage = "Please configure at least one browser profile first."
                return@launch
            }
            
            errorMessage = null
            
            val task = object : AutomationTaskInterface {
                override suspend fun execute(browserProfile: Browser) {
                    println("Running task '$title' on browser ${browserProfile.name}")
                    // Implement further automation execution steps using description & imagePaths here
                }
            }
            playwrightService.runProfilesSequentially(browsers, task)
        }
    }
}