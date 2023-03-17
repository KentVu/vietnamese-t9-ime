package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.model.Key

sealed class UIEvent {
    data class KeyPress(val key: Key): UIEvent()

    object CloseRequest : UIEvent()
}
