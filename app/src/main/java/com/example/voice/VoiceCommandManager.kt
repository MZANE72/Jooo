package com.example.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.example.model.RadioStation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

sealed class VoiceState {
    object Idle : VoiceState()
    object Listening : VoiceState()
    data class Recognized(val spokenText: String, val matchedAction: String) : VoiceState()
    data class Error(val message: String) : VoiceState()
}

sealed class VoiceActionResult {
    data class PlayStation(val station: RadioStation) : VoiceActionResult()
    data class SearchQuery(val query: String) : VoiceActionResult()
    object PausePlayback : VoiceActionResult()
    object ResumePlayback : VoiceActionResult()
    object NextStation : VoiceActionResult()
    object PreviousStation : VoiceActionResult()
    object ToggleFavoriteCurrent : VoiceActionResult()
    data class Unrecognized(val rawText: String) : VoiceActionResult()
}

class VoiceCommandManager(private val context: Context) {

    private var speechRecognizer: SpeechRecognizer? = null
    private val _voiceState = MutableStateFlow<VoiceState>(VoiceState.Idle)
    val voiceState: StateFlow<VoiceState> = _voiceState.asStateFlow()

    private var onResultAction: ((VoiceActionResult) -> Unit)? = null

    fun isSpeechRecognitionAvailable(): Boolean {
        return SpeechRecognizer.isRecognitionAvailable(context)
    }

    fun startListening(
        allStations: List<RadioStation>,
        onAction: (VoiceActionResult) -> Unit
    ) {
        onResultAction = onAction
        stopListening()

        if (!isSpeechRecognitionAvailable()) {
            _voiceState.value = VoiceState.Error("Bu cihazda ses tanıma servisi bulunamadı.")
            return
        }

        try {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        _voiceState.value = VoiceState.Listening
                    }

                    override fun onBeginningOfSpeech() {}
                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {}

                    override fun onError(error: Int) {
                        val message = when (error) {
                            SpeechRecognizer.ERROR_AUDIO -> "Ses kayıt hatası"
                            SpeechRecognizer.ERROR_CLIENT -> "İstemci hatası"
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Mikrofon izni gerekli"
                            SpeechRecognizer.ERROR_NETWORK -> "Ağ hatası"
                            SpeechRecognizer.ERROR_NO_MATCH -> "Ses anlaşılamadı, tekrar deneyin"
                            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Ses servisi meşgul"
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Ses algılanamadı (zaman aşımı)"
                            else -> "Ses algılanamadı ($error)"
                        }
                        _voiceState.value = VoiceState.Error(message)
                    }

                    override fun onResults(results: Bundle?) {
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull()?.trim()
                        if (!text.isNullOrBlank()) {
                            processSpokenCommand(text, allStations)
                        } else {
                            _voiceState.value = VoiceState.Error("Ses anlaşılamadı")
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {}
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "tr-TR")
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "tr-TR")
                putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, false)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            }
            speechRecognizer?.startListening(intent)
            _voiceState.value = VoiceState.Listening
        } catch (e: Exception) {
            _voiceState.value = VoiceState.Error("Ses dinleme başlatılamadı: ${e.localizedMessage}")
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.cancel()
            speechRecognizer?.destroy()
        } catch (_: Exception) {}
        speechRecognizer = null
        if (_voiceState.value is VoiceState.Listening) {
            _voiceState.value = VoiceState.Idle
        }
    }

    fun processSpokenCommand(rawText: String, stations: List<RadioStation>) {
        val lower = rawText.lowercase(Locale("tr", "TR")).trim()

        // 1. Playback controls
        if (lower in listOf("durdur", "kapat", "dur", "kes", "sessize al")) {
            _voiceState.value = VoiceState.Recognized(rawText, "Radyo Durduruldu")
            onResultAction?.invoke(VoiceActionResult.PausePlayback)
            return
        }

        if (lower in listOf("oynat", "başlat", "devam et", "çalıştır", "aç")) {
            _voiceState.value = VoiceState.Recognized(rawText, "Oynatma Başlatıldı")
            onResultAction?.invoke(VoiceActionResult.ResumePlayback)
            return
        }

        if (lower in listOf("sıradaki", "sonraki", "diğer", "ileri")) {
            _voiceState.value = VoiceState.Recognized(rawText, "Sonraki İstasyon")
            onResultAction?.invoke(VoiceActionResult.NextStation)
            return
        }

        if (lower in listOf("önceki", "geri")) {
            _voiceState.value = VoiceState.Recognized(rawText, "Önceki İstasyon")
            onResultAction?.invoke(VoiceActionResult.PreviousStation)
            return
        }

        if (lower.contains("favori") || lower.contains("beğen")) {
            _voiceState.value = VoiceState.Recognized(rawText, "Favorilere Eklendi/Çıkarıldı")
            onResultAction?.invoke(VoiceActionResult.ToggleFavoriteCurrent)
            return
        }

        // 2. Direct station matches: e.g. "Kral FM aç", "Slow Türk çal", "TRT Kurdî", "Power FM"
        val cleaned = lower
            .replace("aç", "")
            .replace("çal", "")
            .replace("dinlet", "")
            .replace("radyosunu", "")
            .replace("radyosu", "")
            .replace("istasyonunu", "")
            .trim()

        // Match exact or contains station name
        val matchedStation = stations.firstOrNull { station ->
            val stName = station.name.lowercase(Locale("tr", "TR"))
            stName == cleaned ||
            stName.contains(cleaned) ||
            cleaned.contains(stName) ||
            (cleaned.contains("trt") && cleaned.contains("kurdi") && station.id == "trt_kurdi") ||
            (cleaned.contains("denge kurdi") && station.id == "denge_kurdi") ||
            (cleaned.contains("kral") && station.id == "kral_fm") ||
            (cleaned.contains("power turk") && station.id == "power_turk") ||
            (cleaned.contains("slow") && station.id == "slow_turk") ||
            (cleaned.contains("karadeniz") && station.id == "karadeniz_fm")
        }

        if (matchedStation != null) {
            _voiceState.value = VoiceState.Recognized(rawText, "${matchedStation.name} Açılıyor...")
            onResultAction?.invoke(VoiceActionResult.PlayStation(matchedStation))
            return
        }

        // 3. Search query: "ara ...", "şarkı ...", "türkü ..."
        val searchQuery = if (lower.startsWith("ara ")) {
            rawText.substringAfter("ara ").trim()
        } else if (lower.contains("ara")) {
            rawText.replace("ara", "").trim()
        } else {
            rawText
        }

        _voiceState.value = VoiceState.Recognized(rawText, "\"$searchQuery\" Aranıyor")
        onResultAction?.invoke(VoiceActionResult.SearchQuery(searchQuery))
    }
}
