package com.example.codevoice.stt

import com.intellij.openapi.diagnostic.Logger
import kotlin.concurrent.thread

/**
 * Dummy STT service implementation for testing purposes.
 * Returns predefined text instead of actual speech recognition.
 */
class DummyISTTService : ISTTService {

    companion object {
        private val LOG = Logger.getInstance(DummyISTTService::class.java)
    }

    private var listening = false
    private var workerThread: Thread? = null

    // Predefined text samples to simulate speech recognition
    private val sampleTexts = listOf(
        "Hello world",
        "This is a test",
        "I am dictating text",
        "Kotlin is awesome",
        "JetBrains IDE integration",
        "Speech to text works"
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

        LOG.info("Starting dummy STT service")
        listening = true

        workerThread = thread(start = true) {
            LOG.info("Worker thread started: ${Thread.currentThread().name}")
            try {
                while (listening && !Thread.currentThread().isInterrupted) {
                    LOG.debug("Worker thread loop iteration - waiting 2 seconds")
                    Thread.sleep(2000)

                    if (!listening) {
                        LOG.debug("Listening stopped during sleep - breaking loop")
                        break
                    }

                    val text = sampleTexts[currentIndex % sampleTexts.size]
                    LOG.debug("Generated sample text [index: $currentIndex]: '$text'")
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
                }
                LOG.info("Worker thread main loop exited normally")
            } catch (e: InterruptedException) {
                LOG.info("Worker thread interrupted: ${e.message}")
                // Thread was interrupted, clean exit
            } catch (e: Exception) {
                LOG.error("Unexpected error in worker thread", e)
            } finally {
                listening = false
                LOG.info("Worker thread cleanup completed - listening set to false")
            }
        }
    }

    override fun stopListening() {
        LOG.info("stopListening() called")

        if (!listening) {
            LOG.debug("stopListening() called but not currently listening")
        }

        listening = false
        LOG.debug("Set listening flag to false")

        workerThread?.let { thread ->
            LOG.debug("Interrupting worker thread: ${thread.name}")
            thread.interrupt()
            LOG.debug("Worker thread interrupted")
        } ?: LOG.debug("No worker thread to interrupt")

        workerThread = null
        LOG.info("stopListening() completed - worker thread reference cleared")
    }

    override fun isListening(): Boolean {
        val result = listening
        LOG.debug("isListening() called - returning: $result")
        return result
    }
}