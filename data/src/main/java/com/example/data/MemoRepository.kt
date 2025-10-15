package com.example.data

import com.example.domain.MemoRepositoryApi
import com.example.domain.Memo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


/**
 * The repository is used to retrieve data from a data source.
 */
@Singleton
class MemoRepository @Inject constructor(
    private val appDatabase: AppDatabase
) : MemoRepositoryApi {

    private val dao get() = appDatabase.memoDao()

    override suspend fun saveMemo(memo: Memo) {
        dao.upsert(memo.toEntity())
    }

    override suspend fun saveMemoAndReturnId(memo: Memo): Long =
        dao.upsert(memo.toEntity())

    override suspend fun getAll(): List<Memo> = dao.getAll().map { it.toDomain() }

    override suspend fun getMemoById(id: Long): Memo =
        dao.getMemoById(id)?.toDomain()
            ?: throw NoSuchElementException("Memo(id=$id) not found")


    override fun observeAll(): Flow<List<Memo>> {
        return dao.observeAll().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun updateMemo(memo: Memo): Int = dao.update(memo.toEntity())

    override suspend fun deleteById(id: Long): Int = dao.deleteById(id)
}