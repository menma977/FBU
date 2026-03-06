package com.owl.minerva.fbu.cores.interfaces

import com.owl.minerva.fbu.app.models.Browser

interface AutomationTaskInterface {
    suspend fun execute(browserProfile: Browser): Unit
}
