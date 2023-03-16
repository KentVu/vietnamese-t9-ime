package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.model.Candidate
import com.github.kentvu.t9vietnamese.model.CandidateSelection

interface UI {
    fun subscribeEvents(block: (UIEvent) -> Unit)
    fun update(event: UpdateEvent)

    sealed class UpdateEvent {
        object Initialized : UpdateEvent()
        object Close : UpdateEvent()
        //object
        // SelectNextCandidate : UpdateEvent()

        class UpdateCandidates(val candidates: CandidateSelection) : UpdateEvent()
        class Confirm(val selectedCandidate: Candidate) : UpdateEvent()
    }
    //class DefaultUI: UI {}
}