package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.MainViewModel
import com.example.model.PlaybackStatus
import com.example.model.RadioCategory
import com.example.model.SortOption
import com.example.ui.components.AddCustomStationDialog
import com.example.ui.components.DynamicWallpaperBackground
import com.example.ui.components.FeaturedStationBanner
import com.example.ui.components.FullPlayerSheet
import com.example.ui.components.MiniPlayerBar
import com.example.ui.components.ReminderDialog
import com.example.ui.components.SettingsAndThemesDialog
import com.example.ui.components.SleepTimerDialog
import com.example.ui.components.StationCard
import com.example.ui.components.VoiceCommandDialog
import com.example.ui.theme.LiveRed
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta

@Composable
fun RadioMainScreen(viewModel: MainViewModel) {
    val context = LocalContext.current

    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val voiceState by viewModel.voiceState.collectAsStateWithLifecycle()
    val stations by viewModel.filteredStations.collectAsStateWithLifecycle()
    val featuredStations by viewModel.featuredStations.collectAsStateWithLifecycle()
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val reminders by viewModel.reminders.collectAsStateWithLifecycle()
    val liveTheme by viewModel.liveTheme.collectAsStateWithLifecycle()
    val audioQuality by viewModel.audioQuality.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val sortOption by viewModel.sortOption.collectAsStateWithLifecycle()
    val onlyFavorites by viewModel.onlyFavorites.collectAsStateWithLifecycle()

    val showSleepTimer by viewModel.showSleepTimer.collectAsStateWithLifecycle()
    val showAddStation by viewModel.showAddStation.collectAsStateWithLifecycle()
    val showVoiceDialog by viewModel.showVoiceDialog.collectAsStateWithLifecycle()
    val showReminders by viewModel.showReminders.collectAsStateWithLifecycle()
    val showSettings by viewModel.showSettings.collectAsStateWithLifecycle()
    val showFullPlayer by viewModel.showFullPlayer.collectAsStateWithLifecycle()

    var isSortMenuOpen by remember { mutableStateOf(false) }

    // Permission launcher for audio recording (Voice command)
    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.showVoiceDialog.value = true
            viewModel.triggerVoiceListening()
        } else {
            viewModel.showVoiceDialog.value = true
        }
    }

    DynamicWallpaperBackground(
        liveTheme = liveTheme,
        isDarkMode = isDarkMode
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Bar
                TopHeaderBar(
                    onlyFavorites = onlyFavorites,
                    activeRemindersCount = reminders.count { it.isEnabled },
                    onVoiceClick = {
                        val hasMic = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED
                        if (hasMic) {
                            viewModel.showVoiceDialog.value = true
                            viewModel.triggerVoiceListening()
                        } else {
                            micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    },
                    onFavoritesClick = { viewModel.toggleOnlyFavorites() },
                    onRemindersClick = { viewModel.showReminders.value = true },
                    onSettingsClick = { viewModel.showSettings.value = true }
                )

                // Search & Sort Bar
                SearchAndFilterBar(
                    searchQuery = searchQuery,
                    onSearchChange = { viewModel.onSearchQueryChanged(it) },
                    sortOption = sortOption,
                    onSortSelect = { viewModel.onSortOptionSelected(it) },
                    onAddStationClick = { viewModel.showAddStation.value = true },
                    isSortMenuOpen = isSortMenuOpen,
                    onToggleSortMenu = { isSortMenuOpen = !isSortMenuOpen }
                )

                // Category Chips Scroll
                CategoryChipsRow(
                    selectedCategory = selectedCategory,
                    onCategorySelect = { viewModel.onCategorySelected(it) }
                )

                // Main Station Grid & Hero
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    contentPadding = PaddingValues(
                        start = 14.dp,
                        end = 14.dp,
                        top = 10.dp,
                        bottom = if (playbackState.currentStation != null) 110.dp else 24.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // Hero Featured Row (only show when not searching)
                    if (searchQuery.isBlank() && selectedCategory == RadioCategory.ALL && !onlyFavorites) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "🔥 En Çok Dinlenen Radyolar",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Canlı Yayınlar",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = NeonCyan
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(featuredStations) { featured ->
                                        val isCurrent = playbackState.currentStation?.id == featured.id
                                        FeaturedStationBanner(
                                            station = featured,
                                            isPlaying = isCurrent && playbackState.status == PlaybackStatus.PLAYING,
                                            isFavorite = favorites.contains(featured.id),
                                            onPlayClick = {
                                                if (isCurrent) viewModel.togglePlayPause() else viewModel.playStation(featured)
                                            },
                                            onFavoriteToggle = { viewModel.toggleFavorite(featured.id) },
                                            modifier = Modifier.width(310.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "📻 Tüm İstasyonlar (${stations.size})",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                        }
                    }

                    // Empty state check
                    if (stations.isEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            EmptyStationsState(
                                searchQuery = searchQuery,
                                onlyFavorites = onlyFavorites,
                                onClearFilters = {
                                    viewModel.onSearchQueryChanged("")
                                    viewModel.onCategorySelected(RadioCategory.ALL)
                                    if (onlyFavorites) viewModel.toggleOnlyFavorites()
                                },
                                onAddNew = { viewModel.showAddStation.value = true }
                            )
                        }
                    } else {
                        items(stations, key = { it.id }) { station ->
                            val isCurrent = playbackState.currentStation?.id == station.id
                            val isPlaying = isCurrent && playbackState.status == PlaybackStatus.PLAYING
                            val isBuffering = isCurrent && playbackState.status == PlaybackStatus.BUFFERING

                            StationCard(
                                station = station,
                                isPlaying = isPlaying,
                                isBuffering = isBuffering,
                                isFavorite = favorites.contains(station.id),
                                onCardClick = {
                                    if (isCurrent) {
                                        viewModel.togglePlayPause()
                                    } else {
                                        viewModel.playStation(station)
                                    }
                                },
                                onFavoriteToggle = { viewModel.toggleFavorite(station.id) }
                            )
                        }
                    }
                }
            }

            // Persistent Mini Player anchored above navigation insets
            AnimatedVisibility(
                visible = playbackState.currentStation != null,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                MiniPlayerBar(
                    playbackState = playbackState,
                    onBarClick = { viewModel.showFullPlayer.value = true },
                    onPlayPauseClick = { viewModel.togglePlayPause() },
                    onNextClick = { viewModel.playNext() },
                    onPrevClick = { viewModel.playPrevious() }
                )
            }
        }
    }

    // Full Player Sheet Modal
    if (showFullPlayer && playbackState.currentStation != null) {
        val st = playbackState.currentStation!!
        FullPlayerSheet(
            playbackState = playbackState,
            isFavorite = favorites.contains(st.id),
            liveTheme = liveTheme,
            audioQuality = audioQuality,
            onDismiss = { viewModel.showFullPlayer.value = false },
            onPlayPauseClick = { viewModel.togglePlayPause() },
            onNextClick = { viewModel.playNext() },
            onPrevClick = { viewModel.playPrevious() },
            onFavoriteToggle = { viewModel.toggleFavorite(st.id) },
            onOpenSleepTimer = { viewModel.showSleepTimer.value = true },
            onOpenSettings = { viewModel.showSettings.value = true },
            onVolumeChange = { viewModel.setVolume(it) }
        )
    }

    // Sleep Timer Dialog
    if (showSleepTimer) {
        SleepTimerDialog(
            activeMinutesLeft = playbackState.sleepTimerMinutesLeft,
            onDismiss = { viewModel.showSleepTimer.value = false },
            onStartTimer = { mins -> viewModel.startSleepTimer(mins) },
            onCancelTimer = { viewModel.cancelSleepTimer() }
        )
    }

    // Add Custom Station Dialog
    if (showAddStation) {
        AddCustomStationDialog(
            onDismiss = { viewModel.showAddStation.value = false },
            onAddStation = { station -> viewModel.addCustomStation(station) }
        )
    }

    // Voice Command Dialog
    if (showVoiceDialog) {
        VoiceCommandDialog(
            voiceState = voiceState,
            onDismiss = {
                viewModel.voiceManager.stopListening()
                viewModel.showVoiceDialog.value = false
            },
            onStartListening = { viewModel.triggerVoiceListening() },
            onStopListening = { viewModel.voiceManager.stopListening() },
            onManualCommandSelect = { cmd ->
                viewModel.executeManualVoiceCommand(cmd)
            }
        )
    }

    // Reminders Dialog
    if (showReminders) {
        ReminderDialog(
            reminders = reminders,
            stations = viewModel.filteredStations.value,
            onDismiss = { viewModel.showReminders.value = false },
            onAddReminder = { r -> viewModel.addReminder(r) },
            onToggleReminder = { id -> viewModel.toggleReminder(id) },
            onDeleteReminder = { id -> viewModel.removeReminder(id) }
        )
    }

    // Settings & Themes Dialog
    if (showSettings) {
        SettingsAndThemesDialog(
            currentLiveTheme = liveTheme,
            currentAudioQuality = audioQuality,
            isDarkMode = isDarkMode,
            onDismiss = { viewModel.showSettings.value = false },
            onSelectTheme = { theme -> viewModel.setLiveTheme(theme) },
            onSelectAudioQuality = { q -> viewModel.setAudioQuality(q) },
            onToggleDarkMode = { dark -> viewModel.setDarkMode(dark) }
        )
    }
}

@Composable
private fun TopHeaderBar(
    onlyFavorites: Boolean,
    activeRemindersCount: Int,
    onVoiceClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onRemindersClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Brand Logo
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(listOf(NeonCyan, NeonMagenta))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "📻", fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = "Radyo FM",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Canlı Yayın & Müzik",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonCyan
                )
            }
        }

        // Action Icons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Voice Command Button with glowing gradient
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(NeonCyan, NeonMagenta)))
                    .clickable { onVoiceClick() }
                    .testTag("voice_command_header_btn"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Sesli Komut",
                    tint = Color.Black,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Favorites Filter Toggle
            IconButton(
                onClick = onFavoritesClick,
                modifier = Modifier.testTag("header_favorites_toggle_btn")
            ) {
                Icon(
                    imageVector = if (onlyFavorites) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorilerim",
                    tint = if (onlyFavorites) LiveRed else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Hourly Program Reminders
            Box {
                IconButton(
                    onClick = onRemindersClick,
                    modifier = Modifier.testTag("header_reminders_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Alarm,
                        contentDescription = "Hatırlatıcılar",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }
                if (activeRemindersCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 4.dp, end = 4.dp)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(NeonAmber),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$activeRemindersCount",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }

            // Themes & Settings
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.testTag("header_settings_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = "Temalar ve Ayarlar",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun SearchAndFilterBar(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    sortOption: SortOption,
    onSortSelect: (SortOption) -> Unit,
    onAddStationClick: () -> Unit,
    isSortMenuOpen: Boolean,
    onToggleSortMenu: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Search TextField
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("İstasyon, sanatçı veya şehir ara...", fontSize = 13.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Temizle",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                focusedBorderColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .weight(1f)
                .height(50.dp)
                .testTag("search_stations_input")
        )

        // Sort Button & Dropdown
        Box {
            Surface(
                modifier = Modifier
                    .height(50.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .clickable { onToggleSortMenu() }
                    .testTag("sort_menu_trigger_btn"),
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Sort,
                        contentDescription = "Sırala",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            DropdownMenu(
                expanded = isSortMenuOpen,
                onDismissRequest = onToggleSortMenu
            ) {
                SortOption.values().forEach { opt ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = opt.title,
                                fontWeight = if (sortOption == opt) FontWeight.Bold else FontWeight.Normal,
                                color = if (sortOption == opt) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        },
                        onClick = {
                            onSortSelect(opt)
                            onToggleSortMenu()
                        }
                    )
                }
            }
        }

        // Add Custom Station Button (+)
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primary)
                .clickable { onAddStationClick() }
                .testTag("add_custom_station_trigger_btn"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Radyo Ekle",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Composable
private fun CategoryChipsRow(
    selectedCategory: RadioCategory,
    onCategorySelect: (RadioCategory) -> Unit
) {
    val categories = RadioCategory.values()
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { cat ->
            val isSelected = selectedCategory == cat
            val bg = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
            val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            val border = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)

            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, border, RoundedCornerShape(12.dp))
                    .clickable { onCategorySelect(cat) }
                    .testTag("cat_chip_${cat.name}"),
                color = bg,
                tonalElevation = if (isSelected) 4.dp else 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = cat.icon, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = cat.title,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = textColor
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyStationsState(
    searchQuery: String,
    onlyFavorites: Boolean,
    onClearFilters: () -> Unit,
    onAddNew: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp, horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = if (onlyFavorites) "⭐" else "📻", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = when {
                    onlyFavorites -> "Henüz favori radyo eklemediniz"
                    searchQuery.isNotBlank() -> "\"$searchQuery\" için sonuç bulunamadı"
                    else -> "Bu kategoride radyo bulunamadı"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "İstasyon ekleyebilir veya filtreleri temizleyebilirsiniz",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onClearFilters,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text("Filtreleri Temizle", color = MaterialTheme.colorScheme.onSurface)
                }

                Button(
                    onClick = onAddNew,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Özel Radyo Ekle", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}
