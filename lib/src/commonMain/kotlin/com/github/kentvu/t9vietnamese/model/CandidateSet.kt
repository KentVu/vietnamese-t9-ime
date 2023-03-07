package com.github.kentvu.t9vietnamese.model


class CandidateSet private constructor(private val candidates: Set<Candidate>) {
    private var selected: Int = 0

    companion object {
        fun from(candidates: Set<String>) =
            CandidateSet(candidates.map { Candidate(it) }.toSet())
    }
    //constructor(candidates: Set<String>) :
    //        this(candidates.map { Candidate(it) }.toSet())

    constructor() : this(setOf<Candidate>())

    fun forEach(action: (Candidate, Boolean) -> Unit): Unit =
        candidates.forEachIndexed { i, cand ->
            action(cand, selected == i)
        }
}
