package com.owl.minerva.fbu.app.view.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.owl.minerva.fbu.app.models.Browser
import com.owl.minerva.fbu.app.services.BrowserService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BrowserViewModel(private val browserService: BrowserService) {
    var browsers: List<Browser> by mutableStateOf(emptyList())
    var isLoading: Boolean by mutableStateOf(false)
    var isAddDialogOpen: Boolean by mutableStateOf(false)
    var newBrowserName: String by mutableStateOf("")

    private val applicationCoroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    fun load() {
        applicationCoroutineScope.launch {
            isLoading = true
            browsers = browserService.index()
            isLoading = false
        }
    }

    fun addBrowser() {
        if (newBrowserName.isBlank()) return

        applicationCoroutineScope.launch {
            isLoading = true
            val browserToStore: Browser = Browser(name = newBrowserName, path = "")
            browserService.store(browserToStore)
            browsers = browserService.index()
            isAddDialogOpen = false
            newBrowserName = ""
            isLoading = false
        }
    }

    fun deleteBrowser(id: Long) {
        applicationCoroutineScope.launch {
            isLoading = true
            browserService.delete(id)
            browsers = browserService.index()
            isLoading = false
        }
    }
}
