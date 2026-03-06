package com.owl.minerva.fbu.app.repositories

import com.owl.minerva.fbu.app.entities.AccountEntity
import com.owl.minerva.fbu.app.entities.BrowserEntity
import com.owl.minerva.fbu.app.models.Account
import com.owl.minerva.fbu.cores.interfaces.RepositoryInterface
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class AccountRepository : RepositoryInterface<Account> {

    private fun AccountEntity.toModel(): Account = Account(
        id = id.value,
        browserProfileId = this.browser.id.value,
        label = this.label,
        username = this.username,
        password = this.password
    )

    override suspend fun getAll(): List<Account> = newSuspendedTransaction {
        AccountEntity.all().map { it.toModel() }
    }

    override suspend fun getById(id: Long): Account? = newSuspendedTransaction {
        AccountEntity.findById(id)?.toModel()
    }

    override suspend fun insert(model: Account): Long = newSuspendedTransaction {
        val browserReference = BrowserEntity.findById(model.browserProfileId) ?: throw Exception("Browser Profile not found ${model.browserProfileId}")

        AccountEntity.new {
            browser = browserReference
            label = model.label
            username = model.username
            password = model.password
        }.id.value
    }

    override suspend fun update(id: Long, model: Account): Boolean = newSuspendedTransaction {
        val account = AccountEntity.findById(id) ?: return@newSuspendedTransaction false
        val browserReference = BrowserEntity.findById(model.browserProfileId) ?: throw Exception("Browser Profile not found ${model.browserProfileId}")

        account.browser = browserReference
        account.label = model.label
        account.username = model.username
        account.password = model.password

        true
    }

    override suspend fun delete(id: Long): Boolean = newSuspendedTransaction {
        val account = AccountEntity.findById(id)

        if (account === null) return@newSuspendedTransaction false

        account.delete()

        true
    }
}