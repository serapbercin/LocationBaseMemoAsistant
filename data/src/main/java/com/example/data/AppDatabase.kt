package com.example.data

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * That database that is used to store information.
 */
@Database(entities = [MemoEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun memoDao(): MemoDao
}