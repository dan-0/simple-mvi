package com.fundrise.simplemvi.ui.main

sealed class NumberRepoState {
    object Error : NumberRepoState()
    data class Content(val value: Int) : NumberRepoState()
}