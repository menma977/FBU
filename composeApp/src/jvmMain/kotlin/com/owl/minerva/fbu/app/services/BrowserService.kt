package com.owl.minerva.fbu.app.services

import com.owl.minerva.fbu.app.models.Browser
import com.owl.minerva.fbu.app.repositories.BrowserRepository
import com.owl.minerva.fbu.cores.abstracts.ServiceAbstract

class BrowserService(browserRepository: BrowserRepository) : ServiceAbstract<Browser>(browserRepository)