package com.owl.minerva.fbu.cores.abstracts

import com.owl.minerva.fbu.cores.interfaces.ModelInterface
import com.owl.minerva.fbu.cores.interfaces.RepositoryInterface

abstract class ServiceAbstract<T : ModelInterface>(protected val repository: RepositoryInterface<T>) {
    open suspend fun index(): List<T> = repository.getAll()
    open suspend fun show(id: Long): T? = repository.getById(id)
    open suspend fun store(model: T): Long = repository.insert(model)
    open suspend fun update(id: Long, model: T): Boolean = repository.update(id, model)
    open suspend fun delete(id: Long): Boolean = repository.delete(id)
}