package com.fundrise.simplemvi.ui.main

import androidx.lifecycle.LiveData

interface DataRepo<Event> {
    val dataSource: LiveData<Event>
}