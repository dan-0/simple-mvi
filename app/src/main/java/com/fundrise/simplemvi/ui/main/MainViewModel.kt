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
class MainViewModel : ViewModel() {
    private val _state = MutableLiveData<MainState>(MainState.Init)
    val state: LiveData<MainState> = _state

    private var timesClicked: Int = -1

    /**
     * Receives and handles [intent]s from the view layer
     */
    fun handleIntent(intent: MainIntent) {

        updateState(MainState.Loading)

        viewModelScope.launch(Dispatchers.IO) {
            val newState: MainState = when (intent) {
                is MainIntent.Initialize -> doInitialization(intent)
                MainIntent.Refresh -> handleRefresh()
                MainIntent.IncrementCounter -> handleIncrementCounter()
            }

            updateState(newState)
        }
    }

    /**
     * Initialize the ViewModel with a value received from the view layer
     */
    private fun doInitialization(intent: MainIntent.Initialize): MainState {
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

    private fun updateState(newState: MainState) {
        Log.d("MVIDEMO_VM", "Handling new state: ${newState::class.simpleName}")
        _state.postValue(newState)
    }
}

