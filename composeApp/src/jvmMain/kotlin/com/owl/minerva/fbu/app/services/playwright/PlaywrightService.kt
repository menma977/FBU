package com.owl.minerva.fbu.app.services.playwright

import com.owl.minerva.fbu.app.models.Browser
import com.owl.minerva.fbu.cores.interfaces.AutomationTaskInterface
import com.owl.minerva.fbu.cores.interfaces.PlaywrightServiceInterface

class PlaywrightService : PlaywrightServiceInterface {
    
    private var isRunning: Boolean = false

    override suspend fun launchBrowserProfile(browserProfile: Browser): Unit {
        // TODO: Implement launching Playwright instance with persistent context parameter (userDataDir = browserProfile.path)
        // TODO: Handle proxy, browser args, etc here later
        this.isRunning = true
    }

    override suspend fun terminateSession(): Unit {
        // TODO: Implement gracefully shutting down Chromium and Playwright instance
        this.isRunning = false
    }

    override suspend fun isSessionActive(): Boolean {
        return this.isRunning
    }

    override suspend fun runProfilesSequentially(
        browserProfiles: List<Browser>, 
        automationTask: AutomationTaskInterface
    ): Unit {
        for (browserProfile: Browser in browserProfiles) {
            // 1. Launch the specific browser profile
            this.launchBrowserProfile(browserProfile)
            
            // 2. Execute the isolated container logic (e.g. login, click, etc from a separate file/class)
            automationTask.execute(browserProfile)
            
            // 3. Close the browser profile before opening the next one
            this.terminateSession()
        }
    }
}
