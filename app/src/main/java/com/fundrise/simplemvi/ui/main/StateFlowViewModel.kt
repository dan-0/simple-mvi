package com.fundrise.simplemvi.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StateFlowViewModel : ViewModel() {

    private val stateFlow = MutableStateFlow("0")

    val channel = Channel<String>(1)

    init {
        val f = channel.consumeAsFlow()
    }

    val stateFlowState: LiveData<String> = stateFlow.map {

        if (it.toInt() % 2 == 0) {
            "even $it"
        } else {
            "odd $it"
        }
    }
        .asLiveData()

    val actualFlow: Flow<String> = stateFlow.onEach {
        Log.d("STATE_FLOW_M", "m: 1 ${Thread.currentThread().name}")
    }.flowOn(Dispatchers.Main)
        .onEach {
            Log.d("STATE_FLOW_M", "m: 2 ${Thread.currentThread().name}")
        }
        .flowOn(Dispatchers.IO)

    val distinctFlow: Flow<String> = stateFlow.onEach {
        Log.d("STATE_FLOW_OE", "oe: $it")
    }

    fun start() {
        Channel<String>(1).consumeAsFlow().asLiveData()
        BroadcastChannel<String>(1)
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("STATE_FLOW", "VM launch thread, ${Thread.currentThread().name}")
            var counter = 0
            var subCounter = true
            while (true) {
                delay(5000)
                val counterValue = if (subCounter) {
                    counter++
                } else {
                    counter
                }
                subCounter = !subCounter
                stateFlow.value = "$counterValue"
            }
        }
    }
}