package com.github.kentvu.t9vietnamese.model

import com.github.kentvu.t9vietnamese.lib.AppEvent

sealed class T9AppEvent: AppEvent {
    data class UpdateCandidates(
        val candidates: Set<String>
    ): T9AppEvent()
}
