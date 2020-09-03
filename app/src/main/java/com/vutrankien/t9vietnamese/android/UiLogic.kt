package com.vutrankien.t9vietnamese.android

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Extraction of common code shared between [MainActivity] and the [T9Vietnamese] IME service.
 */
interface UiLogic {
    fun updateCandidates(candidates: Collection<String>)
    fun clearCandidates()
    fun initializeCandidatesView(recyclerView: RecyclerView)
    fun nextCandidate()

    class DefaultUiLogic: UiLogic {
        private val wordListAdapter = WordListAdapter()

        override fun initializeCandidatesView(recyclerView: RecyclerView) {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(recyclerView.context, RecyclerView.HORIZONTAL, false)
                adapter = wordListAdapter
            }
        }

        override fun updateCandidates(candidates: Collection<String>) {
            wordListAdapter.update(candidates)
        }

        override fun nextCandidate() {
            wordListAdapter.selectNext()
        }

        override fun clearCandidates() {
            wordListAdapter.clear()
        }
    }
}
