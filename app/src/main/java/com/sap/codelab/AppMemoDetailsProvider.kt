package com.sap.codelab

import com.example.core.config.MemoConfig
import com.example.domain.usecases.GetMemoByIdUseCase
import com.example.notification.MemoDetails
import com.example.notification.MemoDetailsProvider
import javax.inject.Inject

class AppMemoDetailsProvider @Inject constructor(private val getMemoByIdUseCase: GetMemoByIdUseCase) :
    MemoDetailsProvider {
    override suspend fun getDetails(memoId: Long): MemoDetails? {
        val memo = getMemoByIdUseCase(memoId)
        val title = memo.title
        val preview = memo.description.take(MemoConfig.PREVIEW_MAX_LENGTH)
        return MemoDetails(title, preview)
    }
}
