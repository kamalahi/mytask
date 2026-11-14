package com.example.mytasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.example.mytasks.screens.DoneScreen
import com.example.mytasks.screens.LockScreen
import com.example.mytasks.screens.SettingsScreen
import com.example.mytasks.screens.TasksScreen
import com.example.mytasks.ui.theme.MyTasksTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()   // draw behind the status bar, the modern full-screen look

        setContent {
            MyTasksTheme {
                AppRoot()
            }
        }
    }
}

/**
 * The gate. Shows the lock screen until the PIN is right, then the real app.
 *
 * `unlocked` is deliberately a plain remember, not rememberSaveable: rotating the
 * phone keeps it, but the app being killed and reopened resets it to false, which
 * is exactly what a lock should do.
 */
@Composable
fun AppRoot() {
    val context = LocalContext.current
    val pinStore = remember { PinStore(context) }

    var unlocked by remember { mutableStateOf(false) }
    var setupMode by remember { mutableStateOf(false) }   // true while changing the PIN

    if (unlocked) {
        MainScreen(
            onLockNow = {
                unlocked = false
                setupMode = false
            },
            onChangePin = {
                unlocked = false
                setupMode = true
            }
        )
    } else {
        LockScreen(
            pinStore = pinStore,
            forceSetup = setupMode,
            onUnlocked = {
                unlocked = true
                setupMode = false
            }
        )
    }
}

/**
 * Everything behind the lock: the three tabs and the single task list they share.
 *
 * The list lives here, one level above all three screens, so ticking a task on the
 * Tasks tab makes it appear on the Done tab with no extra work. Each screen is
 * handed the data it needs plus a lambda to report changes back.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onLockNow: () -> Unit,
    onChangePin: () -> Unit
) {
    val context = LocalContext.current
    val store = remember { TaskStore(context) }

    // mutableStateListOf is a list Compose watches, pre-filled with whatever was saved.
    val tasks = remember { mutableStateListOf<Task>().apply { addAll(store.load()) } }

    // Which tab is open: 0 Tasks, 1 Done, 2 Settings. rememberSaveable keeps it
    // through a screen rotation.
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    fun addTask(text: String) {
        tasks.add(Task(id = System.currentTimeMillis(), text = text))
        store.save(tasks)
    }

    fun toggleTask(task: Task) {
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index == -1) return
        // copy() makes a new Task with one value changed, leaving the old one alone.
        tasks[index] = tasks[index].copy(done = !tasks[index].done)
        store.save(tasks)
    }

    fun deleteTask(task: Task) {
        tasks.removeAll { it.id == task.id }
        store.save(tasks)
    }

    fun clearFinished() {
        tasks.removeAll { it.done }
        store.save(tasks)
    }

    fun deleteEverything() {
        tasks.clear()
        store.clear()
    }

    val doneCount = tasks.count { it.done }

    val title = when (selectedTab) {
        0 -> "Tasks"
        1 -> "Done"
        else -> "Settings"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.SemiBold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                    label = { Text("Tasks") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
                    label = { Text("Done") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("Settings") }
                )
            }
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .padding(innerPadding)   // keeps content clear of both bars
                .fillMaxSize()
        ) {
            // `when` picks which screen to draw. This is the whole navigation system,
            // which is plenty for three tabs that have no back stack between them.
            when (selectedTab) {
                0 -> TasksScreen(
                    tasks = tasks,
                    doneCount = doneCount,
                    onAdd = { text -> addTask(text) },
                    onToggle = { task -> toggleTask(task) },
                    onDelete = { task -> deleteTask(task) }
                )

                1 -> DoneScreen(
                    tasks = tasks,
                    onToggle = { task -> toggleTask(task) },
                    onDelete = { task -> deleteTask(task) },
                    onClearFinished = { clearFinished() }
                )

                else -> SettingsScreen(
                    totalTasks = tasks.size,
                    doneTasks = doneCount,
                    onChangePin = onChangePin,
                    onLockNow = onLockNow,
                    onDeleteAll = { deleteEverything() }
                )
            }
        }
    }
}
