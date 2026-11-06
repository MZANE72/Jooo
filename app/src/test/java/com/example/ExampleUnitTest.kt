package com.example

import com.example.model.RadioCategory
import com.example.model.RadioStation
import com.example.model.SortOption
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Locale

class ExampleUnitTest {

    @Test
    fun `verify default station models and categories`() {
        val testStations = listOf(
            RadioStation(
                id = "trt_fm",
                name = "TRT FM",
                streamUrl = "http://stream.radyotvonline.net/trtfm/playlist.m3u8",
                category = RadioCategory.ULUSAL,
                frequencyOrCity = "91.4 FM • Türkiye",
                accentColorHex = 0xFFE53935,
                iconEmoji = "🇹🇷",
                listenersCount = 145000
            ),
            RadioStation(
                id = "trt_kurdi",
                name = "TRT Kurdî Radyo",
                streamUrl = "http://stream.radyotvonline.net/trtkurdi/playlist.m3u8",
                category = RadioCategory.KURTCE,
                frequencyOrCity = "104.5 FM • Diyarbakır",
                accentColorHex = 0xFF00C853,
                iconEmoji = "☀️",
                listenersCount = 104000
            ),
            RadioStation(
                id = "karadeniz_fm",
                name = "Karadeniz FM",
                streamUrl = "https://stream.karadenizfm.com/stream",
                category = RadioCategory.YEREL,
                frequencyOrCity = "98.2 FM • Trabzon",
                accentColorHex = 0xFF00B0FF,
                iconEmoji = "🌊",
                listenersCount = 81000
            )
        )

        assertEquals(3, testStations.size)
        assertTrue(testStations.any { it.category == RadioCategory.ULUSAL })
        assertTrue(testStations.any { it.category == RadioCategory.KURTCE })
        assertTrue(testStations.any { it.category == RadioCategory.YEREL })
    }

    @Test
    fun `verify sorting logic by popularity and name`() {
        val station1 = RadioStation(
            id = "s1", name = "Z Radyo", streamUrl = "http://test1",
            category = RadioCategory.ULUSAL, frequencyOrCity = "100.0",
            accentColorHex = 0L, iconEmoji = "📻", listenersCount = 1000
        )
        val station2 = RadioStation(
            id = "s2", name = "A Radyo", streamUrl = "http://test2",
            category = RadioCategory.ULUSAL, frequencyOrCity = "101.0",
            accentColorHex = 0L, iconEmoji = "📻", listenersCount = 5000
        )
        val list = listOf(station1, station2)

        val sortedByPop = list.sortedByDescending { it.listenersCount }
        assertEquals("A Radyo", sortedByPop.first().name)

        val sortedByName = list.sortedBy { it.name.lowercase(Locale("tr", "TR")) }
        assertEquals("A Radyo", sortedByName.first().name)
    }

    @Test
    fun `verify search query filtering`() {
        val stations = listOf(
            RadioStation(
                id = "kral_fm", name = "Kral FM", streamUrl = "http://kral",
                category = RadioCategory.ULUSAL, frequencyOrCity = "92.0 FM",
                accentColorHex = 0L, iconEmoji = "👑", songTitle = "Damar Şarkılar"
            ),
            RadioStation(
                id = "slow_turk", name = "Slow Türk", streamUrl = "http://slow",
                category = RadioCategory.SLOW, frequencyOrCity = "95.3 FM",
                accentColorHex = 0L, iconEmoji = "💖", songTitle = "Romantik Müzik"
            )
        )

        val searchResult = stations.filter {
            it.name.contains("kral", ignoreCase = true) || it.songTitle?.contains("kral", ignoreCase = true) == true
        }
        assertEquals(1, searchResult.size)
        assertEquals("kral_fm", searchResult.first().id)
    }
}
