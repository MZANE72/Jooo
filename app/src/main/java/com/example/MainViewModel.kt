package com.example

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.StationRepository
import com.example.model.AudioQuality
import com.example.model.LiveTheme
import com.example.model.PlaybackState
import com.example.model.RadioCategory
import com.example.model.RadioReminder
import com.example.model.RadioStation
import com.example.model.SortOption
import com.example.service.RadioPlayerController
import com.example.voice.VoiceActionResult
import com.example.voice.VoiceCommandManager
import com.example.voice.VoiceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StationRepository(application)
    val voiceManager = VoiceCommandManager(application)

    init {
        RadioPlayerController.init(application)
        RadioPlayerController.updatePlaylist(repository.getAllStations())
    }

    val playbackState: StateFlow<PlaybackState> = RadioPlayerController.playbackState
    val voiceState: StateFlow<VoiceState> = voiceManager.voiceState

    val favorites: StateFlow<Set<String>> = repository.favoritesFlow
    val customStations: StateFlow<List<RadioStation>> = repository.customStationsFlow
    val reminders: StateFlow<List<RadioReminder>> = repository.remindersFlow
    val liveTheme: StateFlow<LiveTheme> = repository.liveThemeFlow
    val audioQuality: StateFlow<AudioQuality> = repository.audioQualityFlow
    val isDarkMode: StateFlow<Boolean> = repository.isDarkModeFlow

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(RadioCategory.ALL)
    val selectedCategory: StateFlow<RadioCategory> = _selectedCategory.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.POPULARITY)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    private val _onlyFavorites = MutableStateFlow(false)
    val onlyFavorites: StateFlow<Boolean> = _onlyFavorites.asStateFlow()

    // Dialog & Sheet States
    var showSleepTimer = MutableStateFlow(false)
    var showAddStation = MutableStateFlow(false)
    var showVoiceDialog = MutableStateFlow(false)
    var showReminders = MutableStateFlow(false)
    var showSettings = MutableStateFlow(false)
    var showFullPlayer = MutableStateFlow(false)

    private data class FilterCriteria(
        val query: String,
        val category: RadioCategory,
        val sortOption: SortOption,
        val onlyFavorites: Boolean
    )

    private val criteriaFlow = combine(
        _searchQuery,
        _selectedCategory,
        _sortOption,
        _onlyFavorites
    ) { query, cat, sort, onlyFav ->
        FilterCriteria(query, cat, sort, onlyFav)
    }

    // Filtered & Sorted Stations Flow
    val filteredStations: StateFlow<List<RadioStation>> = combine(
        customStations,
        favorites,
        criteriaFlow
    ) { customList, favSet, criteria ->
        val all: List<RadioStation> = repository.defaultStations + customList
        RadioPlayerController.updatePlaylist(all)

        var result = all.filter { station: RadioStation ->
            val matchesCategory = when (criteria.category) {
                RadioCategory.ALL -> true
                RadioCategory.FAVORITES -> favSet.contains(station.id)
                RadioCategory.CUSTOM -> station.isCustom
                else -> station.category == criteria.category
            }

            val matchesOnlyFav = if (criteria.onlyFavorites) favSet.contains(station.id) else true

            val matchesQuery = if (criteria.query.isBlank()) {
                true
            } else {
                val q = criteria.query.lowercase(Locale("tr", "TR")).trim()
                station.name.lowercase(Locale("tr", "TR")).contains(q) ||
                station.frequencyOrCity.lowercase(Locale("tr", "TR")).contains(q) ||
                station.category.title.lowercase(Locale("tr", "TR")).contains(q) ||
                (station.songTitle?.lowercase(Locale("tr", "TR"))?.contains(q) == true) ||
                (station.artistName?.lowercase(Locale("tr", "TR"))?.contains(q) == true)
            }

            matchesCategory && matchesOnlyFav && matchesQuery
        }

        // Apply sorting
        when (criteria.sortOption) {
            SortOption.POPULARITY -> result.sortedByDescending { it.listenersCount }
            SortOption.NAME_AZ -> result.sortedBy { it.name.lowercase(Locale("tr", "TR")) }
            SortOption.NAME_ZA -> result.sortedByDescending { it.name.lowercase(Locale("tr", "TR")) }
            SortOption.DATE_ADDED -> result.sortedByDescending { it.dateAdded }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.defaultStations)

    // Top featured channels for header carousel
    val featuredStations: StateFlow<List<RadioStation>> = customStations.combine(favorites) { customList, _ ->
        val all = repository.defaultStations + customList
        all.sortedByDescending { it.listenersCount }.take(6)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.defaultStations.take(6))

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(category: RadioCategory) {
        _selectedCategory.value = category
    }

    fun onSortOptionSelected(sort: SortOption) {
        _sortOption.value = sort
    }

    fun toggleOnlyFavorites() {
        _onlyFavorites.value = !_onlyFavorites.value
    }

    fun playStation(station: RadioStation) {
        RadioPlayerController.play(station)
    }

    fun togglePlayPause() {
        RadioPlayerController.togglePlayPause()
    }

    fun playNext() {
        RadioPlayerController.playNext()
    }

    fun playPrevious() {
        RadioPlayerController.playPrevious()
    }

    fun setVolume(vol: Float) {
        RadioPlayerController.setVolume(vol)
    }

    fun toggleFavorite(stationId: String) {
        repository.toggleFavorite(stationId)
    }

    fun addCustomStation(station: RadioStation) {
        repository.addCustomStation(station)
        RadioPlayerController.updatePlaylist(repository.getAllStations())
    }

    fun deleteCustomStation(stationId: String) {
        repository.deleteCustomStation(stationId)
        RadioPlayerController.updatePlaylist(repository.getAllStations())
    }

    fun startSleepTimer(minutes: Int) {
        RadioPlayerController.startSleepTimer(minutes)
    }

    fun cancelSleepTimer() {
        RadioPlayerController.cancelSleepTimer()
    }

    fun addReminder(reminder: RadioReminder) {
        repository.addReminder(reminder)
    }

    fun removeReminder(id: String) {
        repository.removeReminder(id)
    }

    fun toggleReminder(id: String) {
        repository.toggleReminder(id)
    }

    fun setLiveTheme(theme: LiveTheme) {
        repository.setLiveTheme(theme)
    }

    fun setAudioQuality(quality: AudioQuality) {
        repository.setAudioQuality(quality)
    }

    fun setDarkMode(isDark: Boolean) {
        repository.setDarkMode(isDark)
    }

    fun triggerVoiceListening() {
        val all = repository.getAllStations()
        voiceManager.startListening(all) { action ->
            when (action) {
                is VoiceActionResult.PlayStation -> playStation(action.station)
                is VoiceActionResult.SearchQuery -> _searchQuery.value = action.query
                VoiceActionResult.PausePlayback -> RadioPlayerController.pause()
                VoiceActionResult.ResumePlayback -> RadioPlayerController.resume()
                VoiceActionResult.NextStation -> RadioPlayerController.playNext()
                VoiceActionResult.PreviousStation -> RadioPlayerController.playPrevious()
                VoiceActionResult.ToggleFavoriteCurrent -> {
                    playbackState.value.currentStation?.let { toggleFavorite(it.id) }
                }
                is VoiceActionResult.Unrecognized -> {
                    _searchQuery.value = action.rawText
                }
            }
        }
    }

    fun executeManualVoiceCommand(command: String) {
        val all = repository.getAllStations()
        voiceManager.processSpokenCommand(command, all)
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.stopListening()
    }
}
