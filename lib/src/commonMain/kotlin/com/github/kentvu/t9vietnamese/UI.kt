package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.model.CandidateSet

interface UI {
    fun subscribeEvents(block: (UIEvent) -> Unit)
    fun update(event: UpdateEvent)

    sealed class UpdateEvent {
        object Initialized : UpdateEvent()
        object Close : UpdateEvent()
        object SelectNextCandidate : UpdateEvent()
        object Confirm : UpdateEvent()

        class NewCandidates(val candidates: CandidateSet) : UpdateEvent()
    }
    //class DefaultUI: UI {}
}