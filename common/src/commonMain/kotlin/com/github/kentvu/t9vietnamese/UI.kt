package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.model.EditorInfo

interface UI {
    /** Notify IM service events to domain layer. */
    fun onStartInputView(info: EditorInfo): Boolean
}