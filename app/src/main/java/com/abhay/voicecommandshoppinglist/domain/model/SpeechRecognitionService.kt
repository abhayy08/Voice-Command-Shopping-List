package com.abhay.voicecommandshoppinglist.domain.model

import com.abhay.voicecommandshoppinglist.domain.util.Result

interface SpeechRecognitionService {
    fun startListening(listener: (Result<String>) -> Unit)
    fun stopListening(): Result<Unit>
}