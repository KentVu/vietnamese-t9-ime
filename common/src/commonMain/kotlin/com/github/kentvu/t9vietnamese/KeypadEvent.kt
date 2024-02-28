package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.lib.AppEvent
import com.github.kentvu.t9vietnamese.model.Key

sealed class KeypadEvent: AppEvent {
    data class KeyPress(val key: Key): KeypadEvent()

    object CloseRequest : KeypadEvent()
}
