package com.sap.codelab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.sap.codelab.model.Memo
import com.sap.codelab.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditMemoViewModel : ViewModel() {
    private val _ui = MutableStateFlow(EditUiState())
    val ui = _ui.asStateFlow()

    fun load(id: Long) = viewModelScope.launch {
        val m = Repository.getMemoById(id)
        _ui.value = EditUiState(
            id = m.id,
            title = m.title,
            description = m.description,
            location = LatLng(m.reminderLatitude / 1e7, m.reminderLongitude / 1e7)
        )
    }

    fun onTitle(v: String) = _ui.update { it.copy(title = v) }
    fun onDescription(v: String) = _ui.update { it.copy(description = v) }
    fun onLocation(latLng: LatLng) = _ui.update { it.copy(location = latLng) }

    fun save(onDone: () -> Unit) = viewModelScope.launch {
        val s = _ui.value
        if (!s.isValid()) return@launch
        val updated = Memo(
            id = s.id,
            title = s.title.trim(),
            description = s.description.trim(),
            reminderDate = System.currentTimeMillis(), // or keep old date if you prefer
            reminderLatitude = (s.location!!.latitude * 1e7).toLong(),
            reminderLongitude = (s.location.longitude * 1e7).toLong(),
            isDone = false
        )
        Repository.updateMemo(updated)
        onDone()
    }
}

data class EditUiState(
    val id: Long = 0L,
    val title: String = "",
    val description: String = "",
    val location: LatLng? = null
) {
    fun isValid() = title.isNotBlank() && description.isNotBlank() && location != null
}
