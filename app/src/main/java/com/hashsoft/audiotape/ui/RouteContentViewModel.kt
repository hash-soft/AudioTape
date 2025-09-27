package com.hashsoft.audiotape.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RouteContentViewModel @Inject constructor(private val _controller: AudioController) :
    ViewModel() {

    fun buildController(context: Context) = _controller.buildController(context)
    fun releaseController() = _controller.releaseController()

}
