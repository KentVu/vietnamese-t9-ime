package com.github.kentvu.t9vietnamese.model

import kotlinx.coroutines.flow.StateFlow

interface View {

    val candidates: StateFlow<Candidates>

    interface Candidates {
        fun has(word: String): Boolean
        class CandidatesImpl(private val candidates: Set<String> = setOf()) : Candidates {
            override fun has(word: String): Boolean {
                return candidates.contains(word)
            }

        }
    }
}
