package com.github.kentvu.t9vietnamese

interface UI {
    fun subscribeEvents(block: (UIEvent) -> Unit)
    fun update(event: UpdateEvent)

    sealed class UpdateEvent {
        object Initialized : UpdateEvent()
        class NewCandidates(val candidates: Set<String>) : UpdateEvent()
    }
    //class DefaultUI: UI {}
}