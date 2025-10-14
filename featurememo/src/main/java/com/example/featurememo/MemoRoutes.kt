package com.example.featurememo

object MemoRoutes {
    const val HOME = "memo/home"
    const val CREATE = "memo/create"
    const val MAP = "memo/map"
    const val EDIT = "memo/edit/{id}"
    fun edit(id: Long) = "memo/edit/$id"
}

object MemoFeature {
    const val startRoute = MemoRoutes.HOME
}