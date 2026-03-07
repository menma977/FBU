package com.owl.minerva.fbu.app.view.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.owl.minerva.fbu.app.models.Browser
import com.owl.minerva.fbu.app.services.BrowserService
import com.owl.minerva.fbu.app.services.playwright.PlaywrightService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BrowserViewModel(
    private val browserService: BrowserService,
    private val playwrightService: PlaywrightService
) {
    var browsers: List<Browser> by mutableStateOf(emptyList())
    var isLoading: Boolean by mutableStateOf(false)
    var isAddDialogOpen: Boolean by mutableStateOf(false)
    var newBrowserName: String by mutableStateOf("")

    var isBrowserInstalled by mutableStateOf(false)
    var isInstalling by mutableStateOf(false)
    var isCopyingInProgress by mutableStateOf(false)
    var isLaunchingInProgress by mutableStateOf(false)
    var installationLogs by mutableStateOf("")
    var errorMessage by mutableStateOf<String?>(null)

    private val applicationCoroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    fun load() {
        applicationCoroutineScope.launch {
            isLoading = true
            refreshBrowserInstallationStatus()
            browsers = browserService.index()
            isLoading = false
        }
    }

    suspend fun refreshBrowserInstallationStatus() {
        isBrowserInstalled = playwrightService.areBrowsersInstalled()
    }

    fun performInstallation() {
        applicationCoroutineScope.launch {
            isInstalling = true
            installationLogs = "Starting installation...\n"
            playwrightService.installBrowsers { receivedLogLine ->
                installationLogs += receivedLogLine + "\n"
            }
            refreshBrowserInstallationStatus()
            isInstalling = false
        }
    }

    fun addBrowser() {
        if (newBrowserName.isBlank()) return

        if (!isBrowserInstalled) {
            errorMessage = "Please install the browser binary first."
            return
        }

        applicationCoroutineScope.launch {
            isLoading = true
            isCopyingInProgress = true

            val pendingBrowserModel = Browser(name = newBrowserName, path = "")
            val successfullyStoredBrowser = browserService.storeAndGet(pendingBrowserModel)

            try {
                playwrightService.copyMasterToProfile(successfullyStoredBrowser.path)
            } catch (exception: Exception) {
                errorMessage = "Failed to copy browser files: ${exception.message}"
            }

            browsers = browserService.index()
            isAddDialogOpen = false
            newBrowserName = ""
            isCopyingInProgress = false
            isLoading = false
        }
    }

    fun openBrowser(targetBrowser: Browser) {
        applicationCoroutineScope.launch {
            isLaunchingInProgress = true
            try {
                playwrightService.launchBrowserProfile(targetBrowser)
            } catch (exception: Exception) {
                errorMessage = "Failed to launch browser: ${exception.message}"
            } finally {
                isLaunchingInProgress = false
            }
        }
    }

    fun deleteBrowser(targetBrowserId: Long) {
        applicationCoroutineScope.launch {
            isLoading = true
            browserService.delete(targetBrowserId)
            browsers = browserService.index()
            isLoading = false
        }
    }
}
