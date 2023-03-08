package com.github.kentvu.t9vietnamese.model


class CandidateSet private constructor(private val candidates: Set<Candidate>) {

    companion object {
        fun from(candidates: Set<String>) =
            CandidateSet(candidates.map { Candidate(it) }.toSet())
    }
    //constructor(candidates: Set<String>) :
    //        this(candidates.map { Candidate(it) }.toSet())

    constructor() : this(setOf<Candidate>())

    fun forEach(action: (Candidate) -> Unit): Unit =
        candidates.forEachIndexed { i, cand ->
            action(cand)
        }

    fun forEachIndexed(action: (index: Int, Candidate) -> Unit) {
        candidates.forEachIndexed(action)
    }
}
