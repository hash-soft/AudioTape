package com.hashsoft.audiotape.ui

sealed interface TapeCallbackArgument {

    data class UpdateExist(val exist: Boolean) : TapeCallbackArgument

    data object CloseSelected : TapeCallbackArgument

    data object DeleteSelected : TapeCallbackArgument

}