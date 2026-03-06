package com.owl.minerva.fbu.app.view.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.owl.minerva.fbu.app.services.AccountService
import com.owl.minerva.fbu.app.services.BrowserService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class DashboardViewModel(private val browserService: BrowserService, private val accountService: AccountService) {
    var accountCount by mutableStateOf(0)
    var browserCount by mutableStateOf(0)
    var isLoaded by mutableStateOf(false)

    private val scope = CoroutineScope(Dispatchers.IO)

    fun load() {
        scope.launch {
            isLoaded = true

            accountCount = accountService.index().size
            browserCount = browserService.index().size
            isLoaded = false
        }
    }
}