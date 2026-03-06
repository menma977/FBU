package com.owl.minerva.fbu.app.models

import com.owl.minerva.fbu.cores.interfaces.ModelInterface

data class Account(
    override val id: Long = 0L,
    val browserProfileId: Long,
    val label: String,
    val username: String,
    val password: String,
): ModelInterface
