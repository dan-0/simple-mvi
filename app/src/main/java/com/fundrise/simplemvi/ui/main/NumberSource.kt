package com.fundrise.simplemvi.ui.main

import kotlinx.coroutines.delay
import java.lang.Exception
import kotlin.random.Random

class NumberSource {

    private var number: Int = 0

    private suspend fun doRandomDelay() {
        delay(
            Random.nextLong(
                0,
                500
            )
        )
    }

    private fun doRandomException() {
        // Throw exception 1 in 5 times
        if (number % 5 == 0) {
            throw TestException()
        }
    }

    suspend fun getNumber(): Int {
        doRandomDelay()
        ++number
        doRandomException()
        return number
    }

    class TestException : Exception()
}