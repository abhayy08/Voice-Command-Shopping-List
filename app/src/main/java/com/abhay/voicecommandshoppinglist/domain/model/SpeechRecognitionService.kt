package com.abhay.voicecommandshoppinglist.domain.model

interface SpeechRecognitionService {
    fun startListening(listener: (String) -> Unit)
    fun stopListening()
}
