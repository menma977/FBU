package com.owl.minerva.fbu.cores.configs

import org.jetbrains.exposed.sql.Database
import java.io.File

object DatabaseConfig {
    fun initializeConnection(): Database {
        val appStorageDirectory: File = AppDirectory.getAppStorageDirectory()
        val databaseFile: File = File(appStorageDirectory, "fbu.sqlite")
        val sqliteJdbcUrl: String = "jdbc:sqlite:${databaseFile.absolutePath}"
        val sqliteDriverClassName: String = "org.sqlite.JDBC"

        return Database.connect(sqliteJdbcUrl, sqliteDriverClassName)
    }
}
