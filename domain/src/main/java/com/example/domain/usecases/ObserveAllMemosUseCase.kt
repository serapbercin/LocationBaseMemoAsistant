package com.example.domain.usecases

import com.example.domain.Memo
import com.example.domain.MemoRepositoryApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAllMemosUseCase @Inject constructor(
    private val repo: MemoRepositoryApi
) {
    operator fun invoke(): Flow<List<Memo>> = repo.observeAll()
}