package com.github.kentvu.t9vietnamese.model

import kotlinx.coroutines.flow.StateFlow

interface View {

    val candidates: StateFlow<Candidates>

    class Candidates(private val candidates: Set<String> = setOf()) {
        fun has(word: String): Boolean {
            return candidates.contains(word)
        }

        fun asSet(): Set<String> {
            return candidates
        }
    }
}
