package com.owl.minerva.fbu.migrations

import org.jetbrains.exposed.dao.id.LongIdTable

object BrowserMigration : LongIdTable("browser_migrations") {
    val name = varchar("name", 255)
    val path = varchar("path", 500)
}