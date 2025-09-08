package com.example.codevoice.actions

import com.example.codevoice.stt.DummyISTTService
import com.example.codevoice.stt.ISTTService
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager

/**
 * Action to toggle speech-to-text dictation in the editor
 */
class ToggleDictationAction : AnAction()
{
    companion object {
        private val LOG = Logger.getInstance(ToggleDictationAction::class.java)
    }

    private val sttService: ISTTService = DummyISTTService()
    private var lastInsertedLength = 0
    private var lastCaretOffset = -1

    override fun getActionUpdateThread(): ActionUpdateThread
    {
        return ActionUpdateThread.EDT
    }

    override fun update(e: AnActionEvent) {
        super.update(e)

        val isListening = sttService.isListening()
        LOG.debug("update() called - isListening: $isListening")

        val presentation = e.presentation
        if (isListening) {
            presentation.text = "Stop Dictation"
            presentation.description = "Stop speech-to-text dictation - currently listening"
            LOG.debug("UI updated to show listening state")
        } else {
            presentation.text = "Start Dictation"
            presentation.description = "Start speech-to-text dictation"
            LOG.debug("UI updated to show inactive state")
        }
    }

    override fun actionPerformed(e: AnActionEvent)
    {
        LOG.info("actionPerformed() called")

        val project = e.project
        if (project == null) {
            LOG.warn("No project available - aborting action")
            return
        }
        LOG.debug("Project: ${project.name}")

        val editor = FileEditorManager.getInstance(project).selectedTextEditor
        if (editor == null) {
            LOG.warn("No selected text editor available - aborting action")
            return
        }
        LOG.debug("Editor available, document length: ${editor.document.textLength}")

        val isCurrentlyListening = sttService.isListening()
        LOG.info("Current listening state: $isCurrentlyListening")

        if (isCurrentlyListening)
        {
            LOG.info("Stopping dictation")
            stopDictation()
        } else
        {
            LOG.info("Starting dictation")
            startDictation(editor)
        }
    }

    private fun stopDictation()
    {
        LOG.info("stopDictation() called")
        sttService.stopListening()
        resetState()
        LOG.info("Dictation stopped and state reset")
    }

    private fun startDictation(editor: Editor)
    {
        LOG.info("startDictation() called")

        lastCaretOffset = editor.caretModel.currentCaret.offset
        LOG.debug("Initial caret offset: $lastCaretOffset")

        sttService.startListening(
            onInterimResult = { transcript ->
                LOG.debug("Received interim result: '$transcript'")
                handleInterimResult(editor, transcript)
            },
            onFinalResult = { transcript ->
                LOG.debug("Received final result: '$transcript'")
                handleFinalResult(editor, transcript)
            }
        )

        LOG.info("STT service started, callbacks registered")
    }

    private fun handleInterimResult(editor: Editor, transcript: String)
    {
        LOG.debug("handleInterimResult() called with transcript: '$transcript'")

        invokeLater {
            WriteCommandAction.runWriteCommandAction(editor.project) {
                val caret = editor.caretModel.currentCaret
                val currentOffset = caret.offset

                LOG.debug("Current caret offset: $currentOffset, last stored offset: $lastCaretOffset, last inserted length: $lastInsertedLength")

                validateAndUpdateCaretPosition(currentOffset)
                removePreviousInterimText(editor, currentOffset)
                insertTranscript(editor, currentOffset, transcript, isInterim = true)

                LOG.debug("Interim result handling completed")
            }
        }
    }

    private fun handleFinalResult(editor: Editor, transcript: String)
    {
        LOG.debug("handleFinalResult() called with transcript: '$transcript'")

        invokeLater {
            WriteCommandAction.runWriteCommandAction(editor.project) {
                val caret = editor.caretModel.currentCaret
                val currentOffset = caret.offset

                LOG.debug("Final result - current caret offset: $currentOffset")

                removePreviousInterimText(editor, currentOffset)
                insertTranscript(editor, currentOffset, transcript, isInterim = false)
                resetState()

                LOG.info("Final result handled and state reset")
            }
        }
    }

    private fun validateAndUpdateCaretPosition(currentOffset: Int)
    {
        LOG.debug("validateAndUpdateCaretPosition() - current: $currentOffset, expected: ${lastCaretOffset + lastInsertedLength}")

        if (lastCaretOffset != -1 && currentOffset != lastCaretOffset + lastInsertedLength)
        {
            LOG.warn("Caret position mismatch detected - user may have moved cursor. Resetting state.")
            LOG.debug("Expected offset: ${lastCaretOffset + lastInsertedLength}, actual: $currentOffset")
            resetState()
            lastCaretOffset = currentOffset
            LOG.debug("State reset and new caret offset set: $currentOffset")
        }
    }

    private fun removePreviousInterimText(editor: Editor, caretOffset: Int)
    {
        LOG.debug("removePreviousInterimText() - caretOffset: $caretOffset, lastInsertedLength: $lastInsertedLength")

        if (lastInsertedLength > 0)
        {
            val start = caretOffset - lastInsertedLength
            LOG.debug("Attempting to remove text from $start to $caretOffset")

            if (isValidDocumentRange(editor, start, caretOffset))
            {
                val textToRemove = editor.document.getText(com.intellij.openapi.util.TextRange(start, caretOffset))
                LOG.debug("Removing interim text: '$textToRemove'")
                editor.document.deleteString(start, caretOffset)
                LOG.debug("Interim text removed successfully")
            } else {
                LOG.warn("Invalid document range for removal: start=$start, end=$caretOffset, docLength=${editor.document.textLength}")
            }
        } else {
            LOG.debug("No previous interim text to remove")
        }
    }

    private fun insertTranscript(
        editor: Editor,
        caretOffset: Int,
        transcript: String,
        isInterim: Boolean
    )
    {
        val resultType = if (isInterim) "interim" else "final"
        LOG.debug("insertTranscript() - $resultType result: '$transcript' at offset $caretOffset")

        if (transcript.isNotEmpty())
        {
            editor.document.insertString(caretOffset, transcript)
            LOG.debug("Text inserted successfully")

            if (isInterim)
            {
                lastInsertedLength = transcript.length
                lastCaretOffset = caretOffset
                LOG.debug("Updated state - lastInsertedLength: $lastInsertedLength, lastCaretOffset: $lastCaretOffset")
            } else {
                LOG.debug("Final result - not updating tracking state")
            }
        } else if (isInterim)
        {
            lastInsertedLength = 0
            LOG.debug("Empty interim transcript - set lastInsertedLength to 0")
        } else {
            LOG.debug("Empty $resultType transcript - no action taken")
        }
    }

    private fun isValidDocumentRange(editor: Editor, start: Int, end: Int): Boolean
    {
        val docLength = editor.document.textLength
        val isValid = start >= 0 && start <= docLength && end <= docLength && start <= end

        LOG.debug("isValidDocumentRange() - start: $start, end: $end, docLength: $docLength, valid: $isValid")

        return isValid
    }

    private fun resetState()
    {
        LOG.debug("resetState() called - clearing lastInsertedLength and lastCaretOffset")
        val oldLength = lastInsertedLength
        val oldOffset = lastCaretOffset

        lastInsertedLength = 0
        lastCaretOffset = -1

        LOG.debug("State reset - was: length=$oldLength, offset=$oldOffset; now: length=${0}, offset=$lastCaretOffset")
    }
}