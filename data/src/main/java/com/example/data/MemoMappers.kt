package com.example.data

import com.example.domain.Memo

fun MemoEntity.toDomain(): Memo =
    Memo(
        id = this.id,
        title = this.title,
        description = this.description,
        reminderDate = this.reminderDate,
        reminderLatitude = this.reminderLatitude,
        reminderLongitude = this.reminderLongitude,
        isDone = this.isDone
    )

fun Memo.toEntity(): MemoEntity =
    MemoEntity(
        id = this.id ?: 0L, // Room will auto-generate if 0
        title = this.title,
        description = this.description,
        reminderDate = this.reminderDate,
        reminderLatitude = this.reminderLatitude,
        reminderLongitude = this.reminderLongitude,
        isDone = this.isDone
    )
