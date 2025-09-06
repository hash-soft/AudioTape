package com.hashsoft.audiotape.ui

sealed interface AudioCallbackResult {

    data object None : AudioCallbackResult
    data class Position(
        val position: Long,
    ) : AudioCallbackResult

}