package com.misaka.hoshinoschedule.ui.settings.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.misaka.hoshinoschedule.R
import com.misaka.hoshinoschedule.data.model.PeriodDefinition
import com.misaka.hoshinoschedule.ui.settings.PeriodEditInput

@Composable
fun PeriodEditorRow(
    period: PeriodDefinition,
    onUpdate: (PeriodEditInput) -> Unit,
    onRemove: (Int) -> Unit
) {
    var startMinutes by remember(period.id) { mutableStateOf(period.startMinutes) }
    var endMinutes by remember(period.id) { mutableStateOf(period.endMinutes) }
    var labelText by remember(period.id) { mutableStateOf(period.label.orEmpty()) }
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            stringResource(R.string.settings_period_title, period.sequence),
            style = MaterialTheme.typography.titleSmall
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TimePickerTextField(
                value = minutesToText(startMinutes),
                label = stringResource(R.string.settings_period_start_hint),
                onClick = { showStartPicker = true },
                modifier = Modifier.weight(1f)
            )
            TimePickerTextField(
                value = minutesToText(endMinutes),
                label = stringResource(R.string.settings_period_end_hint),
                onClick = { showEndPicker = true },
                modifier = Modifier.weight(1f)
            )
        }
        OutlinedTextField(
            value = labelText,
            onValueChange = { labelText = it },
            label = { Text(stringResource(R.string.settings_period_label_hint)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = {
                    if (startMinutes < endMinutes) {
                        onUpdate(
                            PeriodEditInput(
                                id = period.id,
                                sequence = period.sequence,
                                startHour = startMinutes / 60,
                                startMinute = startMinutes % 60,
                                endHour = endMinutes / 60,
                                endMinute = endMinutes % 60,
                                label = labelText.takeIf { it.isNotBlank() }
                            )
                        )
                    }
                },
                enabled = startMinutes < endMinutes
            ) { Text(stringResource(R.string.settings_period_apply)) }
            TextButton(onClick = { onRemove(period.sequence) }) {
                androidx.compose.material3.Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(R.string.settings_period_remove))
            }
        }
    }

    if (showStartPicker) {
        TimePickerAlertDialog(
            initialHour = startMinutes / 60,
            initialMinute = startMinutes % 60,
            onDismiss = { showStartPicker = false },
            onConfirm = { hour, minute ->
                showStartPicker = false
                val minutes = hour * 60 + minute
                startMinutes = minutes
                if (minutes >= endMinutes) {
                    endMinutes = (minutes + 5).coerceAtMost(23 * 60 + 55)
                }
            }
        )
    }

    if (showEndPicker) {
        TimePickerAlertDialog(
            initialHour = endMinutes / 60,
            initialMinute = endMinutes % 60,
            onDismiss = { showEndPicker = false },
            onConfirm = { hour, minute ->
                showEndPicker = false
                val minutes = hour * 60 + minute
                endMinutes = minutes
                if (minutes <= startMinutes) {
                    startMinutes = (minutes - 5).coerceAtLeast(0)
                }
            }
        )
    }
}
