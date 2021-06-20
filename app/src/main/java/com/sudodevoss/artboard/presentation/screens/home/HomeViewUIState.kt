package com.sudodevoss.artboard.presentation.screens.home

sealed class HomeViewUIState {
    data class Success(val successMessage: String?) : HomeViewUIState()
    data class Error(val exception: Throwable) : HomeViewUIState()
}