package com.github.kentvu.t9vietnamese

interface View {

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
