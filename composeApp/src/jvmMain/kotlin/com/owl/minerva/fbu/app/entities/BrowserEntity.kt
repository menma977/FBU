package com.owl.minerva.fbu.app.entities

import com.owl.minerva.fbu.migrations.BrowserMigration
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class BrowserEntity(id: EntityID<Long>): LongEntity(id) {
    companion object: LongEntityClass<BrowserEntity>(BrowserMigration)

    var name by BrowserMigration.name
    var path by BrowserMigration.path
}