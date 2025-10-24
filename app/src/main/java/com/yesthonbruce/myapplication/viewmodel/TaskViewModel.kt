package com.yesthonbruce.myapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.yesthonbruce.myapplication.data.Task
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> = _tasks

    init {
        fetchTasks()
    }

    private fun fetchTasks() {
        db.collection("tasks")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }
                val taskList = snapshots?.map { document ->
                    document.toObject(Task::class.java).copy(id = document.id)
                } ?: emptyList()
                _tasks.value = taskList
            }
    }

    fun addTask(taskName: String) {
        if (taskName.isNotBlank()) {
            val task = Task(name = taskName)
            db.collection("tasks").add(task)
        }
    }

    fun updateTaskStatus(taskId: String, isCompleted: Boolean) {
        db.collection("tasks").document(taskId).update("completed", isCompleted)
    }

    fun deleteTask(taskId: String) {
        db.collection("tasks").document(taskId).delete()
    }

    fun formatTimestamp(date: Date): String {
        val formatter = SimpleDateFormat("dd-MM-yyyy – HH:mm", Locale.getDefault())
        return formatter.format(date)
    }
}
