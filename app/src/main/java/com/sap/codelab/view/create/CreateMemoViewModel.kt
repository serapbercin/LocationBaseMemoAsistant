package com.sap.codelab.view.create

import androidx.lifecycle.ViewModel
import com.sap.codelab.model.Memo
import com.sap.codelab.repository.Repository
import com.sap.codelab.utils.coroutines.ScopeProvider
import com.sap.codelab.utils.extensions.empty
import kotlinx.coroutines.launch

/**
 * ViewModel for matching CreateMemo view. Handles user interactions.
 */
internal class CreateMemoViewModel : ViewModel() {

    private var memo = Memo(0, String.empty(), String.empty(), 0, 0, 0, false)

    // Track whether user actually picked a location (0,0 can be valid on Earth, so keep an explicit flag)
    private var locationSet: Boolean = false

    /**
     * Saves the memo in its current state.
     * - Ensures reminderDate is set
     */
    fun saveMemo() {
        val toSave = memo.copy(
            reminderDate = if (memo.reminderDate == 0L) System.currentTimeMillis() else memo.reminderDate
        )
        ScopeProvider.application.launch {
            Repository.saveMemo(toSave)
        }
    }

    /**
     * Call this to update title/description when user edits inputs.
     */
    fun updateMemo(title: String, description: String) {
        memo = memo.copy(
            id = 0,
            title = title.trim(),
            description = description.trim()
        )
    }

    /**
     * Call this after the user picks a location on the map.
     */
    fun updateLocation(latitude: Double, longitude: Double) {
        memo = memo.copy(
            reminderLatitude = latitude.toE7(),
            reminderLongitude = longitude.toE7()
        )
        locationSet = true
    }

    fun hasLocation(): Boolean = locationSet
    /**
     * Validation helpers
     */
    fun isMemoValid(): Boolean =
        memo.title.isNotBlank() && memo.description.isNotBlank() && locationSet

    fun hasTextError(): Boolean = memo.description.isBlank()

    fun hasTitleError(): Boolean = memo.title.isBlank()

    fun hasLocationError(): Boolean = !locationSet
}

/** E7 helpers because Memo keeps lat/lng as Long */
private const val E7 = 1e7
private fun Double.toE7(): Long = (this * E7).toLong()
