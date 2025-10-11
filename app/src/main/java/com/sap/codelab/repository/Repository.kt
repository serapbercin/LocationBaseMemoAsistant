package com.sap.codelab.repository

import androidx.room.Room
import android.content.Context
import androidx.annotation.WorkerThread
import com.sap.codelab.model.Memo
import kotlinx.coroutines.flow.Flow

private const val DATABASE_NAME: String = "codelab"

/**
 * The repository is used to retrieve data from a data source.
 */
internal object Repository : IMemoRepository {

    private lateinit var database: Database

    fun initialize(applicationContext: Context) {
        database =
            Room.databaseBuilder(applicationContext, Database::class.java, DATABASE_NAME).build()
    }

    override suspend fun saveMemo(memo: Memo) {
        database.getMemoDao().insert(memo)
    }

    override suspend fun getOpen(): List<Memo> = database.getMemoDao().getOpen()

    @WorkerThread
    override fun getAll(): List<Memo> = database.getMemoDao().getAll()

    @WorkerThread
    override suspend fun getMemoById(id: Long): Memo = database.getMemoDao().getMemoById(id)

    override suspend fun saveMemoAndReturnId(memo: Memo): Long =
        database.getMemoDao().insert(memo)

    // Repository.kt (add)
    override fun observeAll(): Flow<List<Memo>> {
        return database.getMemoDao().observeAll()
    }

    override suspend fun updateMemo(memo: Memo): Int = database.getMemoDao().update(memo)

}