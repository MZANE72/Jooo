package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.RadioReminder
import com.example.model.RadioStation
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderDialog(
    reminders: List<RadioReminder>,
    stations: List<RadioStation>,
    onDismiss: () -> Unit,
    onAddReminder: (RadioReminder) -> Unit,
    onToggleReminder: (String) -> Unit,
    onDeleteReminder: (String) -> Unit
) {
    var isAddingNew by remember { mutableStateOf(false) }

    var programTitle by remember { mutableStateOf("") }
    var selectedStation by remember { mutableStateOf(stations.firstOrNull() ?: stations[0]) }
    var isStationDropdownOpen by remember { mutableStateOf(false) }

    var hour by remember { mutableIntStateOf(18) }
    var minute by remember { mutableIntStateOf(0) }
    var selectedDays by remember { mutableStateOf("Her Gün") }
    val dayOptions = listOf("Her Gün", "Hafta İçi", "Hafta Sonu", "Her Cuma", "Her Pazar")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(
                    width = 1.dp,
                    color = NeonAmber.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(24.dp)
                )
                .testTag("reminders_dialog"),
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(NeonAmber.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Alarm,
                                contentDescription = null,
                                tint = NeonAmber,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Program Hatırlatıcıları",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Kaçırmak istemediğiniz yayınlar",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_reminders_btn")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Kapat")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (!isAddingNew) {
                    // Reminders list
                    if (reminders.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "⏰", fontSize = 36.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Henüz planlanmış hatırlatıcı yok",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            reminders.forEach { reminder ->
                                ReminderCard(
                                    reminder = reminder,
                                    onToggle = { onToggleReminder(reminder.id) },
                                    onDelete = { onDeleteReminder(reminder.id) }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { isAddingNew = true },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonAmber),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("open_new_reminder_form_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Yeni Hatırlatıcı Planla",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                } else {
                    // Add new reminder form
                    Text(
                        text = "Yeni Program Alarmı",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = NeonAmber,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = programTitle,
                        onValueChange = { programTitle = it },
                        label = { Text("Program veya Yayın Başlığı") },
                        placeholder = { Text("Örn: Mehmet'in Gezegeni, Dengbêj Gecesi") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("program_title_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Station Dropdown
                    ExposedDropdownMenuBox(
                        expanded = isStationDropdownOpen,
                        onExpandedChange = { isStationDropdownOpen = !isStationDropdownOpen },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = "${selectedStation.iconEmoji} ${selectedStation.name}",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Radyo İstasyonu") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isStationDropdownOpen) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("station_select_dropdown")
                        )

                        ExposedDropdownMenu(
                            expanded = isStationDropdownOpen,
                            onDismissRequest = { isStationDropdownOpen = false }
                        ) {
                            stations.forEach { st ->
                                DropdownMenuItem(
                                    text = { Text("${st.iconEmoji} ${st.name} (${st.frequencyOrCity})") },
                                    onClick = {
                                        selectedStation = st
                                        isStationDropdownOpen = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Time Picker (Hour & Minute Selector)
                    Text(
                        text = "Yayın Saati:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Hour
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "%02d".format(hour),
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "▲",
                                        modifier = Modifier.clickable { hour = (hour + 1) % 24 },
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "▼",
                                        modifier = Modifier.clickable { hour = if (hour > 0) hour - 1 else 23 },
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Text(
                            text = ":",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // Minute
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "%02d".format(minute),
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "▲",
                                        modifier = Modifier.clickable { minute = (minute + 5) % 60 },
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "▼",
                                        modifier = Modifier.clickable { minute = if (minute >= 5) minute - 5 else 55 },
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Days selector
                    Text(
                        text = "Tekrarlama Günleri:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        dayOptions.take(3).forEach { day ->
                            DayChip(
                                label = day,
                                isSelected = selectedDays == day,
                                onClick = { selectedDays = day }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        dayOptions.drop(3).forEach { day ->
                            DayChip(
                                label = day,
                                isSelected = selectedDays == day,
                                onClick = { selectedDays = day }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { isAddingNew = false },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Geri", color = MaterialTheme.colorScheme.onSurface)
                        }

                        Button(
                            onClick = {
                                val newRem = RadioReminder(
                                    id = "rem_${UUID.randomUUID()}",
                                    stationId = selectedStation.id,
                                    stationName = selectedStation.name,
                                    programTitle = if (programTitle.isBlank()) "${selectedStation.name} Programı" else programTitle.trim(),
                                    timeHour = hour,
                                    timeMinute = minute,
                                    daysDescription = selectedDays,
                                    isEnabled = true
                                )
                                onAddReminder(newRem)
                                isAddingNew = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonAmber),
                            modifier = Modifier
                                .weight(1.5f)
                                .testTag("save_reminder_btn")
                        ) {
                            Text("Kaydet", fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReminderCard(
    reminder: RadioReminder,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(
                1.dp,
                if (reminder.isEnabled) NeonAmber.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                RoundedCornerShape(14.dp)
            )
            .testTag("reminder_card_${reminder.id}"),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = reminder.formattedTime,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (reminder.isEnabled) NeonAmber else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• ${reminder.daysDescription}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = reminder.programTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "📻 ${reminder.stationName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = reminder.isEnabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = NeonAmber
                    ),
                    modifier = Modifier.testTag("toggle_reminder_${reminder.id}")
                )
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.testTag("delete_reminder_${reminder.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Sil",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DayChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (isSelected) NeonAmber else MaterialTheme.colorScheme.surface
    val textColor = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .border(1.dp, if (isSelected) NeonAmber else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = textColor
        )
    }
}
