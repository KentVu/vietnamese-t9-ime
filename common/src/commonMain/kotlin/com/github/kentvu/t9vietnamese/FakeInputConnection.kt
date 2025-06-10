package com.github.kentvu.t9vietnamese

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.github.kentvu.lib.logging.Logger
import com.github.kentvu.t9vietnamese.lib.InputSystemConnection

/** Fake InputConnection for demonstrating this IM's functions. */
class FakeInputConnection(): InputSystemConnection {
    val confirmedTextState = mutableStateOf("")
    var confirmedText by confirmedTextState
    override fun commitText(text: String) {
        confirmedText = confirmedText + text
    }

    override fun deleteSurroundingText(beforeLength: Int, afterLength: Int) {
        confirmedText = confirmedText.dropLast(1)
    }

    override fun performEditorAction() {
        log.info("TODO(performEditorAction)")
    }
    companion object {
        private val log = Logger.tag("DefaultInputConnection")
    }
}