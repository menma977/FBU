package com.owl.minerva.fbu.app.repositories

import com.owl.minerva.fbu.app.entities.BrowserEntity
import com.owl.minerva.fbu.app.models.Browser
import com.owl.minerva.fbu.cores.interfaces.RepositoryInterface
import com.owl.minerva.fbu.migrations.BrowserMigration
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class BrowserRepository : RepositoryInterface<Browser> {
    private fun BrowserEntity.toModel(): Browser = Browser(
        id = id.value,
        name = name,
        path = path,
    )

    override suspend fun getAll(): List<Browser> = newSuspendedTransaction {
        BrowserEntity.all().map { it.toModel() }
    }

    override suspend fun getById(id: Long): Browser? = newSuspendedTransaction {
        BrowserEntity.findById(id)?.toModel()
    }

    override suspend fun insert(model: Browser): Long = newSuspendedTransaction {
        val generatedId = BrowserMigration.insertAndGetId {
            it[name] = model.name
            it[path] = model.path
        }
        generatedId.value
    }

    override suspend fun update(id: Long, model: Browser): Boolean = newSuspendedTransaction {
        val entity = BrowserEntity.findById(id)

        if (entity === null) return@newSuspendedTransaction false

        entity.name = model.name
        entity.path = model.path

        true
    }

    override suspend fun delete(id: Long): Boolean = newSuspendedTransaction {
        val entity = BrowserEntity.findById(id)

        if (entity === null) return@newSuspendedTransaction false

        entity.delete()

        true
    }

}