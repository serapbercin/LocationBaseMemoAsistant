package com.sap.codelab


import com.example.data.Repository
import com.example.notification.MemoDetails
import com.example.notification.MemoDetailsProvider

class AppMemoDetailsProvider : MemoDetailsProvider {
    override suspend fun getDetails(memoId: Long): MemoDetails? {
        val memo = Repository.getMemoById(memoId)
        val title = memo.title
        val preview = memo.description.take(140)
        return MemoDetails(title, preview)
    }
}
