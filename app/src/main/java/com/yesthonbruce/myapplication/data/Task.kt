package com.yesthonbruce.myapplication.data

import com.google.firebase.Timestamp

data class Task(
    val id: String = "",
    val name: String = "",    val timestamp: Timestamp = Timestamp.now(),
    val isCompleted: Boolean = false
)
