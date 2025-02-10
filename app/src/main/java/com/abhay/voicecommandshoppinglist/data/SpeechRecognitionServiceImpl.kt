package com.abhay.voicecommandshoppinglist.data

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
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

    override fun startListening(listener: (String) -> Unit) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES, true)
        }

        recognitionListener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}

            override fun onError(error: Int) {
                listener("Error: $error")
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                matches?.firstOrNull()?.let { userInput->
                    Log.d("SpeechRecognition", "User Input: $userInput")

                    CoroutineScope(Dispatchers.IO).launch {
                        val processedText = nlpProcessor.processText(userInput)
                        withContext(Dispatchers.Main) {
                            listener(processedText)
                        }
                    }

                }
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        }

        speechRecognizer?.setRecognitionListener(recognitionListener!!)
        speechRecognizer?.startListening(intent)
    }

    override fun stopListening() {
        speechRecognizer?.stopListening()
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
}
