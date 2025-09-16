package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.lib.AppEvent
import com.github.kentvu.t9vietnamese.model.Action
import com.github.kentvu.t9vietnamese.model.EditorInfo

sealed class KeypadEvent: AppEvent {
    data class KeyPress(
        val action: Action,
    ): KeypadEvent()

    data class CandidateSelect(val candidateId: Int) : KeypadEvent() {}
    data class InputViewStart(val editorInfo: EditorInfo) : KeypadEvent() {}
    data object ShowReportClick : KeypadEvent()
    data object DismissReportClick : KeypadEvent()

    data object CloseRequest : KeypadEvent()
}
