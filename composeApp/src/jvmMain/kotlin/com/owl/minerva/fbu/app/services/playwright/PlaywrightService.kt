package com.owl.minerva.fbu.app.services.playwright

import com.microsoft.playwright.BrowserContext
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Playwright
import com.owl.minerva.fbu.app.models.Browser
import com.owl.minerva.fbu.cores.configs.AppDirectory
import com.owl.minerva.fbu.cores.interfaces.AutomationTaskInterface
import com.owl.minerva.fbu.cores.interfaces.PlaywrightServiceInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.stream.Collectors

class PlaywrightService : PlaywrightServiceInterface {

    private var isSessionRunning: Boolean = false
    private var playwrightInstance: Playwright? = null
    private var activeBrowserContext: BrowserContext? = null
    private val masterBinariesPath: String = AppDirectory.getMasterBrowserDirectory().absolutePath

    init {
        System.setProperty("playwright.browsers.path", masterBinariesPath)
    }

    override suspend fun launchBrowserProfile(browserProfile: Browser): Unit = withContext(Dispatchers.IO) {
        if (isSessionRunning) {
            terminateSession()
        }

        try {
            playwrightInstance = Playwright.create()

            val localExecutablePath = findChromiumExecutable(browserProfile.path)

            val persistentLaunchOptions: BrowserType.LaunchPersistentContextOptions = BrowserType.LaunchPersistentContextOptions()
                .setHeadless(false)

            if (localExecutablePath != null) {
                persistentLaunchOptions.setExecutablePath(localExecutablePath)
            } else {
                throw IllegalStateException("Chromium executable isn't found in profile directory: ${browserProfile.path}. Please ensure the browser is installed and the profile was created correctly.")
            }

            val userDataDirectoryPath = Paths.get(browserProfile.path)
            activeBrowserContext = playwrightInstance!!.chromium().launchPersistentContext(userDataDirectoryPath, persistentLaunchOptions)

            if (activeBrowserContext!!.pages().isEmpty()) {
                activeBrowserContext!!.newPage()
            }

            isSessionRunning = true
        } catch (exception: Exception) {
            exception.printStackTrace()
            terminateSession()
            throw exception
        }
    }

    private fun findChromiumExecutable(profilePath: String): Path? {
        val profileDirectory = Paths.get(profilePath)
        if (!Files.exists(profileDirectory)) return null

        val searchPattern = if (System.getProperty("os.name").lowercase().contains("win")) "chrome.exe" else "chrome"

        try {
            Files.walk(profileDirectory).use { stream ->
                return stream
                    .filter { Files.isRegularFile(it) }
                    .filter { it.fileName.toString() == searchPattern }
                    .findFirst()
                    .orElse(null)
            }
        } catch (_: Exception) {
            return null
        }
    }

    override suspend fun terminateSession(): Unit = withContext(Dispatchers.IO) {
        try {
            activeBrowserContext?.close()
            playwrightInstance?.close()
        } catch (exception: Exception) {
            exception.printStackTrace()
        } finally {
            activeBrowserContext = null
            playwrightInstance = null
            isSessionRunning = false
        }
    }

    override suspend fun isSessionActive(): Boolean {
        return this.isSessionRunning
    }

    suspend fun areBrowsersInstalled(): Boolean = withContext(Dispatchers.IO) {
        val rootBinariesPath = Paths.get(masterBinariesPath)
        if (!Files.exists(rootBinariesPath)) return@withContext false

        val directoryStream = Files.newDirectoryStream(rootBinariesPath)
        val chromiumDirectoryExists = directoryStream.any { it.fileName.toString().startsWith("chromium-") }
        directoryStream.close()
        chromiumDirectoryExists
    }

    suspend fun installBrowsers(onProgressLogReceived: (String) -> Unit): Unit = withContext(Dispatchers.IO) {
        try {
            val currentJavaHome: String = System.getProperty("java.home")
            val javaExecutablePath: String = Paths.get(currentJavaHome, "bin", "java").toString()
            val currentApplicationClasspath: String = System.getProperty("java.class.path")

            val installationProcessBuilder = ProcessBuilder(
                javaExecutablePath,
                "-cp", currentApplicationClasspath,
                "com.microsoft.playwright.CLI",
                "install", "chromium"
            )

            installationProcessBuilder.environment()["PLAYWRIGHT_BROWSERS_PATH"] = masterBinariesPath
            installationProcessBuilder.redirectErrorStream(true)

            val runningInstallationProcess = installationProcessBuilder.start()
            val processOutputReader = BufferedReader(InputStreamReader(runningInstallationProcess.inputStream))

            var outputLine: String?
            while (processOutputReader.readLine().also { outputLine = it } != null) {
                onProgressLogReceived(outputLine!!)
            }

            runningInstallationProcess.waitFor()
        } catch (exception: Exception) {
            onProgressLogReceived("Error during installation: ${exception.message}")
            exception.printStackTrace()
        }
    }

    suspend fun copyMasterToProfile(targetProfilePath: String): Unit = withContext(Dispatchers.IO) {
        val sourcePath = Paths.get(masterBinariesPath)
        val targetPath = Paths.get(targetProfilePath)

        if (!Files.exists(sourcePath)) return@withContext

        try {
            Files.walk(sourcePath).use { stream ->
                for (sourceFile in stream.collect(Collectors.toList())) {
                    val relativePath = sourcePath.relativize(sourceFile)
                    val destinationFile = targetPath.resolve(relativePath)

                    if (Files.isDirectory(sourceFile)) {
                        if (!Files.exists(destinationFile)) {
                            Files.createDirectories(destinationFile)
                        }
                    } else {
                        Files.copy(sourceFile, destinationFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES)

                        if (!System.getProperty("os.name").lowercase().contains("win")) {
                            if (Files.isExecutable(sourceFile)) {
                                val permissions = Files.getPosixFilePermissions(sourceFile)
                                Files.setPosixFilePermissions(destinationFile, permissions)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun runProfilesSequentially(
        browserProfiles: List<Browser>,
        automationTask: AutomationTaskInterface
    ) {
        for (currentBrowserProfile: Browser in browserProfiles) {
            try {
                this.launchBrowserProfile(currentBrowserProfile)
                automationTask.execute(currentBrowserProfile)
            } finally {
                this.terminateSession()
            }
        }
    }
}
