package com.github.kentvu.t9vietnamese.model

import com.github.kentvu.t9vietnamese.T9Engine

abstract class View(t9: T9Engine.Output) {

    val candidates: Candidates

    interface Candidates {
        fun has(word: String): Boolean
        class CandidatesImpl: Candidates {
            private val candidates = setOf<String>()
            override fun has(word: String): Boolean {
                return candidates.contains(word)
            }

        }
    }
}
