package com.example.domain.usecases

import com.example.domain.Memo
import com.example.domain.MemoRepositoryApi
import javax.inject.Inject

class GetMemoByIdUseCase @Inject constructor(
    private val repo: MemoRepositoryApi
) {
    suspend operator fun invoke(id: Long): Memo = repo.getMemoById(id)
}
