package com.example.data

import androidx.annotation.WorkerThread
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
        dao.insert(memo.toEntity())
    }

    override suspend fun getOpen(): List<Memo> =
        dao.getOpen().map { it.toDomain() }

    @WorkerThread
    override fun getAll(): List<Memo> = dao.getAll().map { it.toDomain() }

    override suspend fun getMemoById(id: Long): Memo =
        dao.getMemoById(id).toDomain()

    override suspend fun saveMemoAndReturnId(memo: Memo): Long =
        dao.insert(memo.toEntity())

    override fun observeAll(): Flow<List<Memo>> {
        return dao.observeAll()
            .map { entityList -> entityList.map { it.toDomain() } }
    }

    override suspend fun updateMemo(memo: Memo): Int = dao.update(memo.toEntity())

}