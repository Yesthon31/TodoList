package com.yesthonbruce.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.yesthonbruce.myapplication.data.Task
import com.yesthonbruce.myapplication.ui.theme.BuditodolistTheme
import com.yesthonbruce.myapplication.viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {
    private val taskViewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BuditodolistTheme {
                TodoApp(taskViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoApp(viewModel: TaskViewModel) {
    val tasks by viewModel.tasks.observeAsState(initial = emptyList())
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Budi's To-Do List") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Tambah Kegiatan")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (showDialog) {
                AddTaskDialog(
                    onDismiss = { showDialog = false },
                    onTaskAdded = { taskName ->
                        viewModel.addTask(taskName)
                        showDialog = false
                    }
                )
            }

            if (tasks.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    items(tasks, key = { it.id }) { task ->
                        TaskItem(
                            task = task,
                            onStatusChange = { isChecked -> viewModel.updateTaskStatus(task.id, isChecked) },
                            onDelete = { viewModel.deleteTask(task.id) },
                            formatTimestamp = { date -> viewModel.formatTimestamp(date) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onStatusChange: (Boolean) -> Unit,
    onDelete: () -> Unit,
    formatTimestamp: (java.util.Date) -> String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = onStatusChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    color = if (task.isCompleted) Color.Gray else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatTimestamp(task.timestamp.toDate()),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Hapus Kegiatan", tint = Color.Red)
            }
        }
    }
}

@Composable
fun AddTaskDialog(onDismiss: () -> Unit, onTaskAdded: (String) -> Unit) {
    var taskName by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Tambah Kegiatan Baru", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(
                    value = taskName,
                    onValueChange = { taskName = it },
                    label = { Text("Nama Kegiatan") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("Batal")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        if (taskName.isNotBlank()) {
                            onTaskAdded(taskName)
                        }
                    }) {
                        Text("Simpan")
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "Belum ada kegiatan",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
            Text(
                "Tekan tombol '+' untuk menambahkan",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}
