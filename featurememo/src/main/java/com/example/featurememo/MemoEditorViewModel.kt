package com.example.featurememo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecases.GetMemoByIdUseCase
import com.example.domain.usecases.SaveMemoUseCase
import com.example.domain.usecases.UpdateMemoUseCase
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


class MemoEditorViewModel @Inject constructor(
    private val getMemoByIdUseCase: GetMemoByIdUseCase,
    private val saveMemoUseCase: SaveMemoUseCase,
    private val updateMemoUseCase: UpdateMemoUseCase,
) : ViewModel() {

    private val _effects = MutableSharedFlow<Effect>(extraBufferCapacity = 1)
    val effects: Flow<Effect> = _effects

    private val _uiStateFlow = MutableStateFlow(MemoEditorUi())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    fun initialize(memoId: Long?) {
        if (_uiStateFlow.value.initialized) return
        if (memoId == null || memoId <= 0L) {
            _uiStateFlow.value = MemoEditorUi(initialized = true, mode = EditorMode.Create)
            return
        }
        viewModelScope.launch {
            val m = getMemoByIdUseCase(memoId)
            _uiStateFlow.value = MemoEditorUi(
                initialized = true,
                mode = EditorMode.Edit(memoId),
                title = m.title,
                description = m.description,
                location = LatLng(m.reminderLatitude / 1e7, m.reminderLongitude / 1e7)
            )
        }
    }

    fun onTitleChanged(v: String) {
        _uiStateFlow.value = _uiStateFlow.value.copy(title = v)
    }

    fun onDescriptionChanged(v: String) {
        _uiStateFlow.value = _uiStateFlow.value.copy(description = v)
    }

    fun onLocationChanged(latLng: LatLng) {
        _uiStateFlow.value = _uiStateFlow.value.copy(location = latLng)
    }

    fun isValid(): Boolean =
        _uiStateFlow.value.let { it.title.isNotBlank() && it.description.isNotBlank() && it.location != null }

    fun submit(onCreated: (Long) -> Unit, onUpdated: () -> Unit) = viewModelScope.launch {
        val uiStateValue = _uiStateFlow.value
        val loc = uiStateValue.location ?: return@launch
        when (val mode = uiStateValue.mode) {
            EditorMode.Create -> {
                val id = saveMemoUseCase(
                    title = uiStateValue.title,
                    description = uiStateValue.description,
                    lat = loc.latitude,
                    lng = loc.longitude
                )
                onCreated(id)
                _uiStateFlow.value = MemoEditorUi(initialized = true, mode = EditorMode.Create)
            }

            is EditorMode.Edit -> {
                runCatching {
                    updateMemoUseCase(
                        id = mode.id,
                        title = uiStateValue.title,
                        description = uiStateValue.description,
                        lat = loc.latitude,
                        lng = loc.longitude
                    )
                }.onSuccess { updated ->
                    if (updated) onUpdated()
                    else _effects.emit(Effect.ShowMessage(R.string.msg_update_failed))
                }.onFailure {
                    _effects.emit(Effect.ShowMessage(R.string.msg_update_failed))
                }
            }
        }
    }
}

data class MemoEditorUi(
    val initialized: Boolean = false,
    val mode: EditorMode = EditorMode.Create,
    val title: String = "",
    val description: String = "",
    val location: LatLng? = null
)

sealed class EditorMode {
    data object Create : EditorMode()
    data class Edit(val id: Long) : EditorMode()
}


sealed interface Effect {
    data class CloseScreen(val id: Long?) : Effect
    data class ShowMessage(val resId: Int) : Effect
}