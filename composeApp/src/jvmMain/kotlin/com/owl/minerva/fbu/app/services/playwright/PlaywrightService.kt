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

    suspend fun launchBrowserProfileManual(browserProfile: Browser): Unit = withContext(Dispatchers.IO) {
        val executablePath = findChromiumExecutable(browserProfile.path)
            ?: throw IllegalStateException("Chromium executable isn't found in profile directory: ${browserProfile.path}")

        val processArguments = mutableListOf<String>()
        processArguments.add(executablePath.toString())
        processArguments.add("--user-data-dir=${browserProfile.path}")
        processArguments.add("--no-first-run")
        processArguments.add("--no-default-browser-check")
        processArguments.add("https://www.facebook.com")

        val nativeBrowserProcessBuilder = ProcessBuilder(processArguments)
        try {
            nativeBrowserProcessBuilder.start()
        } catch (processLaunchException: Exception) {
            processLaunchException.printStackTrace()
            throw processLaunchException
        }
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
                .setIgnoreDefaultArgs(listOf("--enable-automation", "--no-sandbox"))
                .setArgs(listOf("--disable-blink-features=AutomationControlled", "--no-first-run"))
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36")

            if (localExecutablePath != null) {
                persistentLaunchOptions.setExecutablePath(localExecutablePath)
            } else {
                throw IllegalStateException("Chromium executable isn't found in profile directory: ${browserProfile.path}")
            }

            val userDataDirectoryPath = Paths.get(browserProfile.path)
            activeBrowserContext = playwrightInstance!!.chromium().launchPersistentContext(userDataDirectoryPath, persistentLaunchOptions)

            val stealthInitializationScript = """
                () => {
                    Object.defineProperty(navigator, 'webdriver', { get: () => undefined });
                    Object.defineProperty(navigator, 'languages', { get: () => ['en-US', 'en', 'id-ID', 'id'] });
                    Object.defineProperty(navigator, 'deviceMemory', { get: () => 8 });
                    Object.defineProperty(navigator, 'hardwareConcurrency', { get: () => 8 });
                    
                    if (window.WebGLRenderingContext) {
                        const webglCanvas = document.createElement('canvas');
                        const webglContext = webglCanvas.getContext('webgl');
                        if (webglContext) {
                            const webglDebugInfo = webglContext.getExtension('WEBGL_debug_renderer_info');
                            if (webglDebugInfo) {
                                const originalGetParameter = WebGLRenderingContext.prototype.getParameter;
                                WebGLRenderingContext.prototype.getParameter = function(parameter) {
                                    if (parameter === webglDebugInfo.UNMASKED_VENDOR_WEBGL) return 'Intel Inc.';
                                    if (parameter === webglDebugInfo.UNMASKED_RENDERER_WEBGL) return 'Intel(R) UHD Graphics 620';
                                    return originalGetParameter.apply(this, arguments);
                                };
                            }
                        }
                    }
                }
            """.trimIndent()
            activeBrowserContext!!.addInitScript(stealthInitializationScript)

            val currentPages = activeBrowserContext!!.pages()
            val initialPage = if (currentPages.isEmpty()) {
                activeBrowserContext!!.newPage()
            } else {
                currentPages[0]
            }

            initialPage.navigate("https://www.facebook.com")

            isSessionRunning = true
        } catch (launchException: Exception) {
            launchException.printStackTrace()
            terminateSession()
            throw launchException
        }
    }

    private fun findChromiumExecutable(profilePath: String): Path? {
        val profileDirectory = Paths.get(profilePath)
        if (!Files.exists(profileDirectory)) return null

        val platformSearchPattern = if (System.getProperty("os.name").lowercase().contains("win")) "chrome.exe" else "chrome"

        try {
            Files.walk(profileDirectory).use { directoryWalkStream ->
                return directoryStreamToPath(directoryWalkStream, platformSearchPattern)
            }
        } catch (_: Exception) {
            return null
        }
    }

    private fun directoryStreamToPath(stream: java.util.stream.Stream<Path>, pattern: String): Path? {
        return stream
            .filter { fileEntry -> Files.isRegularFile(fileEntry) }
            .filter { fileEntry -> fileEntry.fileName.toString() == pattern }
            .findFirst()
            .orElse(null)
    }

    override suspend fun terminateSession(): Unit = withContext(Dispatchers.IO) {
        try {
            activeBrowserContext?.close()
            playwrightInstance?.close()
        } catch (terminationException: Exception) {
            terminationException.printStackTrace()
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
        val chromiumDirectoryExists = directoryStream.any { directoryItem -> directoryItem.fileName.toString().startsWith("chromium-") }
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
            while (processOutputReader.readLine().also { receivedLine -> outputLine = receivedLine } != null) {
                onProgressLogReceived(outputLine!!)
            }

            runningInstallationProcess.waitFor()
        } catch (installationException: Exception) {
            onProgressLogReceived("Error during installation: ${installationException.message}")
            installationException.printStackTrace()
        }
    }

    suspend fun copyMasterToProfile(targetProfilePath: String): Unit = withContext(Dispatchers.IO) {
        val sourcePath = Paths.get(masterBinariesPath)
        val targetPath = Paths.get(targetProfilePath)

        if (!Files.exists(sourcePath)) return@withContext

        try {
            val sourceFilesList = Files.walk(sourcePath).use { stream -> stream.collect(Collectors.toList()) }
            for (sourceFile in sourceFilesList) {
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
                            val filePermissions = Files.getPosixFilePermissions(sourceFile)
                            Files.setPosixFilePermissions(destinationFile, filePermissions)
                        }
                    }
                }
            }
        } catch (copyException: Exception) {
            copyException.printStackTrace()
            throw copyException
        }
    }

    override suspend fun runProfilesSequentially(
        browserProfiles: List<Browser>,
        automationTask: AutomationTaskInterface
    ) {
        for (currentBrowserProfile: Browser in browserProfiles) {
            try {
                this.launchBrowserProfile(currentBrowserProfile)
                
                val currentPagesList = activeBrowserContext?.pages()
                if (currentPagesList != null && currentPagesList.isNotEmpty()) {
                    val activePageInstance = currentPagesList[0]
                    automationTask.execute(currentBrowserProfile, activePageInstance)
                }
            } finally {
                this.terminateSession()
            }
        }
    }
}
