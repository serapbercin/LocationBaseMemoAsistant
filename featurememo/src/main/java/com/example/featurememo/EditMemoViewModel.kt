package com.example.featurememo

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.IMemoRepository
import com.example.domain.Memo
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditMemoViewModel(private val repo: IMemoRepository) : ViewModel() {
    private val _ui = MutableStateFlow(EditUiState())
    val ui = _ui.asStateFlow()

    fun load(id: Long) = viewModelScope.launch {
        val m = repo.getMemoById(id)
        _ui.value = EditUiState(
            id = m.id!!,
            title = m.title,
            description = m.description,
            location = LatLng(m.reminderLatitude / 1e7, m.reminderLongitude / 1e7)
        )
    }

    fun onTitle(v: String) = _ui.update { it.copy(title = v) }
    fun onDescription(v: String) = _ui.update { it.copy(description = v) }
    fun onLocation(latLng: LatLng) = _ui.update { it.copy(location = latLng) }

    fun save(onDone: () -> Unit) = viewModelScope.launch {
        val value = _ui.value
        if (!value.isValid()) return@launch
        Log.d("CreateMemoViewModel2", "SERAP location: ${value.location}")
        val updated = Memo(
            id = value.id,
            title = value.title.trim(),
            description = value.description.trim(),
            reminderDate = System.currentTimeMillis(), // or keep old date if you prefer
            reminderLatitude = (value.location!!.latitude * 1e7).toLong(),
            reminderLongitude = (value.location.longitude * 1e7).toLong(),
            isDone = false
        )
        repo.updateMemo(updated)
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
