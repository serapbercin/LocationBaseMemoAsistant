package com.sap.codelab.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sap.codelab.model.Memo
import kotlinx.coroutines.flow.Flow

/**
 * The Dao representation of a Memo.
 */
@Dao
internal interface MemoDao {

    /**
     * @return all memos that are currently in the database.
     */
    @Query("SELECT * FROM memo")
    fun getAll(): List<Memo>

    @Query("SELECT * FROM memo ORDER BY id DESC")
    fun observeAll(): Flow<List<Memo>>

    /**
     * @return all memos that are currently in the database and have not yet been marked as "done".
     */
    @Query("SELECT * FROM memo WHERE isDone = 0")
    suspend fun getOpen(): List<Memo>

    /**
     * Inserts the given Memo into the database. We currently do not support updating of memos.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(memo: Memo): Long

    @Update
    suspend fun update(memo: Memo): Int

    /**
     * @return the memo whose id matches the given id.
     */
    @Query("SELECT * FROM memo WHERE id = :memoId")
    suspend fun getMemoById(memoId: Long): Memo
}