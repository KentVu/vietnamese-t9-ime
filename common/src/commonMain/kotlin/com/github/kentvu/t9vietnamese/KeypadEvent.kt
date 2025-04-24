package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.lib.AppEvent
import com.github.kentvu.t9vietnamese.model.Action

sealed class KeypadEvent: AppEvent {
    data class KeyPress(
        val action: Action,
    ): KeypadEvent()

    data class CandidateSelect(val candidateId: Int) : KeypadEvent() {}

    object CloseRequest : KeypadEvent()
}
