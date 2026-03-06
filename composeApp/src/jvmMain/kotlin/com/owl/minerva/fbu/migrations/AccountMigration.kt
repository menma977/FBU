package com.owl.minerva.fbu.migrations

import org.jetbrains.exposed.dao.id.LongIdTable

object AccountMigration: LongIdTable("account_migrations") {
    val browserProfileId = reference("browser_profile_id", BrowserMigration)
    val label = varchar("label", 255)
    val username = varchar("username", 255)
    val password = varchar("password", 255)
}