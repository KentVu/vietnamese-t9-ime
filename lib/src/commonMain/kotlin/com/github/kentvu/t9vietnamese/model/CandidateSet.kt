package com.github.kentvu.t9vietnamese.model


class CandidateSet private constructor(private val candidates: List<Candidate>) {

    companion object {
        fun from(candidates: LinkedHashSet<String>) =
            CandidateSet(candidates.toList().map { Candidate(it) })
    }
    //constructor(candidates: Set<String>) :
    //        this(candidates.map { Candidate(it) }.toSet())

    constructor() : this(listOf<Candidate>())

    fun forEach(action: (Candidate) -> Unit): Unit =
        candidates.forEachIndexed { i, cand ->
            action(cand)
        }

    fun forEachIndexed(action: (index: Int, Candidate) -> Unit) {
        candidates.forEachIndexed(action)
    }

    operator fun get(i: Int): Candidate = candidates[i]
}
