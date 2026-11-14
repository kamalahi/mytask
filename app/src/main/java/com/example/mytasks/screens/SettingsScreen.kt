package com.example.mytasks.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * Tab 3. Changing the PIN, locking on demand, and wiping the data.
 *
 * @param onChangePin   sends the user back to the lock screen to pick a new PIN
 * @param onLockNow     locks the app without closing it
 * @param onDeleteAll   wipes every task, called only after the dialog is confirmed
 */
@Composable
fun SettingsScreen(
    totalTasks: Int,
    doneTasks: Int,
    onChangePin: () -> Unit,
    onLockNow: () -> Unit,
    onDeleteAll: () -> Unit
) {
    // Controls whether the "are you sure" dialog is on screen.
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Your tasks", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "$totalTasks total, $doneTasks finished",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Text("Security", style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(8.dp))

        SettingButton(
            label = "Change PIN",
            icon = Icons.Default.Refresh,
            onClick = onChangePin
        )

        Spacer(Modifier.height(8.dp))

        SettingButton(
            label = "Lock now",
            icon = Icons.Default.Lock,
            onClick = onLockNow
        )

        Spacer(Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(Modifier.height(24.dp))

        Text("Danger zone", style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(8.dp))

        Button(
            onClick = { showDeleteDialog = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Delete, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Delete all tasks")
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "My Tasks, version 1.0",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    // The dialog only exists while showDeleteDialog is true.
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete all tasks?") },
            text = { Text("All $totalTasks tasks will be gone for good. Your PIN stays as it is.") },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteAll()
                    showDeleteDialog = false
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/** A full-width outlined button with an icon, used for the settings actions. */
@Composable
private fun SettingButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(icon, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text(label)
    }
}
