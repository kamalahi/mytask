package com.example.mytasks.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mytasks.PinStore
import kotlinx.coroutines.delay

/** How many digits the PIN has. Change this one number to use 5 or 6 instead. */
private const val PIN_LENGTH = 4

/** The three jobs this screen can be doing. */
private enum class PinMode { CREATE, CONFIRM, UNLOCK }

/**
 * The screen shown before anything else.
 *
 * First ever launch: it asks the user to invent a PIN, then type it again to confirm.
 * Every launch after that: it asks for the PIN and only then lets them in.
 *
 * @param forceSetup true when Settings sent the user here to pick a new PIN
 * @param onUnlocked called once the PIN is correct, which swaps in the main screen
 */
@Composable
fun LockScreen(
    pinStore: PinStore,
    forceSetup: Boolean = false,
    onUnlocked: () -> Unit
) {
    // Decide the starting job once, when the screen first appears.
    var mode by remember {
        mutableStateOf(
            if (forceSetup || !pinStore.hasPin()) PinMode.CREATE else PinMode.UNLOCK
        )
    }

    var typed by remember { mutableStateOf("") }        // digits entered so far
    var firstEntry by remember { mutableStateOf("") }   // remembered during CONFIRM
    var error by remember { mutableStateOf<String?>(null) }

    // LaunchedEffect re-runs every time `typed` changes. Once the PIN is full
    // it waits a moment so the last dot is actually visible, then acts.
    LaunchedEffect(typed) {
        if (typed.length < PIN_LENGTH) return@LaunchedEffect
        delay(140)

        when (mode) {
            PinMode.CREATE -> {
                firstEntry = typed
                typed = ""
                error = null
                mode = PinMode.CONFIRM
            }

            PinMode.CONFIRM -> {
                if (typed == firstEntry) {
                    pinStore.savePin(typed)
                    onUnlocked()
                } else {
                    error = "Those did not match. Try again."
                    firstEntry = ""
                    typed = ""
                    mode = PinMode.CREATE
                }
            }

            PinMode.UNLOCK -> {
                if (pinStore.checkPin(typed)) {
                    onUnlocked()
                } else {
                    error = "Wrong PIN"
                    typed = ""
                }
            }
        }
    }

    val title = when (mode) {
        PinMode.CREATE -> "Create a PIN"
        PinMode.CONFIRM -> "Type it once more"
        PinMode.UNLOCK -> "Enter your PIN"
    }

    val subtitle = when (mode) {
        PinMode.CREATE -> "$PIN_LENGTH digits. You will need it every time you open the app."
        PinMode.CONFIRM -> "Just to be sure you remember it."
        PinMode.UNLOCK -> null
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(48.dp)
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium
        )

        if (subtitle != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(32.dp))

        PinDots(filled = typed.length)

        Spacer(Modifier.height(12.dp))

        // Reserve the space even when empty, so the keypad does not jump around.
        Box(modifier = Modifier.height(24.dp), contentAlignment = Alignment.Center) {
            if (error != null) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Keypad(
            onDigit = { digit ->
                if (typed.length < PIN_LENGTH) {
                    error = null
                    typed += digit
                }
            },
            onBackspace = {
                if (typed.isNotEmpty()) typed = typed.dropLast(1)
            }
        )

        if (mode == PinMode.CONFIRM) {
            TextButton(onClick = {
                firstEntry = ""
                typed = ""
                error = null
                mode = PinMode.CREATE
            }) {
                Text("Start over")
            }
        }
    }
}

/** The row of circles that fill in as digits are typed. */
@Composable
private fun PinDots(filled: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        repeat(PIN_LENGTH) { index ->
            val isFilled = index < filled
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .then(
                        if (isFilled) {
                            Modifier.background(MaterialTheme.colorScheme.primary)
                        } else {
                            Modifier.border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = CircleShape
                            )
                        }
                    )
            )
        }
    }
}

/** The number pad: 1-9, then 0 and a backspace key. */
@Composable
private fun Keypad(
    onDigit: (String) -> Unit,
    onBackspace: () -> Unit
) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9")
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        for (row in rows) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                for (digit in row) {
                    KeypadKey(label = digit, onClick = { onDigit(digit) })
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Spacer(Modifier.size(72.dp))                       // empty slot, keeps 0 centered
            KeypadKey(label = "0", onClick = { onDigit("0") })
            KeypadKey(label = "⌫", onClick = onBackspace) // the backspace symbol
        }
    }
}

/** One round key. */
@Composable
private fun KeypadKey(label: String, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)                                  // clip first, so the
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)                       // ripple stays round
    ) {
        Text(
            text = label,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
