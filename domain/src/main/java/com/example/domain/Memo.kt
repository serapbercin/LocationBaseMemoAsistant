package com.example.domain

/**
 * Data class representing a Memo entity in the domain layer.
 *
 * @property id Unique identifier for the memo. Nullable for new memos.
 * @property title Title of the memo.
 * @property description Detailed description of the memo.
 * @property reminderDate Timestamp for when the reminder is set.
 * @property reminderLatitude Latitude coordinate for location-based reminders, scaled by 1E7 to store as a `Long`
 * @property reminderLongitude Longitude coordinate for location-based reminders,. scaled by 1E7 to store as a `Long`
 * @property isDone Flag indicating whether the memo has been completed.
 */
data class Memo(
    val id: Long? = null,
    val title: String,
    val description: String,
    val reminderDate: Long,
    val reminderLatitude: Long,
    val reminderLongitude: Long,
    val isDone: Boolean = false
)