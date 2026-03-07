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

        val appStorageDirectory = File(userHomeDirectory, ".fbu")

        if (!appStorageDirectory.exists()) {
            appStorageDirectory.mkdirs()
        }

        return appStorageDirectory
    }

    fun getProfilesDirectory(): File {
        val profilesDirectory = File(getAppStorageDirectory(), "profiles")

        if (!profilesDirectory.exists()) {
            profilesDirectory.mkdirs()
        }

        return profilesDirectory
    }

    fun getBrowserBinaryDirectory(): File {
        val binDirectory = File(getAppStorageDirectory(), "bin")

        if (!binDirectory.exists()) {
            binDirectory.mkdirs()
        }

        return binDirectory
    }

    fun getMasterBrowserDirectory(): File {
        val masterDirectory = File(getBrowserBinaryDirectory(), "master")

        if (!masterDirectory.exists()) {
            masterDirectory.mkdirs()
        }

        return masterDirectory
    }
}
