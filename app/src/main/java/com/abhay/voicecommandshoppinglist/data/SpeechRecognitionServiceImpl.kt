package com.abhay.voicecommandshoppinglist.data

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.abhay.voicecommandshoppinglist.domain.util.Result
import com.abhay.voicecommandshoppinglist.domain.model.SpeechRecognitionService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

class SpeechRecognitionServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val nlpProcessor: NLPProcessor
) : SpeechRecognitionService {

    private var speechRecognizer: SpeechRecognizer? =
        SpeechRecognizer.createSpeechRecognizer(context)
    private var recognitionListener: RecognitionListener? = null

    override fun startListening(listener: (Result<String>) -> Unit) {
        try {
            if (speechRecognizer == null) {
                listener(Result.Error("Failed to create speech recognizer"))
                return
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES, true)
            }

            recognitionListener = object : RecognitionListener {

                override fun onError(error: Int) {
                    val errorMessage = when (error) {
                        SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                        SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                        SpeechRecognizer.ERROR_NETWORK -> "Network error"
                        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                        SpeechRecognizer.ERROR_NO_MATCH -> "No speech input"
                        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognition service busy"
                        SpeechRecognizer.ERROR_SERVER -> "Server error"
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                        else -> "Unknown error: $error"
                    }
                    listener(Result.Error(errorMessage))
                }

                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (matches.isNullOrEmpty()) {
                        listener(Result.Error("No speech recognition results"))
                        return
                    }

                    matches.firstOrNull()?.let { userInput ->
                        CoroutineScope(Dispatchers.IO).launch {
                            val processedResult = nlpProcessor.processText(userInput)
                            withContext(Dispatchers.Main) {
                                listener(processedResult)
                            }
                        }
                    }
                }

                override fun onPartialResults(p0: Bundle?) {}
                override fun onEvent(p0: Int, p1: Bundle?) {}
                override fun onReadyForSpeech(p0: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(p0: Float) {}
                override fun onBufferReceived(p0: ByteArray?) {}
                override fun onEndOfSpeech() {}
            }

            speechRecognizer!!.setRecognitionListener(recognitionListener)
            speechRecognizer!!.startListening(intent)

        } catch (e: Exception) {
            listener(Result.Error("Failed to start speech recognition: ${e.message}"))
        }
    }

    override fun stopListening(): Result<Unit> {
        return try {
            speechRecognizer?.stopListening()
            speechRecognizer?.destroy()
            speechRecognizer = null
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to stop speech recognition: ${e.message}")
        }
    }
}
