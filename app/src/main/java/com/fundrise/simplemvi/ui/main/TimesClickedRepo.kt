package com.fundrise.simplemvi.ui.main

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlin.random.Random

class TimesClickedRepo() {
    private var timesClicked: Int = -1

    private val channel = Channel<TimesClickedRepoState>(Channel.CONFLATED)
    val dataSource: Flow<TimesClickedRepoState> = channel.consumeAsFlow()

    suspend fun incrementCounter(value: Int? = null) {
        value?.let { timesClicked = it }
        val newState = handleIncrementCounter()
        channel.send(newState)
    }

    suspend fun refresh() {
        val newState = TimesClickedRepoState.Content(timesClicked)
        channel.send(newState)
    }

    /**
     * Handle incrementing the counter
     */
    private suspend fun handleIncrementCounter(): TimesClickedRepoState {

        delay(1000)

        val isRandomError = Random.nextInt() % 5 == 0

        return if (isRandomError) {
            TimesClickedRepoState.Error
        } else {
            timesClicked++
            TimesClickedRepoState.Content(timesClicked)
        }
    }
}