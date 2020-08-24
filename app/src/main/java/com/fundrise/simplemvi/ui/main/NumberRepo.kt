package com.fundrise.simplemvi.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import com.hadilq.liveevent.LiveEvent
import kotlinx.coroutines.*

class NumberRepo(
    private val numberSource: NumberSource = NumberSource()
) : DataRepo<NumberRepoState> {

    private val _dataSource = LiveEvent<NumberRepoState>()
    override val dataSource: LiveData<NumberRepoState> = _dataSource

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("REPO_EXCEPTION_HANDLER", "Error handling fetchNumber", throwable)
        val newState = NumberRepoState.Error
        updateSource(newState)
    }

    suspend fun fetchNumber() = launch(exceptionHandler) {
        val newNumber = numberSource.getNumber()
        val newState = NumberRepoState.Content(newNumber)
        updateSource(newState)
    }

    private suspend fun DataRepo<*>.launch(exceptionHandler: CoroutineExceptionHandler, function: suspend () -> Unit) {
        supervisorScope {
            launch(exceptionHandler) {
                function()
            }
        }
    }

    private fun updateSource(newState: NumberRepoState) {
        Log.d("NumberRepo", "New state: $newState")
        _dataSource.postValue(newState)
    }
}

