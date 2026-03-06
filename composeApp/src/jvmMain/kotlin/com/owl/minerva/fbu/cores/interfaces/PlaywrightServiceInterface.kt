package com.owl.minerva.fbu.cores.interfaces

import com.owl.minerva.fbu.app.models.Browser

interface PlaywrightServiceInterface {
    suspend fun launchBrowserProfile(browserProfile: Browser): Unit
    suspend fun terminateSession(): Unit
    suspend fun isSessionActive(): Boolean
    
    suspend fun runProfilesSequentially(
        browserProfiles: List<Browser>,
        automationTask: AutomationTaskInterface
    ): Unit
}
