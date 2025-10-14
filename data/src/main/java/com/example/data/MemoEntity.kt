package com.example.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memo")
data class MemoEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "reminderDate")
    val reminderDate: Long,
    @ColumnInfo(name = "reminderLatitude")
    val reminderLatitude: Long,
    @ColumnInfo(name = "reminderLongitude")
    val reminderLongitude: Long,
    @ColumnInfo(name = "isDone")
    val isDone: Boolean = false
)
