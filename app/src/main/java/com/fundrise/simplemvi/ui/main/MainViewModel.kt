package com.fundrise.simplemvi.ui.main

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Simple view model that holds and dictates state based on received Intents
 */
class MainViewModel : ViewModel() {

    private val numberRepo = NumberRepo()

    private val _state = MediatorLiveData<MainState>().apply {
        value = MainState.Init

        addSource(numberRepo.dataSource) {
            val newState = when (it) {
                NumberRepoState.Error -> MainState.Error
                is NumberRepoState.Content -> MainState.Content(it.value)
            }

            updateState(newState)
        }
    }
    val state: LiveData<MainState> = _state

    fun handleClick() {
        /*
            Only add a coroutine exception handler here if you do not trust the repo,
            otherwise the repo will not able to use it's own exception handlers
         */
        viewModelScope.launch(Dispatchers.IO) {
            numberRepo.fetchNumber()
        }
    }

    private fun updateState(newState: MainState) {
        Log.d("MVIDEMO_VM", "Handling new state: ${newState::class.simpleName}")
        _state.postValue(newState)
    }
}

