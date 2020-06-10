package com.fundrise.simplemvi.ui.main

/**
 * Represents the state of the View
 */
sealed class MainState {
    object Init : MainState()

    object Loading : MainState()

    object Error : MainState()

    /**
     * Content state of the view, providing the number of [timesClicked] to the view
     */
    data class Content(
            val timesClicked: Int
    ) : MainState()
}