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

class CreateMemoViewModel2(private val repo: IMemoRepository) : ViewModel() {

    private val _ui = MutableStateFlow(CreateMemoUiState())
    val ui = _ui.asStateFlow()

    fun onTitleChanged(v: String) = _ui.update { it.copy(title = v) }
    fun onDescriptionChanged(v: String) = _ui.update { it.copy(description = v) }
    fun updateLocation(lat: Double, lng: Double) =
        _ui.update { it.copy(location = LatLng(lat, lng)) }

    fun isValid(): Boolean = with(_ui.value) {
        title.isNotBlank() && description.isNotBlank() && location != null
    }

    fun saveMemo(
        onSaved: (savedId: Long) -> Unit
    ) = viewModelScope.launch {
        val value = _ui.value
        Log.d("CreateMemoViewModel2", "SERAP location: ${value.location}")
        val memo = Memo(
            id = 0,
            title = value.title.trim(),
            description = value.description.trim(),
            reminderDate = System.currentTimeMillis(),
            reminderLatitude = (value.location!!.latitude).toE7(),
            reminderLongitude = (value.location.longitude).toE7(),
            isDone = false
        )
        // Prefer changing DAO insert to return Long; if not, add a repository method for it.
        val id = repo.saveMemoAndReturnId(memo) // you add this (see note below)
        onSaved(id)
        _ui.update { CreateMemoUiState() } // reset if you want
    }
}

data class CreateMemoUiState(
    val title: String = "",
    val description: String = "",
    val location: LatLng? = null
)

private const val E7 = 1e7
private fun Double.toE7(): Long = (this * E7).toLong()
private fun Long.fromE7(): Double = this / E7