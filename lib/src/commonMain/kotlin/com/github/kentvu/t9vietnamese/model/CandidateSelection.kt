package com.github.kentvu.t9vietnamese.model


class CandidateSelection(
    private val candidates: List<Candidate>,
    private val selectedCandidateId: Int = 0
) {

    companion object {
        fun from(candidates: LinkedHashSet<String>) =
            CandidateSelection(candidates.toList().map { Candidate(it) })
    }
    //constructor(candidates: Set<String>) :
    //        this(candidates.map { Candidate(it) }.toSet())

    constructor() : this(listOf<Candidate>())

    val selectedCandidate: Candidate
        get() = candidates[selectedCandidateId]

    fun forEach(action: (Candidate) -> Unit): Unit {
        candidates.forEach { cand -> action(cand) }
    }

    fun forEachIndexed(action: (index: Int, Candidate) -> Unit) {
        candidates.forEachIndexed(action)
    }

    operator fun get(i: Int): Candidate = candidates[i]

    fun advanceSelectedCandidate(): CandidateSelection {
        return CandidateSelection(candidates, selectedCandidateId + 1)
    }
}
