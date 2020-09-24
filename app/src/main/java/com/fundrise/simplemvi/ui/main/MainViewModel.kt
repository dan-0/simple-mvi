package com.fundrise.simplemvi.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Simple view model that holds and dictates state based on received Intents
 */
class MainViewModel(
    private val analytics: Analytics = Analytics(),
    private val repo: TimesClickedRepo = TimesClickedRepo()
) : ViewModel() {

    private val _state = MutableStateFlow<MainState>(MainState.Init)
    val state: StateFlow<MainState> = _state

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repo.dataSource.collect {
                _state.value = when (it) {
                    is TimesClickedRepoState.Content -> MainState.Content(it.counter)
                    TimesClickedRepoState.Error -> MainState.Error
                }
            }
        }
    }

    /**
     * Receives and handles [intent]s from the view layer
     */
    fun handleIntent(intent: MainIntent.UserIntent) {

        updateState(MainState.Loading, MainIntent.SystemIntent.SystemLoading)

        viewModelScope.launch(Dispatchers.IO) {
            val newState: MainState = when (intent) {
                is MainIntent.UserIntent.Initialize -> doInitialization(intent)
                MainIntent.UserIntent.Refresh -> handleRefresh()
                MainIntent.UserIntent.IncrementCounter -> doIncrement()
            }

            updateState(newState, intent)
        }
    }

    /**
     * Initialize the ViewModel with a value received from the view layer
     */
    private fun doInitialization(intent: MainIntent.UserIntent.Initialize): MainState {
        viewModelScope.launch {
            repo.incrementCounter(intent.initialValue)
        }
        return MainState.Loading
    }

    private fun doIncrement(): MainState {
        viewModelScope.launch {
            repo.incrementCounter()
        }
        return MainState.Loading
    }

    /**
     * Handle the intent to refresh the view.
     */
    private fun handleRefresh(): MainState {
        viewModelScope.launch {
            repo.refresh()
        }
        return MainState.Loading
    }

    private fun updateState(newState: MainState, originatingIntent: MainIntent) {
        Log.d("MVIDEMO_VM", "Handling new state: ${newState::class.simpleName}")

        trackStateChange(newState, originatingIntent)

        _state.value = newState
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

