package com.github.kentvu.t9vietnamese.ui

import com.github.kentvu.t9vietnamese.model.Candidate
import com.github.kentvu.t9vietnamese.model.CandidateSet

data class UIState(
    val initialized: Boolean,
    val candidates: CandidateSet,
    val selectedCandidate: Int = 0
) {
    fun advanceSelectedCandidate(): UIState {
        return copy(selectedCandidate = selectedCandidate+1)
    }

    constructor() : this(false, CandidateSet(),)

}
