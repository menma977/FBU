package com.owl.minerva.fbu.cores.configs

import com.owl.minerva.fbu.migrations.AccountMigration
import com.owl.minerva.fbu.migrations.BrowserMigration
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object Migration {
    fun run() {
        transaction {
            SchemaUtils.create(AccountMigration, BrowserMigration)
        }
    }
}