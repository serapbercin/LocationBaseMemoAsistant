package com.sap.codelab

import com.example.domain.MemoRepositoryApi
import com.example.notification.MemoDetails
import com.example.notification.MemoDetailsProvider
import javax.inject.Inject

class AppMemoDetailsProvider @Inject constructor(private val repo: MemoRepositoryApi) :
    MemoDetailsProvider {
    override suspend fun getDetails(memoId: Long): MemoDetails? {
        val memo = repo.getMemoById(memoId)
        val title = memo.title
        val preview = memo.description.take(140)
        return MemoDetails(title, preview)
    }
}
