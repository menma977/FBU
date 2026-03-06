package com.owl.minerva.fbu.cores.interfaces

interface RepositoryInterface<T: ModelInterface> {
    suspend fun getAll(): List<T>
    suspend fun getById(id: Long): T?
    suspend fun insert(model: T): Long
    suspend fun update(id: Long, model: T): Boolean
    suspend fun delete(id: Long): Boolean
}