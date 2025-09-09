package com.example.codevoice.stt

import com.intellij.openapi.diagnostic.Logger
import kotlin.concurrent.thread
import kotlin.math.abs
import javax.sound.sampled.*

/**
 * Dummy STT service implementation for testing purposes.
 * Uses real microphone input to detect speech events and outputs
 * predefined text when audio levels exceed a threshold.
 */
class DummyISTTService : ISTTService {

    companion object {
        private val LOG = Logger.getInstance(DummyISTTService::class.java)
        private const val SAMPLE_RATE = 16000f
        private const val SAMPLE_SIZE_IN_BITS = 16
        private const val CHANNELS = 1
        private const val SIGNED = true
        private const val BIG_ENDIAN = false
        private const val BUFFER_SIZE = 4096
        private const val AUDIO_THRESHOLD = 1000.0 // Minimum audio level to consider as speech
        private const val CHECK_INTERVAL_MS = 100L // Check audio levels every 100ms
    }

    private var listening = false
    private var workerThread: Thread? = null
    private var microphone: TargetDataLine? = null

    // Predefined text samples to simulate speech recognition
    private val sampleTexts = listOf(
        "Hello world",
        "This is a test",
        "I am dictating text",
        "Kotlin is awesome",
        "JetBrains IDE integration",
        "Speech to text works",
        "function main",
        "class MyClass",
        "val result equals",
        "println hello"
    )

    private var currentIndex = 0

    override fun startListening(
        onInterimResult: (String) -> Unit,
        onFinalResult: (String) -> Unit
    ) {
        LOG.info("startListening() called")

        if (listening) {
            LOG.warn("startListening() called but already listening - ignoring")
            return
        }

        try {
            // Set up audio format and microphone
            val format = AudioFormat(SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, CHANNELS, SIGNED, BIG_ENDIAN)
            val info = DataLine.Info(TargetDataLine::class.java, format)
            
            if (!AudioSystem.isLineSupported(info)) {
                LOG.error("Audio line not supported")
                return
            }
            
            microphone = AudioSystem.getLine(info) as TargetDataLine
            microphone!!.open(format)
            microphone!!.start()
            
            LOG.info("Starting microphone-based STT service - will detect speech via audio levels")
            listening = true

            workerThread = thread(start = true) {
                LOG.info("Worker thread started: ${Thread.currentThread().name}")
                val buffer = ByteArray(BUFFER_SIZE)
                
                try {
                    while (listening && !Thread.currentThread().isInterrupted) {
                        if (!listening) {
                            LOG.debug("Listening stopped - breaking loop")
                            break
                        }

                        // Read audio data from microphone
                        val bytesRead = microphone!!.read(buffer, 0, buffer.size)
                        
                        if (bytesRead > 0) {
                            // Calculate audio level (RMS)
                            val audioLevel = calculateAudioLevel(buffer, bytesRead)
                            LOG.debug("Audio level: $audioLevel (threshold: $AUDIO_THRESHOLD)")

                            // Check if audio level exceeds speech threshold
                            if (audioLevel > AUDIO_THRESHOLD) {
                                val text = sampleTexts[currentIndex % sampleTexts.size]
                                LOG.info("Speech detected! Audio level: $audioLevel, Generated sample text [index: $currentIndex]: '$text'")
                                currentIndex++

                                // Simulate interim result first
                                LOG.debug("Sending interim result: '$text'")
                                onInterimResult(text)

                                Thread.sleep(500)

                                if (!listening) {
                                    LOG.debug("Listening stopped during interim pause - breaking loop")
                                    break
                                }

                                // Then send final result
                                LOG.debug("Sending final result: '$text'")
                                onFinalResult(text)
                                
                                // Pause briefly to avoid multiple detections for the same speech
                                Thread.sleep(1000)
                            }
                        }
                        
                        Thread.sleep(CHECK_INTERVAL_MS)
                    }
                    LOG.info("Worker thread main loop exited normally")
                } catch (e: InterruptedException) {
                    LOG.info("Worker thread interrupted: ${e.message}")
                    // Thread was interrupted, clean exit
                } catch (e: Exception) {
                    LOG.error("Unexpected error in worker thread", e)
                } finally {
                    microphone?.stop()
                    microphone?.close()
                    microphone = null
                    listening = false
                    LOG.info("Worker thread cleanup completed - microphone closed, listening set to false")
                }
            }
        } catch (e: Exception) {
            LOG.error("Failed to initialize microphone", e)
            listening = false
        }
    }

    /**
     * Calculate the RMS (Root Mean Square) audio level from the buffer
     */
    private fun calculateAudioLevel(buffer: ByteArray, bytesRead: Int): Double {
        var sum = 0.0
        val samples = bytesRead / 2 // 16-bit samples = 2 bytes per sample
        
        for (i in 0 until samples * 2 step 2) {
            if (i + 1 < bytesRead) {
                // Convert 2 bytes to 16-bit signed integer (little-endian)
                val sample = (buffer[i].toInt() and 0xff) or ((buffer[i + 1].toInt() and 0xff) shl 8)
                val signedSample = if (sample > 32767) sample - 65536 else sample
                sum += signedSample * signedSample
            }
        }
        
        return if (samples > 0) kotlin.math.sqrt(sum / samples) else 0.0
    }

    override fun stopListening() {
        LOG.info("stopListening() called")

        if (!listening) {
            LOG.debug("stopListening() called but not currently listening")
        }

        listening = false
        LOG.debug("Set listening flag to false")

        // Stop and close microphone
        microphone?.let { mic ->
            LOG.debug("Stopping and closing microphone")
            try {
                mic.stop()
                mic.close()
            } catch (e: Exception) {
                LOG.warn("Error closing microphone", e)
            }
        } ?: LOG.debug("No microphone to close")
        microphone = null

        workerThread?.let { thread ->
            LOG.debug("Interrupting worker thread: ${thread.name}")
            thread.interrupt()
            LOG.debug("Worker thread interrupted")
        } ?: LOG.debug("No worker thread to interrupt")

        workerThread = null
        LOG.info("stopListening() completed - microphone closed, worker thread reference cleared")
    }

    override fun isListening(): Boolean {
        val result = listening
        LOG.debug("isListening() called - returning: $result")
        return result
    }
}