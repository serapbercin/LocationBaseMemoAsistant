package com.example.featurememo

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.Memo
import com.example.domain.usecases.DeleteMemoUseCase
import com.example.domain.usecases.ObserveAllMemosUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * Depend on use cases, not repositories, to keep app -> domain only.
 * Replace the constructor params with your real use-cases.
 */
class HomeViewModel @Inject constructor(
    observeMemos: ObserveAllMemosUseCase,
    private val deleteMemo: DeleteMemoUseCase,
) : ViewModel() {

    private val _effects = MutableSharedFlow<Effect>(extraBufferCapacity = 1)
    val effects: Flow<Effect> = _effects

    sealed interface Effect {
        data class NavigateToEdit(val id: Long?) : Effect
        data class ShowMessage(@StringRes val resId: Int) : Effect
    }

    val state: StateFlow<HomeUiState> =
        observeMemos()
            .map { HomeUiState(isLoading = false, memos = it) }
            .onStart { emit(HomeUiState(isLoading = true)) }
            .catch { e -> emit(HomeUiState(isLoading = false, error = e.message)) }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                HomeUiState(isLoading = true)
            )

    fun onAddClicked() {
        _effects.tryEmit(Effect.NavigateToEdit(null))
    }

    fun onMemoClicked(id: Long) {
        _effects.tryEmit(Effect.NavigateToEdit(id))
    }

    fun onDeleteClicked(id: Long) {
        viewModelScope.launch {
            runCatching { deleteMemo(id) }
                .onSuccess { _effects.tryEmit(Effect.ShowMessage(R.string.msg_delete_success)) }
                .onFailure { _effects.tryEmit(Effect.ShowMessage(R.string.msg_delete_failed)) }
        }
    }
}

data class HomeUiState(
    val isLoading: Boolean = true,
    val memos: List<Memo> = emptyList(),
    val error: String? = null
)
