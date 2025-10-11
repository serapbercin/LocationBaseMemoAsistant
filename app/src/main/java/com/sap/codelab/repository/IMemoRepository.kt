package com.sap.codelab.repository

import com.sap.codelab.model.Memo
import kotlinx.coroutines.flow.Flow

/**
 * Interface for a repository offering memo related CRUD operations.
 */
internal interface IMemoRepository {

    /**
     * Saves the given memo to the database.
     */
    suspend fun saveMemo(memo: Memo)

    /**
     * @return all memos currently in the database.
     */
    fun getAll(): List<Memo>

    /**
     * @return all memos currently in the database, except those that have been marked as "done".
     */
    suspend fun getOpen(): List<Memo>

    /**
     * @return the memo whose id matches the given id.
     */
    suspend fun getMemoById(id: Long): Memo

    suspend fun saveMemoAndReturnId(memo: Memo): Long

    fun observeAll(): Flow<List<Memo>>
    suspend fun updateMemo(memo: Memo): Int
}