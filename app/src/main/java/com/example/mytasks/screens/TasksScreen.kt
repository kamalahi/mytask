package com.example.mytasks.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.mytasks.Task

/**
 * Tab 1. The box for adding a task, plus everything not finished yet.
 *
 * It holds no task data of its own. The list arrives as a parameter and every
 * change is reported back up through the three lambdas. That pattern is called
 * state hoisting, and it is why all three tabs stay in step automatically.
 */
@Composable
fun TasksScreen(
    tasks: List<Task>,
    doneCount: Int,
    onAdd: (String) -> Unit,
    onToggle: (Task) -> Unit,
    onDelete: (Task) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var newTaskText by remember { mutableStateOf("") }

    // Only the unfinished ones belong on this tab.
    val active = tasks.filter { task -> !task.done }

    fun submit() {
        val text = newTaskText.trim()
        if (text.isEmpty()) return
        onAdd(text)
        newTaskText = ""
        focusManager.clearFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = newTaskText,
                onValueChange = { newTaskText = it },
                placeholder = { Text("Add a task") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { submit() }),
                modifier = Modifier.weight(1f)
            )

            FilledIconButton(onClick = { submit() }) {
                Icon(Icons.Default.Add, contentDescription = "Add task")
            }
        }

        if (tasks.isNotEmpty()) {
            Text(
                text = "$doneCount of ${tasks.size} done",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 16.dp)
            )
            LinearProgressIndicator(
                progress = { doneCount.toFloat() / tasks.size },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp, bottom = 12.dp)
            )
        }

        if (active.isEmpty()) {
            EmptyMessage(
                if (tasks.isEmpty()) "Nothing here yet.\nType something above to get started."
                else "All done. Nice."
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(items = active, key = { task -> task.id }) { task ->
                    TaskRow(
                        task = task,
                        onToggle = { onToggle(task) },
                        onDelete = { onDelete(task) }
                    )
                }
            }
        }
    }
}
