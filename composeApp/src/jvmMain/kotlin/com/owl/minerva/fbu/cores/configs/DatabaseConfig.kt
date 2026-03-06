package com.owl.minerva.fbu.cores.configs

import org.jetbrains.exposed.sql.Database
import java.io.File

object DatabaseConfig {
    fun initializeConnection(): Database {
        val isDevelopmentMode: Boolean = AppConfig.APP_ENVIRONMENT == "development"

        val userHomeDirectory: String = if (isDevelopmentMode) {
            System.getProperty("user.dir")
        } else {
            System.getProperty("user.home")
        }
        val appStorageDirectory = File(userHomeDirectory, ".fbu")

        if (!appStorageDirectory.exists()) {
            appStorageDirectory.mkdirs()
        }

        val databaseFile = File(appStorageDirectory, "fbu.sqlite")
        val sqliteJdbcUrl = "jdbc:sqlite:${databaseFile.absolutePath}"
        val sqliteDriverClassName = "org.sqlite.JDBC"

        return Database.connect(sqliteJdbcUrl, sqliteDriverClassName)
    }
}