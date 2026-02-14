package com.hashsoft.audiotape.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class AboutScreenState {
    Normal,
    HowtoUseDialog,
    PrivacyPolicyDialog
}

class AboutViewModel : ViewModel() {

    private val _screenState = MutableStateFlow(AboutScreenState.Normal)
    val screenState = _screenState.asStateFlow()

    fun setScreenState(state: AboutScreenState) {
        _screenState.update { state }
    }

}
