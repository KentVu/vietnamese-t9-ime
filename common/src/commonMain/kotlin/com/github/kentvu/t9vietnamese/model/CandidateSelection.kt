package com.github.kentvu.t9vietnamese.model


class CandidateSelection(
    private val candidates: List<Candidate>,
    val selectedCandidateId: Int = 0
): Iterable<Candidate> by candidates {

    companion object {
        fun from(candidates: List<String>, selectedCandidateId: Int = 0): CandidateSelection =
            CandidateSelection(candidates.map { Candidate(it) }, selectedCandidateId)

        fun CandidateSelection.forEach(action: (Candidate) -> Unit) {
            candidates.forEach(action)
        }

        fun forEachIndexed(candidateSelection: CandidateSelection, action: (Int, Candidate) -> Unit) {
            candidateSelection.candidates.forEachIndexed(action)
        }
    }
    //constructor(candidates: Set<String>) :
    //        this(candidates.map { Candidate(it) }.toSet())

    constructor() : this(listOf<Candidate>())

    val selectedCandidate: Candidate
        get() = candidates[selectedCandidateId]

    operator fun get(i: Int): Candidate = candidates[i]

    fun advanceSelectedCandidate(): CandidateSelection {
        return CandidateSelection(
            candidates,
            if (selectedCandidateId == candidates.lastIndex)
                0
            else selectedCandidateId + 1
        )
    }

    fun isNotEmpty() = candidates.isNotEmpty()
    fun select(id: Int): CandidateSelection {
        return CandidateSelection(candidates, id)
    }

    fun lastIndex(): Int {
        return candidates.lastIndex
    }
}
