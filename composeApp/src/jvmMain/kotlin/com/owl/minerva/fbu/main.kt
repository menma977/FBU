package com.owl.minerva.fbu

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.owl.minerva.fbu.app.view.App
import com.owl.minerva.fbu.cores.configs.DatabaseConfig
import com.owl.minerva.fbu.cores.configs.Migration

fun main() = application {
    DatabaseConfig.initializeConnection()

    Migration.run()

    Window(
        onCloseRequest = ::exitApplication,
        title = "fbu",
    ) {
        App()
    }
}