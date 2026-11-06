package com.example.model

enum class RadioCategory(val id: String, val title: String, val icon: String) {
    ALL("all", "Tümü", "📻"),
    FAVORITES("favorites", "Favorilerim", "⭐"),
    ULUSAL("ulusal", "Ulusal", "🇹🇷"),
    YEREL("yerel", "Yerel", "📍"),
    KURTCE("kurtce", "Kürtçe", "☀️"),
    HABER_SPOR("haber_spor", "Haber & Spor", "📰"),
    SLOW("slow", "Slow & Akustik", "💖"),
    NOSTALJI("nostalji", "Nostalji", "📼"),
    DINI("dini", "Dini & İlahi", "🌙"),
    YABANCI("yabanci", "Yabancı Hit", "🌍"),
    CUSTOM("custom", "Kendi Radyolarım", "✨")
}

enum class SortOption(val title: String) {
    POPULARITY("En Çok Dinlenen"),
    NAME_AZ("İsim (A-Z)"),
    NAME_ZA("İsim (Z-A)"),
    DATE_ADDED("Eklenme Tarihi")
}

enum class AudioQuality(val title: String, val description: String, val approxBitrate: String) {
    AUTO("Otomatik", "Bağlantı hızına göre akıllı ayar", "Dinamik"),
    LOW("Veri Tasarrufu", "Düşük veri tüketimi (Mobil veri dostu)", "64 kbps"),
    STANDARD("Standart Kalite", "Dengeli ses ve hızlı ara belleğe alma", "128 kbps"),
    HIGH("Yüksek HD Ses", "Stüdyo netliğinde zengin akustik", "320 kbps")
}

enum class LiveTheme(
    val title: String,
    val subtitle: String,
    val primaryColorHex: Long,
    val secondaryColorHex: Long,
    val bgGradientStart: Long,
    val bgGradientEnd: Long
) {
    NEON_CYBER(
        title = "Neon Gece",
        subtitle = "Cyberpunk neon titreşimi ve siber ışıltılar",
        primaryColorHex = 0xFF00E5FF,
        secondaryColorHex = 0xFFFF007F,
        bgGradientStart = 0xFF0B0F19,
        bgGradientEnd = 0xFF140D26
    ),
    AURORA(
        title = "Kuzey Işıkları",
        subtitle = "Zümrüt yeşili ve mor dalgalanan gökyüzü",
        primaryColorHex = 0xFF00E676,
        secondaryColorHex = 0xFF7C4DFF,
        bgGradientStart = 0xFF03141A,
        bgGradientEnd = 0xFF120826
    ),
    COSMIC(
        title = "Derin Kozmos",
        subtitle = "Yıldız tozu, nebulalar ve galaksi ışıltısı",
        primaryColorHex = 0xFFB388FF,
        secondaryColorHex = 0xFF3D5AFE,
        bgGradientStart = 0xFF0A071A,
        bgGradientEnd = 0xFF1B0B2E
    ),
    RETRO_AMBER(
        title = "Retro Kaset",
        subtitle = "Sıcak kehribar lambalı radyo nostaljisi",
        primaryColorHex = 0xFFFFAB00,
        secondaryColorHex = 0xFFFF3D00,
        bgGradientStart = 0xFF1F1206,
        bgGradientEnd = 0xFF120803
    ),
    MIDNIGHT_OCEAN(
        title = "Gece Okyanusu",
        subtitle = "Derin okyanus mavisi ve dingin su dalgaları",
        primaryColorHex = 0xFF00B0FF,
        secondaryColorHex = 0xFF1DE9B6,
        bgGradientStart = 0xFF03101C,
        bgGradientEnd = 0xFF051D2D
    )
}

data class RadioStation(
    val id: String,
    val name: String,
    val streamUrl: String,
    val backupStreamUrl: String = "",
    val category: RadioCategory,
    val frequencyOrCity: String,
    val accentColorHex: Long,
    val iconEmoji: String,
    val isCustom: Boolean = false,
    val listenersCount: Int = 12000,
    val featuredBadge: String? = null,
    val dateAdded: Long = System.currentTimeMillis(),
    val songTitle: String? = null,
    val artistName: String? = null
)

data class RadioReminder(
    val id: String,
    val stationId: String,
    val stationName: String,
    val programTitle: String,
    val timeHour: Int,
    val timeMinute: Int,
    val daysDescription: String = "Her Gün",
    val isEnabled: Boolean = true
) {
    val formattedTime: String
        get() = "%02d:%02d".format(timeHour, timeMinute)
}

enum class PlaybackStatus {
    IDLE,
    BUFFERING,
    PLAYING,
    PAUSED,
    ERROR
}

data class PlaybackState(
    val status: PlaybackStatus = PlaybackStatus.IDLE,
    val currentStation: RadioStation? = null,
    val errorMessage: String? = null,
    val sleepTimerMinutesLeft: Int? = null,
    val sleepTimerTotalMinutes: Int? = null,
    val isMuted: Boolean = false,
    val volume: Float = 1.0f,
    val audioFrequencies: List<Float> = List(16) { 0.1f }
)
