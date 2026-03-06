package com.owl.minerva.fbu.app.entities

import com.owl.minerva.fbu.migrations.AccountMigration
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class AccountEntity(id: EntityID<Long>): LongEntity(id) {
    companion object : LongEntityClass<AccountEntity>(AccountMigration)

    var browser by BrowserEntity referencedOn AccountMigration.browserProfileId
    var label by AccountMigration.label
    var username by AccountMigration.username
    var password by AccountMigration.password
}