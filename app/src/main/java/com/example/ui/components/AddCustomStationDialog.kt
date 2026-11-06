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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import com.example.model.RadioCategory
import com.example.model.RadioStation
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCustomStationDialog(
    onDismiss: () -> Unit,
    onAddStation: (RadioStation) -> Unit
) {
    var stationName by remember { mutableStateOf("") }
    var streamUrl by remember { mutableStateOf("") }
    var frequencyOrCity by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(RadioCategory.CUSTOM) }
    var isCategoryDropdownOpen by remember { mutableStateOf(false) }

    val colorOptions = listOf(
        0xFF00E5FF, 0xFFFF007F, 0xFF9D4EDD, 0xFF00E676,
        0xFFFFB300, 0xFFFF3D00, 0xFF2979FF, 0xFFD81B60
    )
    var selectedColorIndex by remember { mutableIntStateOf(0) }

    val emojiOptions = listOf("✨", "📻", "🎵", "🎧", "🔥", "☀️", "🌴", "⚡", "🎙️", "👑")
    var selectedEmoji by remember { mutableStateOf("📻") }

    var errorMsg by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(24.dp)
                )
                .testTag("add_custom_station_dialog"),
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
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Radio,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Özel Radyo Ekle",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Kendi yayın URL'nizi kaydedin",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_add_station_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Kapat",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Name Input
                OutlinedTextField(
                    value = stationName,
                    onValueChange = {
                        stationName = it
                        errorMsg = null
                    },
                    label = { Text("Radyo İstasyonu Adı *") },
                    placeholder = { Text("Örn: Süper Karadeniz FM") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("station_name_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Stream URL Input
                OutlinedTextField(
                    value = streamUrl,
                    onValueChange = {
                        streamUrl = it
                        errorMsg = null
                    },
                    label = { Text("Canlı Yayın Akış URL'si (Stream) *") },
                    placeholder = { Text("http://yayin.radyom.com:8000/live") },
                    leadingIcon = {
                        Icon(Icons.Default.Link, contentDescription = null)
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("station_url_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Frequency or City
                OutlinedTextField(
                    value = frequencyOrCity,
                    onValueChange = { frequencyOrCity = it },
                    label = { Text("Şehir veya Frekans Bilgisi") },
                    placeholder = { Text("Örn: 94.5 FM • Trabzon") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("station_frequency_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Category selector
                ExposedDropdownMenuBox(
                    expanded = isCategoryDropdownOpen,
                    onExpandedChange = { isCategoryDropdownOpen = !isCategoryDropdownOpen },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = "${selectedCategory.icon} ${selectedCategory.title}",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Kategori") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryDropdownOpen) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .testTag("category_dropdown")
                    )

                    ExposedDropdownMenu(
                        expanded = isCategoryDropdownOpen,
                        onDismissRequest = { isCategoryDropdownOpen = false }
                    ) {
                        RadioCategory.values().filter { it != RadioCategory.ALL && it != RadioCategory.FAVORITES }.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text("${cat.icon} ${cat.title}") },
                                onClick = {
                                    selectedCategory = cat
                                    isCategoryDropdownOpen = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Emoji picker row
                Text(
                    text = "İstasyon İkonu Seçin:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    emojiOptions.take(5).forEach { emo ->
                        EmojiBadge(
                            emoji = emo,
                            isSelected = selectedEmoji == emo,
                            onClick = { selectedEmoji = emo }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    emojiOptions.drop(5).forEach { emo ->
                        EmojiBadge(
                            emoji = emo,
                            isSelected = selectedEmoji == emo,
                            onClick = { selectedEmoji = emo }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Color picker
                Text(
                    text = "Vurgu Rengi:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    colorOptions.forEachIndexed { idx, colHex ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(colHex))
                                .border(
                                    width = if (selectedColorIndex == idx) 2.5.dp else 1.dp,
                                    color = if (selectedColorIndex == idx) Color.White else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColorIndex = idx }
                        )
                    }
                }

                if (errorMsg != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMsg ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("cancel_add_station_btn")
                    ) {
                        Text("Vazgeç")
                    }

                    Button(
                        onClick = {
                            if (stationName.isBlank()) {
                                errorMsg = "Lütfen bir radyo adı girin."
                                return@Button
                            }
                            if (streamUrl.isBlank() || (!streamUrl.startsWith("http://") && !streamUrl.startsWith("https://"))) {
                                errorMsg = "Lütfen geçerli bir yayın URL'si girin (http:// veya https://)."
                                return@Button
                            }

                            val newStation = RadioStation(
                                id = "custom_${UUID.randomUUID()}",
                                name = stationName.trim(),
                                streamUrl = streamUrl.trim(),
                                category = selectedCategory,
                                frequencyOrCity = if (frequencyOrCity.isBlank()) "Özel İstasyon" else frequencyOrCity.trim(),
                                accentColorHex = colorOptions[selectedColorIndex],
                                iconEmoji = selectedEmoji,
                                isCustom = true,
                                listenersCount = 1,
                                featuredBadge = "ÖZEL",
                                dateAdded = System.currentTimeMillis()
                            )

                            onAddStation(newStation)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("save_custom_station_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Radyoyu Kaydet",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmojiBadge(
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface
    val border = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)

    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .border(1.5.dp, border, RoundedCornerShape(10.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = emoji, fontSize = 22.sp)
    }
}
