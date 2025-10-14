package com.example.domain

/**
 * Represents a memo.
 *
 */
data class Memo(
    val id: Long? = null,
    val title: String,
    val description: String,
    val reminderDate: Long,
    val reminderLatitude: Long,   // better type than Long
    val reminderLongitude: Long,  // better type than Long
    val isDone: Boolean = false
)