package com.example.data

import androidx.room.Room
import android.content.Context
import androidx.annotation.WorkerThread
import com.example.domain.IMemoRepository
import com.example.domain.Memo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATABASE_NAME: String = "codelab"

/**
 * The repository is used to retrieve data from a data source.
 */
object Repository : IMemoRepository {

    private lateinit var database: Database

    fun initialize(applicationContext: Context) {
        database =
            Room.databaseBuilder(applicationContext, Database::class.java, DATABASE_NAME).build()
    }

    override suspend fun saveMemo(memo: Memo) {
        database.getMemoDao().insert(memo.toEntity())
    }

    override suspend fun getOpen(): List<Memo> =
        database.getMemoDao().getOpen().map { it.toDomain() }

    @WorkerThread
    override fun getAll(): List<Memo> = database.getMemoDao().getAll().map { it.toDomain() }

    override suspend fun getMemoById(id: Long): Memo =
        database.getMemoDao().getMemoById(id).toDomain()

    override suspend fun saveMemoAndReturnId(memo: Memo): Long =
        database.getMemoDao().insert(memo.toEntity())

    override fun observeAll(): Flow<List<Memo>> {
        return database.getMemoDao().observeAll()
            .map { entityList -> entityList.map { it.toDomain() } }
    }

    override suspend fun updateMemo(memo: Memo): Int = database.getMemoDao().update(memo.toEntity())

}