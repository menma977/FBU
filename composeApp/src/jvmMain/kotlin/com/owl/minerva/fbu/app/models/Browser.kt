package com.owl.minerva.fbu.app.models

import com.owl.minerva.fbu.cores.interfaces.ModelInterface

data class Browser(
    override val id: Long = 0L,
    val name: String,
    val path: String,
): ModelInterface
