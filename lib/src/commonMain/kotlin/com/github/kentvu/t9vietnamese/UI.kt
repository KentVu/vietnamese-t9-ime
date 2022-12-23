package com.github.kentvu.t9vietnamese

interface UI {
    fun subscribeEvents(block: (UIEvent) -> Unit)
    fun update(event: UpdateEvent)

    enum class UpdateEvent {
        Initialized
    }
    //class DefaultUI: UI {}
}