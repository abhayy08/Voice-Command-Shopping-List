package com.abhay.voicecommandshoppinglist.domain.use_cases

import com.abhay.voicecommandshoppinglist.domain.model.SpeechRecognitionService
import javax.inject.Inject
import com.abhay.voicecommandshoppinglist.domain.util.Result

class SpeechRecognitionUseCase @Inject constructor(private val repository: SpeechRecognitionService) {
    fun startListening(listener: (Result<String>) -> Unit) {
        try {
            repository.startListening { result ->
                when (result) {
                    is Result.Success -> {
                        val processedText = result.data ?: ""
                        if (processedText.isBlank()) {
                            listener(Result.Error("No speech detected"))
                        } else {
                            listener(Result.Success(processedText))
                        }
                    }
                    is Result.Error -> listener(result)
                }
            }
        } catch (e: Exception) {
            listener(Result.Error("Failed to start speech recognition: ${e.message}"))
        }
    }

    fun stopListening(): Result<Unit> {
        return try {
            repository.stopListening()
        } catch (e: Exception) {
            Result.Error("Failed to stop speech recognition: ${e.message}")
        }
    }
}
