package com.owl.minerva.fbu.app.view.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.microsoft.playwright.Page
import com.owl.minerva.fbu.app.models.Browser
import com.owl.minerva.fbu.app.services.AccountService
import com.owl.minerva.fbu.app.services.BrowserService
import com.owl.minerva.fbu.app.services.playwright.PlaywrightService
import com.owl.minerva.fbu.cores.configs.AutomationConfig
import com.owl.minerva.fbu.cores.interfaces.AutomationTaskInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.nio.file.Paths
import kotlin.random.Random


class DashboardViewModel(
    private val browserService: BrowserService,
    private val accountService: AccountService,
    private val playwrightService: PlaywrightService
) {
    var accountCount by mutableStateOf(0)
    var browserCount by mutableStateOf(0)
    var isLoaded by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    private val applicationCoroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    fun load() {
        applicationCoroutineScope.launch {
            isLoaded = true

            accountCount = accountService.index().size
            browserCount = browserService.index().size
            isLoaded = false
        }
    }

    private suspend fun simulateHumanTyping(targetPage: Page, inputSelector: String, inputText: String) {
        targetPage.focus(inputSelector)
        for (character in inputText) {
            targetPage.keyboard().type(character.toString())
            val typingDelay = Random.nextLong(AutomationConfig.MIN_TYPING_DELAY_MS, AutomationConfig.MAX_TYPING_DELAY_MS)
            delay(typingDelay)
        }
    }

    private suspend fun performHumanLikeWait() {
        val stepDelay = Random.nextLong(AutomationConfig.MIN_STEP_DELAY_MS, AutomationConfig.MAX_STEP_DELAY_MS)
        delay(stepDelay)
    }

    private suspend fun stageNavigateToTarget(activePage: Page) {
        println("Stage: Navigating to Facebook Marketplace/Post creation...")
        activePage.navigate("https://www.facebook.com/marketplace/create/item")
        performHumanLikeWait()
    }

    private suspend fun stageUploadImages(activePage: Page, imagePaths: List<String>) {
        println("Stage: Uploading images...")
        if (imagePaths.isEmpty()) return
        
        val fileInputSelector = "input[type='file']"
        val pathsAsNioPaths = imagePaths.map { Paths.get(it) }.toTypedArray()
        
        activePage.setInputFiles(fileInputSelector, pathsAsNioPaths)
        performHumanLikeWait()
    }

    private suspend fun stageFillContent(activePage: Page, title: String, description: String) {
        println("Stage: Filling title and description...")
        
        val titleSelector = "label[aria-label='Judul'] input, label[aria-label='Title'] input"
        simulateHumanTyping(activePage, titleSelector, title)
        performHumanLikeWait()

        val priceSelector = "label[aria-label='Harga'] input, label[aria-label='Price'] input"
        simulateHumanTyping(activePage, priceSelector, "0")
        performHumanLikeWait()

        val descriptionSelector = "label[aria-label='Keterangan'] textarea, label[aria-label='Description'] textarea"
        simulateHumanTyping(activePage, descriptionSelector, description)
        performHumanLikeWait()
    }

    private suspend fun stageSubmitPost(activePage: Page) {
        println("Stage: Submitting post...")
        val submitButtonSelector = "div[aria-label='Terbitkan'], div[aria-label='Publish']"
        // activePage.click(submitButtonSelector)
        println("Action: Submit button logic is ready (currently disabled for safety)")
        performHumanLikeWait()
    }

    fun startAutomation(taskTitle: String, taskDescription: String, taskImagePaths: List<String>) {
        applicationCoroutineScope.launch {
            if (!playwrightService.areBrowsersInstalled()) {
                errorMessage = "Please install the browser binary first from the Browser screen."
                return@launch
            }

            val registeredBrowsers = browserService.index()
            if (registeredBrowsers.isEmpty()) {
                errorMessage = "Please configure at least one browser profile first."
                return@launch
            }
            
            errorMessage = null
            
            val automationTaskImplementation = object : AutomationTaskInterface {
                override suspend fun execute(browserProfile: Browser, activePage: Page) {
                    println("Automation started for browser: ${browserProfile.name}")
                    
                    stageNavigateToTarget(activePage)
                    stageUploadImages(activePage, taskImagePaths)
                    stageFillContent(activePage, taskTitle, taskDescription)
                    stageSubmitPost(activePage)
                    
                    println("Automation finished for browser: ${browserProfile.name}")
                }
            }
            playwrightService.runProfilesSequentially(registeredBrowsers, automationTaskImplementation)
        }
    }
}
