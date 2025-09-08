package com.example.codevoice.stt

/**
 * Interface for Speech-to-Text services
 */
interface ISTTService {
    /**
     * Start listening for speech input
     * @param onInterimResult callback for interim (non-final) transcription results
     * @param onFinalResult callback for final transcription results
     */
    fun startListening(
        onInterimResult: (String) -> Unit,
        onFinalResult: (String) -> Unit
    )
    
    /**
     * Stop listening and clean up resources
     */
    fun stopListening()
    
    /**
     * Check if the service is currently listening
     */
    fun isListening(): Boolean
}