package com.fundrise.simplemvi.ui.main

/**
 * Represents Intent coming from the View Layer.
 *
 * Note, this do not map directly to a interaction,
 * rather the desired "intent" of an action. So if the user taps "Increment Times Clicked", the function
 * in the actor that would handle this would be "tapIncrementTimesClicked", but it would resolve to the
 * actual desired programmatic result/intent to [IncrementCounter]. This helps to further separate concerns
 * between the View and Model layers as several user actions may resolve to a single intent.
 */
sealed class MainIntent(
    val analyticKey: String
) {

    /**
     * An intent originating from the User
     */
    sealed class UserIntent(key: String) : MainIntent(key) {

        /**
         * Intent to initialize the UX with the given input
         */
        data class Initialize(val initialValue: Int) : UserIntent("main_initialize")

        /**
         * Intent to Refresh the UX
         */
        object Refresh : UserIntent("main_refresh")

        /**
         * Intent to increment the counter shown in the UX
         */
        object IncrementCounter : UserIntent("main_increment_counter")
    }

    /**
     * An intent originating from the system
     */
    sealed class SystemIntent(key: String) : MainIntent(key) {
        /**
         * A system initiated intent
         */
        object SystemLoading : SystemIntent("main_system_initiated")
    }
}
