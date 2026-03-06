package com.owl.minerva.fbu.app.services

import com.owl.minerva.fbu.app.models.Account
import com.owl.minerva.fbu.app.repositories.AccountRepository
import com.owl.minerva.fbu.cores.abstracts.ServiceAbstract

class AccountService(private val accountRepository: AccountRepository, private val browserService: BrowserService) : ServiceAbstract<Account>(accountRepository) {
    suspend fun triggerAutomation(accountId: Long) {
        val account = show(accountId) ?: throw Exception("Account not found")
        browserService.show(account.browserProfileId) ?: throw Exception("Browser Profile not found")

        println("Triggering automation for an account: ${account.id}")
    }

    override suspend fun delete(id: Long): Boolean {
        val account = show(id) ?: throw Exception("Account not found")
        val browser = browserService.show(account.browserProfileId) ?: throw Exception("Browser Profile not found")
        val success = super.delete(id)

        if (success) {
            val file = java.io.File(browser.path).canonicalFile
            val appDataDirectory = java.io.File(System.getProperty("user.home"), ".fbu").canonicalFile

            val isInsideAppDataDirectory = file.canonicalPath.startsWith(appDataDirectory.canonicalPath)
            val isNotRoot = file.absolutePath != appDataDirectory.absolutePath

            if (isInsideAppDataDirectory && isNotRoot && file.exists() && file.isDirectory && file.name.startsWith("profile_")) file.deleteRecursively()
        }

        return success
    }
}