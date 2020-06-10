package com.fundrise.simplemvi.ui.main

/**
 * Resolves view layer Actions to Intents and reduces coupling between the Model and View layers
 *
 * @property intentHandler Handles resolved intents.
 */
class MainActor(
    val intentHandler: (MainIntent) -> Unit
) {
    fun tapClickMe() {
        val intent = MainIntent.IncrementCounter
        intentHandler(intent)
    }

    fun tapErrorRefresh() {
        val intent = MainIntent.Refresh
        intentHandler(intent)
    }

    fun initialize(initValue: Int) {
        val intent = MainIntent.Initialize(initValue)
        intentHandler(intent)
    }
}