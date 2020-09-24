package com.fundrise.simplemvi.ui.main

sealed class TimesClickedRepoState {
    class Content(val counter: Int) : TimesClickedRepoState()
    object Error : TimesClickedRepoState()
}