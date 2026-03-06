package com.owl.minerva.fbu.cores.configs

import java.io.File

object AppDirectory {
    fun getAppStorageDirectory(): File {
        val isDevelopmentMode: Boolean = AppConfig.APP_ENVIRONMENT == "development"
        val userHomeDirectory: String = if (isDevelopmentMode) {
            System.getProperty("user.dir")
        } else {
            System.getProperty("user.home")
        }
        
        val appStorageDirectory: File = File(userHomeDirectory, ".fbu")
        
        if (!appStorageDirectory.exists()) {
            appStorageDirectory.mkdirs()
        }
        
        return appStorageDirectory
    }

    fun getProfilesDirectory(): File {
        val profilesDirectory: File = File(getAppStorageDirectory(), "profiles")
        
        if (!profilesDirectory.exists()) {
            profilesDirectory.mkdirs()
        }
        
        return profilesDirectory
    }
}
