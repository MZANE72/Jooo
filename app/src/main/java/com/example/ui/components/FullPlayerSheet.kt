package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AudioQuality
import com.example.model.LiveTheme
import com.example.model.PlaybackState
import com.example.model.PlaybackStatus
import com.example.ui.theme.LiveRed
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta

@Composable
fun FullPlayerSheet(
    playbackState: PlaybackState,
    isFavorite: Boolean,
    liveTheme: LiveTheme,
    audioQuality: AudioQuality,
    onDismiss: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPrevClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onOpenSleepTimer: () -> Unit,
    onOpenSettings: () -> Unit,
    onVolumeChange: (Float) -> Unit
) {
    val station = playbackState.currentStation ?: return
    val isPlaying = playbackState.status == PlaybackStatus.PLAYING
    val isBuffering = playbackState.status == PlaybackStatus.BUFFERING
    val accentColor = Color(station.accentColorHex)

    // Animated rotating vinyl / tape disc
    val infiniteTransition = rememberInfiniteTransition(label = "vinyl_rotate")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    var currentVolume by remember { mutableFloatStateOf(playbackState.volume) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .testTag("full_player_sheet"),
            color = MaterialTheme.colorScheme.background
        ) {
            DynamicWallpaperBackground(
                liveTheme = liveTheme,
                isDarkMode = true
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(28.dp))

                    // Top Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("collapse_player_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Aşağı İndir",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "ŞU AN CANLI ÇALIYOR",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = station.category.title,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = onOpenSettings,
                            modifier = Modifier.testTag("player_settings_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Ayarlar",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Center Animated Disc & Equalizer Pulse
                    Box(
                        modifier = Modifier
                            .size(260.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Pulsing Rings
                        CircularEqualizerPulse(
                            isPlaying = isPlaying,
                            primaryColor = NeonCyan,
                            secondaryColor = accentColor,
                            sizeDp = 260.dp
                        )

                        // Big Glowing Disc / Cassette Player
                        Box(
                            modifier = Modifier
                                .size(200.dp)
                                .rotate(if (isPlaying) rotationAngle else 0f)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(
                                            accentColor.copy(alpha = 0.5f),
                                            Color(0xFF161B2C),
                                            Color(0xFF090C16)
                                        )
                                    )
                                )
                                .border(
                                    3.dp,
                                    Brush.linearGradient(listOf(NeonCyan, accentColor, NeonMagenta)),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            // Grooves
                            Box(
                                modifier = Modifier
                                    .size(150.dp)
                                    .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                            )
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                            )

                            // Center Label
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(accentColor)
                                    .border(2.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = station.iconEmoji, fontSize = 36.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Dynamic Frequency Bars (Equalizer)
                    EqualizerFrequencyBars(
                        frequencies = playbackState.audioFrequencies,
                        isPlaying = isPlaying,
                        primaryColor = NeonCyan,
                        secondaryColor = accentColor,
                        barCount = 18,
                        height = 54.dp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Station Name & Details
                    Text(
                        text = station.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = station.frequencyOrCity,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (!station.songTitle.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "🎶 ${station.songTitle}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = NeonAmber,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Badges row: Quality, Listeners, Sleep Timer
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Quality badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(NeonCyan.copy(alpha = 0.15f))
                                .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "HD ${audioQuality.approxBitrate}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan
                            )
                        }

                        // Listeners count
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.08f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "🎧 ${station.listenersCount / 1000}k dinliyor",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Sleep timer active badge
                        if (playbackState.sleepTimerMinutesLeft != null) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(NeonAmber.copy(alpha = 0.2f))
                                    .clickable { onOpenSleepTimer() }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "⏳ ${playbackState.sleepTimerMinutesLeft} dk",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonAmber
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Main Controls: Favorite, Prev, Play/Pause, Next, Sleep Timer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onFavoriteToggle,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("full_player_fav_btn")
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favori",
                                tint = if (isFavorite) LiveRed else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        IconButton(
                            onClick = onPrevClick,
                            modifier = Modifier
                                .size(56.dp)
                                .testTag("full_player_prev_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipPrevious,
                                contentDescription = "Önceki",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        // Giant Play / Pause Button
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(NeonCyan, accentColor))
                                )
                                .clickable { onPlayPauseClick() }
                                .testTag("full_player_play_pause_btn"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isBuffering) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(34.dp),
                                    color = Color.Black,
                                    strokeWidth = 3.dp
                                )
                            } else {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (isPlaying) "Duraklat" else "Oynat",
                                    tint = Color.Black,
                                    modifier = Modifier.size(42.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = onNextClick,
                            modifier = Modifier
                                .size(56.dp)
                                .testTag("full_player_next_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "Sonraki",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        IconButton(
                            onClick = onOpenSleepTimer,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("full_player_sleep_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bedtime,
                                contentDescription = "Uyku Modu",
                                tint = if (playbackState.sleepTimerMinutesLeft != null) NeonAmber else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Volume slider
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (currentVolume == 0f) Icons.Default.VolumeMute else Icons.Default.VolumeDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )

                        Slider(
                            value = currentVolume,
                            onValueChange = {
                                currentVolume = it
                                onVolumeChange(it)
                            },
                            valueRange = 0f..1f,
                            colors = SliderDefaults.colors(
                                thumbColor = NeonCyan,
                                activeTrackColor = NeonCyan
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 10.dp)
                                .testTag("volume_slider")
                        )

                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
