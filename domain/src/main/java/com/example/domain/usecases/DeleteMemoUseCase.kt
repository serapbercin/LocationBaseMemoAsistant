package com.example.domain.usecases

import com.example.domain.MemoRepositoryApi
import javax.inject.Inject

class DeleteMemoUseCase @Inject constructor(private val repo: MemoRepositoryApi) {
    suspend operator fun invoke(id: Long): Int = repo.deleteById(id)
}