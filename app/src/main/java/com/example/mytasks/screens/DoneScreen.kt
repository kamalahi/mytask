package com.example.mytasks.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mytasks.Task

/**
 * Tab 2. Everything already ticked off.
 *
 * Unticking a task here sends it straight back to the Tasks tab, because both
 * tabs read the same list and only filter it differently.
 */
@Composable
fun DoneScreen(
    tasks: List<Task>,
    onToggle: (Task) -> Unit,
    onDelete: (Task) -> Unit,
    onClearFinished: () -> Unit
) {
    val finished = tasks.filter { task -> task.done }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        if (finished.isEmpty()) {
            EmptyMessage("Nothing finished yet.\nTick something off on the Tasks tab.")
            return@Column   // nothing else to draw, so leave the function here
        }

        Text(
            text = "${finished.size} finished",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)   // take the space left over above the button
        ) {
            items(items = finished, key = { task -> task.id }) { task ->
                TaskRow(
                    task = task,
                    onToggle = { onToggle(task) },
                    onDelete = { onDelete(task) }
                )
            }
        }

        OutlinedButton(
            onClick = onClearFinished,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            Text("Clear finished")
        }
    }
}
