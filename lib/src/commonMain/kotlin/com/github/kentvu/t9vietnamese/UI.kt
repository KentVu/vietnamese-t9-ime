package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.lib.AppEvent
import com.github.kentvu.t9vietnamese.lib.InputConnection
import com.github.kentvu.t9vietnamese.model.Candidate
import com.github.kentvu.t9vietnamese.model.CandidateSelection

interface UI {
    val inputConnection: InputConnection
    fun subscribeKeypadEvents(block: (KeypadEvent) -> Unit)
    fun update(event: UpdateEvent)

    sealed class UpdateEvent: AppEvent {
        object Initialized : UpdateEvent()
        object Close : UpdateEvent()
        //object
        // SelectNextCandidate : UpdateEvent()

        class UpdateCandidates(val candidates: CandidateSelection) : UpdateEvent()
    }
    //class DefaultUI: UI {}
}