package com.abhay.voicecommandshoppinglist.domain.use_cases

import com.abhay.voicecommandshoppinglist.domain.model.SpeechRecognitionService
import javax.inject.Inject

class SpeechRecognitionUseCase @Inject constructor(private val repository: SpeechRecognitionService) {
    fun startListening(listener: (String) -> Unit) = repository.startListening(listener)
    fun stopListening() = repository.stopListening()
}
