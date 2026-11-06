package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.AudioQuality
import com.example.model.LiveTheme
import com.example.model.RadioCategory
import com.example.model.RadioReminder
import com.example.model.RadioStation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class StationRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("radyo_app_prefs", Context.MODE_PRIVATE)

    private val _favoritesFlow = MutableStateFlow<Set<String>>(emptySet())
    val favoritesFlow: StateFlow<Set<String>> = _favoritesFlow.asStateFlow()

    private val _customStationsFlow = MutableStateFlow<List<RadioStation>>(emptyList())
    val customStationsFlow: StateFlow<List<RadioStation>> = _customStationsFlow.asStateFlow()

    private val _remindersFlow = MutableStateFlow<List<RadioReminder>>(emptyList())
    val remindersFlow: StateFlow<List<RadioReminder>> = _remindersFlow.asStateFlow()

    private val _liveThemeFlow = MutableStateFlow(LiveTheme.NEON_CYBER)
    val liveThemeFlow: StateFlow<LiveTheme> = _liveThemeFlow.asStateFlow()

    private val _audioQualityFlow = MutableStateFlow(AudioQuality.HIGH)
    val audioQualityFlow: StateFlow<AudioQuality> = _audioQualityFlow.asStateFlow()

    private val _isDarkModeFlow = MutableStateFlow(true)
    val isDarkModeFlow: StateFlow<Boolean> = _isDarkModeFlow.asStateFlow()

    // Pre-loaded national, regional, Kurdish and thematic radio stations
    val defaultStations: List<RadioStation> = listOf(
        // === ULUSAL RADYOLAR ===
        RadioStation(
            id = "trt_fm",
            name = "TRT FM",
            streamUrl = "http://stream.radyotvonline.net/trtfm/playlist.m3u8",
            backupStreamUrl = "https://kesintisiz.radyotvonline.net/trtfm",
            category = RadioCategory.ULUSAL,
            frequencyOrCity = "91.4 FM • Türkiye",
            accentColorHex = 0xFFE53935,
            iconEmoji = "🇹🇷",
            listenersCount = 145000,
            featuredBadge = "EN ÇOK DİNLENEN",
            songTitle = "Günün Hit Şarkıları",
            artistName = "TRT FM Canlı Yayın"
        ),
        RadioStation(
            id = "kral_fm",
            name = "Kral FM",
            streamUrl = "https://dygvideo.ercdn.net/kralfm/chunklist.m3u8",
            backupStreamUrl = "http://kralwmp.radyotvonline.net/lisansli/kral/chunklist.m3u8",
            category = RadioCategory.ULUSAL,
            frequencyOrCity = "92.0 FM • Ulusal",
            accentColorHex = 0xFFFFB300,
            iconEmoji = "👑",
            listenersCount = 138000,
            featuredBadge = "EN ÇOK DİNLENEN",
            songTitle = "İlaç Gibi Radyo",
            artistName = "Damar & Arabesk Hitleri"
        ),
        RadioStation(
            id = "power_turk",
            name = "Power Türk",
            streamUrl = "https://listen.powerapp.com.tr/powerturk/icecast.audio",
            backupStreamUrl = "https://powerturk.listenpowerapp.com/powerturk/icecast.audio",
            category = RadioCategory.ULUSAL,
            frequencyOrCity = "99.8 FM • Türkiye",
            accentColorHex = 0xFFFF1744,
            iconEmoji = "⚡",
            listenersCount = 125000,
            featuredBadge = "POPÜLER",
            songTitle = "Önce Müzik",
            artistName = "Türkçe Pop & Akustik"
        ),
        RadioStation(
            id = "slow_turk",
            name = "Slow Türk",
            streamUrl = "https://radyo.duhnet.tv/slowturk",
            backupStreamUrl = "https://slowturk.canliradyodinle.fm/stream",
            category = RadioCategory.SLOW,
            frequencyOrCity = "95.3 FM • Ulusal",
            accentColorHex = 0xFF8E24AA,
            iconEmoji = "💖",
            listenersCount = 118000,
            featuredBadge = "AŞKIN FREKANSI",
            songTitle = "Aşkın Frekansı",
            artistName = "Romantik Türkçe Şarkılar"
        ),
        RadioStation(
            id = "power_fm",
            name = "Power FM",
            streamUrl = "https://listen.powerapp.com.tr/powerfm/icecast.audio",
            backupStreamUrl = "https://powerfm.listenpowerapp.com/powerfm/icecast.audio",
            category = RadioCategory.YABANCI,
            frequencyOrCity = "100.0 FM • Ulusal",
            accentColorHex = 0xFF00E5FF,
            iconEmoji = "🔥",
            listenersCount = 112000,
            featuredBadge = "DÜNYA HİTLERİ",
            songTitle = "The Power of Music",
            artistName = "Global Top 40"
        ),
        RadioStation(
            id = "super_fm",
            name = "Süper FM",
            streamUrl = "https://17733.live.streamtheworld.com/SUPER_FM_SC",
            backupStreamUrl = "https://superfm.canliradyodinle.fm/stream",
            category = RadioCategory.ULUSAL,
            frequencyOrCity = "90.8 FM • Ulusal",
            accentColorHex = 0xFFFF6D00,
            iconEmoji = "🌟",
            listenersCount = 98000,
            featuredBadge = "CANLI",
            songTitle = "En Süper Şarkılar",
            artistName = "Hareketli Türkçe Pop"
        ),
        RadioStation(
            id = "fenomen",
            name = "Radyo Fenomen",
            streamUrl = "https://listen.powerapp.com.tr/fenomen/icecast.audio",
            backupStreamUrl = "https://fenomen.listenpowerapp.com/fenomen/icecast.audio",
            category = RadioCategory.YABANCI,
            frequencyOrCity = "100.4 FM • Ulusal",
            accentColorHex = 0xFF76FF03,
            iconEmoji = "🎧",
            listenersCount = 94000,
            featuredBadge = "GENÇLİK",
            songTitle = "Maksimum Hit Müzik",
            artistName = "Fenomen Club & Dance"
        ),
        RadioStation(
            id = "joy_fm",
            name = "Joy FM",
            streamUrl = "https://17733.live.streamtheworld.com/JOY_FM_SC",
            category = RadioCategory.SLOW,
            frequencyOrCity = "100.6 FM • İstanbul",
            accentColorHex = 0xFFD81B60,
            iconEmoji = "🎷",
            listenersCount = 89000,
            featuredBadge = "LOUNGE",
            songTitle = "Easy Listening & Jazz",
            artistName = "Sakin Dinleti"
        ),
        RadioStation(
            id = "best_fm",
            name = "Best FM",
            streamUrl = "https://stream.bestfm.com.tr/bestfm.aac",
            category = RadioCategory.ULUSAL,
            frequencyOrCity = "98.4 FM • Ulusal",
            accentColorHex = 0xFF2979FF,
            iconEmoji = "📻",
            listenersCount = 86000,
            featuredBadge = "KLASİK",
            songTitle = "Radyonuz Açık Olsun",
            artistName = "Türkçe Pop & Sohbet"
        ),
        RadioStation(
            id = "alem_fm",
            name = "Alem FM",
            streamUrl = "https://turkmedya.radyotvonline.net/alemfm",
            category = RadioCategory.ULUSAL,
            frequencyOrCity = "89.2 FM • Ulusal",
            accentColorHex = 0xFF00B0FF,
            iconEmoji = "🌍",
            listenersCount = 84000,
            featuredBadge = "SOHBET",
            songTitle = "Alem Buysa",
            artistName = "Adem Metan Show & Hit Müzik"
        ),
        RadioStation(
            id = "show_radyo",
            name = "Show Radyo",
            streamUrl = "https://stream.showradyo.com.tr/showradyo.aac",
            category = RadioCategory.ULUSAL,
            frequencyOrCity = "89.8 FM • Ulusal",
            accentColorHex = 0xFFFF5252,
            iconEmoji = "🎭",
            listenersCount = 76000,
            featuredBadge = "CANLI",
            songTitle = "Türk Müziğinin Şovu",
            artistName = "Hit Türkçe Müzik"
        ),
        RadioStation(
            id = "radyo_d",
            name = "Radyo D",
            streamUrl = "https://radyo.duhnet.tv/radyod",
            category = RadioCategory.ULUSAL,
            frequencyOrCity = "104.0 FM • Ulusal",
            accentColorHex = 0xFF304FFE,
            iconEmoji = "🎯",
            listenersCount = 72000,
            featuredBadge = "POPÜLER",
            songTitle = "Müziğin Rengi",
            artistName = "Güncel Pop Parçalar"
        ),
        RadioStation(
            id = "trt_radyo1",
            name = "TRT Radyo 1",
            streamUrl = "http://stream.radyotvonline.net/trtradyo1/playlist.m3u8",
            category = RadioCategory.HABER_SPOR,
            frequencyOrCity = "95.6 FM • Türkiye",
            accentColorHex = 0xFF37474F,
            iconEmoji = "🎙️",
            listenersCount = 79000,
            featuredBadge = "KÜLTÜR & HABER",
            songTitle = "Dünyadan ve Türkiye'den",
            artistName = "TRT Haber ve Kültür Programları"
        ),
        RadioStation(
            id = "trt_nagme",
            name = "TRT Nağme",
            streamUrl = "http://stream.radyotvonline.net/trtnagme/playlist.m3u8",
            category = RadioCategory.NOSTALJI,
            frequencyOrCity = "101.6 FM • Ulusal",
            accentColorHex = 0xFF5D4037,
            iconEmoji = "🎻",
            listenersCount = 68000,
            featuredBadge = "SANAT MÜZİĞİ",
            songTitle = "Gönülden Nağmeler",
            artistName = "Klasik Türk Müziği Korosu"
        ),
        RadioStation(
            id = "a_spor_radyo",
            name = "A Spor Radyo",
            streamUrl = "https://radyoaspor.ercdn.net/asporradyo/chunklist.m3u8",
            category = RadioCategory.HABER_SPOR,
            frequencyOrCity = "93.0 FM • Ulusal",
            accentColorHex = 0xFF00C853,
            iconEmoji = "⚽",
            listenersCount = 82000,
            featuredBadge = "CANLI MAÇ",
            songTitle = "Süper Lig & Spor Gündemi",
            artistName = "Canlı Maç Anlatımı & Yorumlar"
        ),
        RadioStation(
            id = "ntv_radyo",
            name = "NTV Radyo",
            streamUrl = "https://dygvideo.ercdn.net/ntvradyo/chunklist.m3u8",
            category = RadioCategory.HABER_SPOR,
            frequencyOrCity = "102.8 FM • Ulusal",
            accentColorHex = 0xFFD50000,
            iconEmoji = "📰",
            listenersCount = 77000,
            featuredBadge = "HABER",
            songTitle = "Son Dakika ve Analiz",
            artistName = "NTV Radyo Canlı Haber Kuşağı"
        ),

        // === KÜRTÇE YAYIN YAPAN İSTASYONLAR ===
        RadioStation(
            id = "trt_kurdi",
            name = "TRT Kurdî Radyo",
            streamUrl = "http://stream.radyotvonline.net/trtkurdi/playlist.m3u8",
            backupStreamUrl = "https://kesintisiz.radyotvonline.net/trtkurdi",
            category = RadioCategory.KURTCE,
            frequencyOrCity = "104.5 FM • Diyarbakır & Bölgesel",
            accentColorHex = 0xFF00C853,
            iconEmoji = "☀️",
            listenersCount = 104000,
            featuredBadge = "DENGÊ KURDÎ",
            songTitle = "Kilam û Dengbêjî",
            artistName = "TRT Kurdî Zindî"
        ),
        RadioStation(
            id = "denge_kurdi",
            name = "Radyo Dengê Kurdî",
            streamUrl = "https://stream.dengekurdi.com/live",
            backupStreamUrl = "http://stream.radyotvonline.net/trtkurdi/playlist.m3u8",
            category = RadioCategory.KURTCE,
            frequencyOrCity = "İnternet & Uydu • Diyarbakır",
            accentColorHex = 0xFFFFD600,
            iconEmoji = "🎶",
            listenersCount = 92000,
            featuredBadge = "KLASÎK & STRAN",
            songTitle = "Stranên Gelêrî yên Nemir",
            artistName = "Şivan Perwer, Ciwan Haco, Xelîl Xemgîn"
        ),
        RadioStation(
            id = "damla_kurdi",
            name = "Damla FM (Kürtçe Ezgiler)",
            streamUrl = "http://yayin.damlafm.com.tr:8000/stream",
            backupStreamUrl = "https://dygvideo.ercdn.net/kralfm/chunklist.m3u8",
            category = RadioCategory.KURTCE,
            frequencyOrCity = "87.5 FM • İstanbul & Doğu",
            accentColorHex = 0xFFFF6D00,
            iconEmoji = "🌾",
            listenersCount = 74000,
            featuredBadge = "EZGİLER",
            songTitle = "Bölgesel Ezgiler ve Halaylar",
            artistName = "Kürtçe Türküler & Govend"
        ),
        RadioStation(
            id = "radyo_hevi",
            name = "Radyo Hêvî",
            streamUrl = "https://stream.radyohevi.com/live",
            backupStreamUrl = "http://stream.radyotvonline.net/trtkurdi/playlist.m3u8",
            category = RadioCategory.KURTCE,
            frequencyOrCity = "Kültür & Sanat • Van & Mardin",
            accentColorHex = 0xFF6200EA,
            iconEmoji = "🪕",
            listenersCount = 61000,
            featuredBadge = "HÊVÎYA WE",
            songTitle = "Hevpeyvîn û Muzîka Kurdî",
            artistName = "Kültür, Edebiyat ve Müzik"
        ),
        RadioStation(
            id = "rudaw_radio",
            name = "Rûdaw Radio Kurdî",
            streamUrl = "https://rudaw.ercdn.net/rudawradio/chunklist.m3u8",
            backupStreamUrl = "http://stream.radyotvonline.net/trtkurdi/playlist.m3u8",
            category = RadioCategory.KURTCE,
            frequencyOrCity = "Bölgesel & Uydu • Erbil & Diyarbakır",
            accentColorHex = 0xFFFF3D00,
            iconEmoji = "📡",
            listenersCount = 88000,
            featuredBadge = "ZINDÎ",
            songTitle = "Nûçeyên Zindî û Muzîk",
            artistName = "Rûdaw Radyo Canlı"
        ),
        RadioStation(
            id = "waar_radio",
            name = "Waar Radyo Kurdî",
            streamUrl = "https://stream.waarradio.com/live",
            backupStreamUrl = "https://rudaw.ercdn.net/rudawradio/chunklist.m3u8",
            category = RadioCategory.KURTCE,
            frequencyOrCity = "Bölgesel • Şırnak & Duhok",
            accentColorHex = 0xFF00E676,
            iconEmoji = "🌿",
            listenersCount = 53000,
            featuredBadge = "FOLKLOR",
            songTitle = "Stranên Herêmî",
            artistName = "Kürtçe Folklorik Ezgiler"
        ),
        RadioStation(
            id = "vin_fm",
            name = "Vin FM Kurdî",
            streamUrl = "https://stream.vinfm.com/live",
            backupStreamUrl = "http://stream.radyotvonline.net/trtkurdi/playlist.m3u8",
            category = RadioCategory.KURTCE,
            frequencyOrCity = "Modern Kürtçe Pop • Gençlik",
            accentColorHex = 0xFFE040FB,
            iconEmoji = "✨",
            listenersCount = 69000,
            featuredBadge = "POP HÎT",
            songTitle = "Muzîka Nûjen a Kurdî",
            artistName = "Modern Kürtçe Hit Listesi"
        ),

        // === YEREL RADYOLAR ===
        RadioStation(
            id = "karadeniz_fm",
            name = "Karadeniz FM",
            streamUrl = "https://stream.karadenizfm.com/stream",
            backupStreamUrl = "https://turkmedya.radyotvonline.net/alemfm",
            category = RadioCategory.YEREL,
            frequencyOrCity = "98.2 FM • Trabzon & Karadeniz",
            accentColorHex = 0xFF00B0FF,
            iconEmoji = "🌊",
            listenersCount = 81000,
            featuredBadge = "KARADENİZ",
            songTitle = "Kemençe ve Tulumun Sesi",
            artistName = "Karadeniz Türküleri & Horon"
        ),
        RadioStation(
            id = "radyo_ege",
            name = "Radyo Ege",
            streamUrl = "https://stream.radyoege.com.tr/stream",
            backupStreamUrl = "https://radyo.duhnet.tv/radyod",
            category = RadioCategory.YEREL,
            frequencyOrCity = "92.7 FM • İzmir & Ege Bölgesi",
            accentColorHex = 0xFF00E5FF,
            iconEmoji = "☀️",
            listenersCount = 67000,
            featuredBadge = "EGE'NİN SESİ",
            songTitle = "İzmir Rüzgarı",
            artistName = "Ege Türküleri ve Pop Müzik"
        ),
        RadioStation(
            id = "olay_fm",
            name = "Bursa Olay FM",
            streamUrl = "http://yayin.olayfm.com.tr:8000/stream",
            backupStreamUrl = "https://stream.showradyo.com.tr/showradyo.aac",
            category = RadioCategory.YEREL,
            frequencyOrCity = "90.5 FM • Bursa & Marmara",
            accentColorHex = 0xFF43A047,
            iconEmoji = "🌲",
            listenersCount = 59000,
            featuredBadge = "BURSA",
            songTitle = "Marmara'nın Kalbi",
            artistName = "Güncel Yerel Haber & Müzik"
        ),
        RadioStation(
            id = "ankara_kent",
            name = "Ankara Kent Radyo",
            streamUrl = "https://stream.radyotvonline.net/trtankara/playlist.m3u8",
            backupStreamUrl = "http://stream.radyotvonline.net/trtfm/playlist.m3u8",
            category = RadioCategory.YEREL,
            frequencyOrCity = "105.6 FM • Ankara Başkent",
            accentColorHex = 0xFFFF5722,
            iconEmoji = "🏛️",
            listenersCount = 64000,
            featuredBadge = "BAŞKENT",
            songTitle = "Başkent Gündemi ve Seymen Havaları",
            artistName = "Ankara Havaları & Kültür"
        ),
        RadioStation(
            id = "akdeniz_fm",
            name = "Akdeniz FM",
            streamUrl = "http://yayin.akdenizfm.com.tr:8000/stream",
            backupStreamUrl = "https://17733.live.streamtheworld.com/SUPER_FM_SC",
            category = RadioCategory.YEREL,
            frequencyOrCity = "91.3 FM • Antalya & Akdeniz",
            accentColorHex = 0xFF00ACC1,
            iconEmoji = "🌴",
            listenersCount = 52000,
            featuredBadge = "ANTALYA",
            songTitle = "Akdeniz Akşamları",
            artistName = "Yaz Şarkıları & Pop"
        ),
        RadioStation(
            id = "radyo_34",
            name = "Radyo 34 İstanbul",
            streamUrl = "http://yayin.radyo34.com.tr:8000/stream",
            backupStreamUrl = "https://dygvideo.ercdn.net/kralfm/chunklist.m3u8",
            category = RadioCategory.YEREL,
            frequencyOrCity = "88.0 FM • İstanbul Metropol",
            accentColorHex = 0xFFE91E63,
            iconEmoji = "🌉",
            listenersCount = 58000,
            featuredBadge = "İSTANBUL",
            songTitle = "Boğazın Melodileri",
            artistName = "İstanbul Radyosu Hitleri"
        ),
        RadioStation(
            id = "diyarbakir_can",
            name = "Diyarbakır Can Radyo",
            streamUrl = "http://yayin.canradyo.com.tr:8000/stream",
            backupStreamUrl = "http://stream.radyotvonline.net/trtkurdi/playlist.m3u8",
            category = RadioCategory.YEREL,
            frequencyOrCity = "94.0 FM • Diyarbakır & Güneydoğu",
            accentColorHex = 0xFFFF9800,
            iconEmoji = "🏰",
            listenersCount = 62000,
            featuredBadge = "DİYARBAKIR",
            songTitle = "Dicle'nin Ezgileri",
            artistName = "Bölgesel Türküler & Kültür"
        ),
        RadioStation(
            id = "gozde_fm",
            name = "Gaziantep Gözde FM",
            streamUrl = "http://yayin.gozdefm.com.tr:8000/stream",
            backupStreamUrl = "https://turkmedya.radyotvonline.net/alemfm",
            category = RadioCategory.YEREL,
            frequencyOrCity = "92.4 FM • Gaziantep",
            accentColorHex = 0xFF795548,
            iconEmoji = "🏺",
            listenersCount = 47000,
            featuredBadge = "GAZİANTEP",
            songTitle = "Güneydoğu Rüzgarı",
            artistName = "Barak Havaları & Türk Pop"
        ),
        RadioStation(
            id = "cukurova_fm",
            name = "Adana Çukurova FM",
            streamUrl = "http://yayin.cukurovafm.com.tr:8000/stream",
            backupStreamUrl = "https://radyo.duhnet.tv/slowturk",
            category = RadioCategory.YEREL,
            frequencyOrCity = "97.0 FM • Adana & Çukurova",
            accentColorHex = 0xFFFFC107,
            iconEmoji = "🍊",
            listenersCount = 49000,
            featuredBadge = "ÇUKUROVA",
            songTitle = "Torosların Sesi",
            artistName = "Bölgesel Türkü & Pop"
        ),

        // === DİĞER KATEGORİLER ===
        RadioStation(
            id = "moral_fm",
            name = "Moral FM",
            streamUrl = "http://yayin.moralfm.com.tr:8000/stream",
            backupStreamUrl = "http://stream.radyotvonline.net/trtradyo1/playlist.m3u8",
            category = RadioCategory.DINI,
            frequencyOrCity = "105.0 FM • Dini & Sohbet",
            accentColorHex = 0xFF00897B,
            iconEmoji = "🌙",
            listenersCount = 48000,
            featuredBadge = "HUZUR",
            songTitle = "Gönül Dünyamız",
            artistName = "Tasavvuf Müziği & Sohbet"
        ),
        RadioStation(
            id = "baba_radyo",
            name = "Baba Radyo",
            streamUrl = "https://stream.babaradyo.com/stream",
            backupStreamUrl = "https://dygvideo.ercdn.net/kralfm/chunklist.m3u8",
            category = RadioCategory.NOSTALJI,
            frequencyOrCity = "105.6 FM • Nostalji & Damar",
            accentColorHex = 0xFF6D4C41,
            iconEmoji = "👑",
            listenersCount = 63000,
            featuredBadge = "BABALAR",
            songTitle = "Müslüm Gürses, Orhan Gencebay, Ferdi Tayfur",
            artistName = "Efsane Damar Şarkılar"
        )
    )

    init {
        loadPreferences()
    }

    private fun loadPreferences() {
        // Load favorites
        val favSet = prefs.getStringSet("favorites_set", setOf("trt_fm", "kral_fm", "trt_kurdi", "power_turk")) ?: emptySet()
        _favoritesFlow.value = favSet

        // Load custom stations
        val customJson = prefs.getString("custom_stations_json", "[]") ?: "[]"
        val customList = mutableListOf<RadioStation>()
        try {
            val jsonArr = JSONArray(customJson)
            for (i in 0 until jsonArr.length()) {
                val obj = jsonArr.getJSONObject(i)
                customList.add(
                    RadioStation(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        streamUrl = obj.getString("streamUrl"),
                        backupStreamUrl = obj.optString("backupStreamUrl", ""),
                        category = RadioCategory.valueOf(obj.optString("category", RadioCategory.CUSTOM.name)),
                        frequencyOrCity = obj.optString("frequencyOrCity", "Özel İstasyon"),
                        accentColorHex = obj.optLong("accentColorHex", 0xFF00E5FF),
                        iconEmoji = obj.optString("iconEmoji", "✨"),
                        isCustom = true,
                        listenersCount = 1,
                        featuredBadge = "ÖZEL",
                        dateAdded = obj.optLong("dateAdded", System.currentTimeMillis())
                    )
                )
            }
        } catch (_: Exception) {}
        _customStationsFlow.value = customList

        // Load reminders
        val remindersJson = prefs.getString("reminders_json", null)
        val reminderList = mutableListOf<RadioReminder>()
        if (remindersJson != null) {
            try {
                val jsonArr = JSONArray(remindersJson)
                for (i in 0 until jsonArr.length()) {
                    val obj = jsonArr.getJSONObject(i)
                    reminderList.add(
                        RadioReminder(
                            id = obj.getString("id"),
                            stationId = obj.getString("stationId"),
                            stationName = obj.getString("stationName"),
                            programTitle = obj.getString("programTitle"),
                            timeHour = obj.getInt("timeHour"),
                            timeMinute = obj.getInt("timeMinute"),
                            daysDescription = obj.optString("daysDescription", "Her Gün"),
                            isEnabled = obj.optBoolean("isEnabled", true)
                        )
                    )
                }
            } catch (_: Exception) {}
        } else {
            // Default sample reminders so user immediately sees how it works
            reminderList.add(
                RadioReminder(
                    id = "rem_1",
                    stationId = "kral_fm",
                    stationName = "Kral FM",
                    programTitle = "Mehmet'in Gezegeni Sohbeti",
                    timeHour = 16,
                    timeMinute = 0,
                    daysDescription = "Hafta İçi"
                )
            )
            reminderList.add(
                RadioReminder(
                    id = "rem_2",
                    stationId = "trt_kurdi",
                    stationName = "TRT Kurdî Radyo",
                    programTitle = "Şeva Dengbêjan (Dengbêj Gecesi)",
                    timeHour = 20,
                    timeMinute = 30,
                    daysDescription = "Her Cuma"
                )
            )
        }
        _remindersFlow.value = reminderList

        // Load theme
        val savedTheme = prefs.getString("live_theme", LiveTheme.NEON_CYBER.name)
        _liveThemeFlow.value = try {
            LiveTheme.valueOf(savedTheme ?: LiveTheme.NEON_CYBER.name)
        } catch (_: Exception) {
            LiveTheme.NEON_CYBER
        }

        // Load audio quality
        val savedQuality = prefs.getString("audio_quality", AudioQuality.HIGH.name)
        _audioQualityFlow.value = try {
            AudioQuality.valueOf(savedQuality ?: AudioQuality.HIGH.name)
        } catch (_: Exception) {
            AudioQuality.HIGH
        }

        // Load dark mode
        _isDarkModeFlow.value = prefs.getBoolean("is_dark_mode", true)
    }

    fun getAllStations(): List<RadioStation> {
        return defaultStations + _customStationsFlow.value
    }

    fun toggleFavorite(stationId: String) {
        val current = _favoritesFlow.value.toMutableSet()
        if (current.contains(stationId)) {
            current.remove(stationId)
        } else {
            current.add(stationId)
        }
        _favoritesFlow.value = current
        prefs.edit().putStringSet("favorites_set", current).apply()
    }

    fun isFavorite(stationId: String): Boolean {
        return _favoritesFlow.value.contains(stationId)
    }

    fun addCustomStation(station: RadioStation) {
        val updated = _customStationsFlow.value.toMutableList()
        updated.add(0, station)
        _customStationsFlow.value = updated
        saveCustomStations(updated)
    }

    fun deleteCustomStation(stationId: String) {
        val updated = _customStationsFlow.value.filter { it.id != stationId }
        _customStationsFlow.value = updated
        saveCustomStations(updated)
    }

    private fun saveCustomStations(list: List<RadioStation>) {
        val jsonArr = JSONArray()
        for (st in list) {
            val obj = JSONObject()
            obj.put("id", st.id)
            obj.put("name", st.name)
            obj.put("streamUrl", st.streamUrl)
            obj.put("backupStreamUrl", st.backupStreamUrl)
            obj.put("category", st.category.name)
            obj.put("frequencyOrCity", st.frequencyOrCity)
            obj.put("accentColorHex", st.accentColorHex)
            obj.put("iconEmoji", st.iconEmoji)
            obj.put("dateAdded", st.dateAdded)
            jsonArr.put(obj)
        }
        prefs.edit().putString("custom_stations_json", jsonArr.toString()).apply()
    }

    fun addReminder(reminder: RadioReminder) {
        val current = _remindersFlow.value.toMutableList()
        current.add(0, reminder)
        _remindersFlow.value = current
        saveReminders(current)
    }

    fun removeReminder(reminderId: String) {
        val current = _remindersFlow.value.filter { it.id != reminderId }
        _remindersFlow.value = current
        saveReminders(current)
    }

    fun toggleReminder(reminderId: String) {
        val current = _remindersFlow.value.map {
            if (it.id == reminderId) it.copy(isEnabled = !it.isEnabled) else it
        }
        _remindersFlow.value = current
        saveReminders(current)
    }

    private fun saveReminders(list: List<RadioReminder>) {
        val jsonArr = JSONArray()
        for (r in list) {
            val obj = JSONObject()
            obj.put("id", r.id)
            obj.put("stationId", r.stationId)
            obj.put("stationName", r.stationName)
            obj.put("programTitle", r.programTitle)
            obj.put("timeHour", r.timeHour)
            obj.put("timeMinute", r.timeMinute)
            obj.put("daysDescription", r.daysDescription)
            obj.put("isEnabled", r.isEnabled)
            jsonArr.put(obj)
        }
        prefs.edit().putString("reminders_json", jsonArr.toString()).apply()
    }

    fun setLiveTheme(theme: LiveTheme) {
        _liveThemeFlow.value = theme
        prefs.edit().putString("live_theme", theme.name).apply()
    }

    fun setAudioQuality(quality: AudioQuality) {
        _audioQualityFlow.value = quality
        prefs.edit().putString("audio_quality", quality.name).apply()
    }

    fun setDarkMode(isDark: Boolean) {
        _isDarkModeFlow.value = isDark
        prefs.edit().putBoolean("is_dark_mode", isDark).apply()
    }
}
