package com.fundrise.simplemvi.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Simple view model that holds and dictates state based on received Intents
 */
class MainViewModel(
    private val analytics: Analytics = Analytics()
) : ViewModel() {
    private val _state = MutableLiveData<MainState>(MainState.Init)
    val state: LiveData<MainState> = _state

    private var timesClicked: Int = -1

    /**
     * Receives and handles [intent]s from the view layer
     */
    fun handleIntent(intent: MainIntent.UserIntent) {

        updateState(MainState.Loading, MainIntent.SystemIntent.SystemLoading)

        viewModelScope.launch(Dispatchers.IO) {
            val newState: MainState = when (intent) {
                is MainIntent.UserIntent.Initialize -> doInitialization(intent)
                MainIntent.UserIntent.Refresh -> handleRefresh()
                MainIntent.UserIntent.IncrementCounter -> handleIncrementCounter()
            }

            updateState(newState, intent)
        }
    }

    /**
     * Initialize the ViewModel with a value received from the view layer
     */
    private fun doInitialization(intent: MainIntent.UserIntent.Initialize): MainState {
        timesClicked = intent.initialValue
        return MainState.Content(timesClicked)
    }

    /**
     * Handle the intent to refresh the view.
     */
    private fun handleRefresh(): MainState {
        return MainState.Content(timesClicked)
    }

    /**
     * Handle incrementing the counter
     */
    private suspend fun handleIncrementCounter(): MainState {

        delay(1000)

        val isRandomError = Random.nextBoolean()

        return if (isRandomError) {
            MainState.Error
        } else {
            timesClicked++
            MainState.Content(timesClicked)
        }
    }

    private fun updateState(newState: MainState, originatingIntent: MainIntent) {
        Log.d("MVIDEMO_VM", "Handling new state: ${newState::class.simpleName}")

        trackStateChange(newState, originatingIntent)

        _state.postValue(newState)
    }

    private fun trackStateChange(
        newState: MainState,
        originatingIntent: MainIntent
    ) {
        when (newState) {
            MainState.Init -> {
                /* Stub; analytics for initialized values/states are better done in an [init] block */
            }
            MainState.Loading -> {
                /* Stub; this is a transitional event and does not normally need to be tracked, but can be */
            }
            MainState.Error -> {
                val props = mapOf(
                    "user_action" to originatingIntent.analyticKey
                )
                analytics.trackEvent(UiEvent.ERROR_DISPLAYED, props)
            }
            is MainState.Content -> {
                val props = mapOf(
                    "user_action" to originatingIntent.analyticKey,
                    "times_clicked" to newState.timesClicked.toString()
                )
                analytics.trackEvent(UiEvent.CONTENT, props)
            }
        }
    }
}

