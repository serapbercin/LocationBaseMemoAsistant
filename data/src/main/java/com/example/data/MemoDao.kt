package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for interacting with Memo records in the database.
 *
 * This interface defines methods for reading, observing, inserting, and updating memos.
 * All suspend functions are automatically executed off the main thread by Room.
 */
@Dao
interface MemoDao {

    /**
     * Retrieves all memos stored in the database, ordered by descending ID.
     *
     * @return a list of all [MemoEntity] objects currently in the table.
     */
    @Query("SELECT * FROM memo ORDER BY id DESC")
    suspend fun getAll(): List<MemoEntity>

    /**
     * Observes all memos in the database.
     * Emits a new list every time the underlying data changes.
     *
     * @return a [Flow] that continuously emits updated lists of memos.
     */
    @Query("SELECT * FROM memo ORDER BY id DESC")
    fun observeAll(): Flow<List<MemoEntity>>

    /**
     * Inserts a new memo into the database, or replaces the existing one
     * if a memo with the same primary key already exists.
     *
     * This is often referred to as an "upsert" operation (insert or update).
     * If the memo is replaced, the old record is deleted and reinserted with the new data.
     *
     * @param memo the [MemoEntity] to insert or replace.
     * @return the new row ID of the inserted or replaced memo.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(memo: MemoEntity): Long

    /**
     * Updates an existing memo in the database.
     *
     * Unlike [upsert], this method only affects existing rows that match
     * the entity’s primary key. If no matching row exists, no operation occurs.
     *
     * @param memo the [MemoEntity] to update.
     * @return the number of rows that were updated (0 if none matched).
     */
    @Update
    suspend fun update(memo: MemoEntity): Int

    /**
     * Retrieves the memo with the given ID, or null if it does not exist.
     *
     * @param memoId the unique identifier of the memo.
     * @return the matching [MemoEntity], or null if not found.
     */
    @Query("SELECT * FROM memo WHERE id = :memoId")
    suspend fun getMemoById(memoId: Long): MemoEntity?

    @Query("DELETE FROM memo WHERE id = :id")
    suspend fun deleteById(id: Long): Int
}

