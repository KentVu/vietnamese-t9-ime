package com.github.kentvu.t9vietnamese.model

sealed class T9AppEvent {
    data class UpdateCandidates(
        val candidates: Set<String>
    ): T9AppEvent()
}
