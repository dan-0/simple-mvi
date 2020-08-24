package com.fundrise.simplemvi.ui.main

/**
 * Resolves view layer Actions to Intents and reduces coupling between the Model and View layers
 *
 * @property intentHandler Handles resolved intents.
 */
class MainActor(
    val intentHandler: (MainIntent.UserIntent) -> Unit
) {
    fun tapClickMe() {
        val intent = MainIntent.UserIntent.IncrementCounter
        intentHandler(intent)
    }

    fun tapErrorRefresh() {
        val intent = MainIntent.UserIntent.Refresh
        intentHandler(intent)
    }

    fun initialize(initValue: Int) {
        val intent = MainIntent.UserIntent.Initialize(initValue)
        intentHandler(intent)
    }
}