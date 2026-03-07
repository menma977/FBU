package com.owl.minerva.fbu.app.services

import com.owl.minerva.fbu.app.models.Browser
import com.owl.minerva.fbu.app.repositories.BrowserRepository
import com.owl.minerva.fbu.cores.abstracts.ServiceAbstract
import com.owl.minerva.fbu.cores.configs.AppDirectory
import java.io.File
import java.util.UUID

class BrowserService(browserRepository: BrowserRepository) : ServiceAbstract<Browser>(browserRepository) {

    suspend fun storeAndGet(model: Browser): Browser {
        val profilesDirectory: File = AppDirectory.getProfilesDirectory()
        val browserProfileIdentifier: String = UUID.randomUUID().toString()
        val allocatedProfileFolder: File = File(profilesDirectory, browserProfileIdentifier)
        
        if (!allocatedProfileFolder.exists()) {
            allocatedProfileFolder.mkdirs()
        }

        val configuredBrowserModel: Browser = model.copy(path = allocatedProfileFolder.absolutePath)
        val id = super.store(configuredBrowserModel)
        
        return configuredBrowserModel.copy(id = id)
    }

    override suspend fun delete(id: Long): Boolean {
        val targetBrowserModel: Browser? = super.show(id)
        
        if (targetBrowserModel === null) {
            return false
        }

        val wasDatabaseRecordDeleted: Boolean = super.delete(id)

        if (wasDatabaseRecordDeleted) {
            val associatedProfileFolder: File = File(targetBrowserModel.path)
            val profilesDirectoryReference: File = AppDirectory.getProfilesDirectory()

            val targetFolderPath: String = associatedProfileFolder.canonicalPath
            val profilesFolderPath: String = profilesDirectoryReference.canonicalPath
            
            val isStrictlyChildOfProfilesDirectory: Boolean = targetFolderPath.startsWith(profilesFolderPath + File.separator)
            val isNotProfilesRootDirectory: Boolean = targetFolderPath != profilesFolderPath

            if (isStrictlyChildOfProfilesDirectory && isNotProfilesRootDirectory && associatedProfileFolder.exists()) {
                associatedProfileFolder.deleteRecursively()
            }
        }

        return wasDatabaseRecordDeleted
    }
}
