package com.example.featurememo

object MemoRoutes {
    const val HOME = "memo/home"
    const val MAP = "memo/map"

    const val ARG_ID = "memoId"
    const val EDITOR = "editor?$ARG_ID={$ARG_ID}"

    fun editor() = "editor"                    // create
    fun editor(id: Long) = "editor?$ARG_ID=$id" // edit
}

object MemoFeature {
    const val startRoute = MemoRoutes.HOME
}