package com.example.domain.usecases

import com.example.domain.Memo
import com.example.domain.MemoRepositoryApi
import javax.inject.Inject

private const val E7 = 1e7

class UpdateMemoUseCase @Inject constructor(
    private val repo: MemoRepositoryApi
) {
    suspend operator fun invoke(
        id: Long,
        title: String,
        description: String,
        lat: Double,
        lng: Double,
        whenMillis: Long = System.currentTimeMillis()
    ): Boolean {
        require(id > 0 && title.isNotBlank() && description.isNotBlank())
        val memo = Memo(
            id = id,
            title = title.trim(),
            description = description.trim(),
            reminderDate = whenMillis,
            reminderLatitude = (lat * E7).toLong(),
            reminderLongitude = (lng * E7).toLong(),
            isDone = false
        )
        return repo.updateMemo(memo) > 0
    }
}